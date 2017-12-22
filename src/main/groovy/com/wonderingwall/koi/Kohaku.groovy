package com.wonderingwall.koi

import com.wonderingwall.koi.constant.Doitsu
import com.wonderingwall.koi.constant.Sanke
import groovy.text.GStringTemplateEngine

import java.util.spi.ResourceBundleControlProvider

/**
 *
 */
class Kohaku {
    /** 当前版本 */
    def VERSION = '1.0.0'
    /** 当前支持的插件语言 */
    def LANGUAGE = ['java', 'groovy', 'kotlin']

    ResourceBundle rb

    static void main(String[] args) {
        Kohaku main = new Kohaku()
        main.loadResource()
        main.help(args)
    }

    def loadResource() {
        rb = ResourceBundle.getBundle("string", Locale.default)
    }

    def help(String[] args) {
        def cli = new CliBuilder(usage: rb.getString("koi.help.usage"), header: rb.getString("koi.help.header"), footer: rb.getString("koi.help.footer"))
        cli.h(longOpt: 'help', rb.getString("koi.help.option.it"))
        cli.n(longOpt: 'project-name', argName: 'name', required: true, args: 1, rb.getString("koi.help.option.project.name"))
        cli._(longOpt: 'project-path', argName: 'path', args: 1, optionalArg: true, rb.getString("koi.help.option.project.path"))
        cli._(longOpt: 'project-app-name', argName: 'name', args: 1, optionalArg: true, rb.getString("koi.help.option.project.app.name"))
        cli.N(longOpt: 'package-name', rb.getString("koi.help.option.package.name"))
        cli._(longOpt: 'plugin-id', rb.getString("koi.help.option.plugin.id"))
        cli._(longOpt: 'plugin-class-name', rb.getString("koi.help.option.plugin.class.name"))
        cli._(longOpt: 'plugin-class-task-name', rb.getString("koi.help.option.plugin.class.task.name"))
        cli._(longOpt: 'plugin-class-extension-name', rb.getString("koi.help.option.plugin.class.extension.name"))
        cli._(longOpt: 'plugin-artifact-id', rb.getString("koi.help.option.plugin.artifact.id"))
        cli._(longOpt: 'plugin-group-id', rb.getString("koi.help.option.plugin.group.id"))
        cli._(longOpt: 'plugin-language', args: 1, rb.getString("koi.help.option.plugin.language"))
        cli.v(longOpt: 'version', rb.getString("koi.help.option.version"))

        // 无参时显示帮助
        if (args.size() == 0) { usage(cli) }
        // 处理参数
        args.find { param ->
            if ("-h" == param || "--help" == param) { usage(cli) }
            if ("-v" == param || "--version" == param) { version() }
        }
        def option = cli.parse(args)
        // 参数不合法，或者传参不正确直接返回
        if (!option) { return }
        // 显示帮助信息
        if (option.h) {usage(cli)}
        // 显示版本
        if (option.v) {version()}

        println option.'plugin-language'?:"java"
        assert LANGUAGE.contains(option.'plugin-language'?:"java")

        createProject(option.'project-path'?:'.', option.n?:Sanke.DEFAULT_PROJECT_NAME,
                [
                        (Doitsu.BINDING_KEY_PLUGIN_MODULE_APP_NAME): option.'project-app-name'?:Sanke.DEFAULT_APP_NAME,                // Demo项目名
                        (Doitsu.BINDING_KEY_PACKAGE_NAME)          : option.N?:Sanke.DEFAULT_PACKAGE_NAME,                                       // 包名
                        (Doitsu.BINDING_KEY_PLUGIN_ID)             : option.'plugin-id'?:Sanke.DEFAULT_PLUGIN_ID,                       // 插件名
                        (Doitsu.BINDING_KEY_CLASS_NAME)            : option.'plugin-class-name'?:Sanke.DEFAULT_PLUGIN_MAIN_CLASS_NAME,             // 插件主类
                        (Doitsu.BINDING_KEY_CLASS_TASK_NAME)       : option.'plugin-class-task-name'?:Sanke.DEFAULT_PLUGIN_TASK_CLASS_NAME,   // 任务类
                        (Doitsu.BINDING_KEY_POM_ARTIFACT_ID)       : option.'plugin-artifact-id'?:Sanke.DEFAULT_POM_ARTIFACT_ID,
                        (Doitsu.BINDING_KEY_POM_GROUP_ID) : option.''?:Sanke.DEFAULT_PACKAGE_NAME,
                ]
        )
    }

    def version() {
        println "version: ${rb.getString("project.name")} $VERSION"; System.exit(0)
    }
    def usage(CliBuilder cli) {
        cli.usage(); System.exit(0)
    }

    /**
     * To create gradle plugin project.
     * @param path          Project path
     * @param pathname      Project dir name
     * @param params        All custom binding_key how to set custom project.
     * @return  void
     */
    def createProject(String path, String pathname, Map params) {
        println "[createProject] -- path: $path , pathname: $pathname , params: $params"

        def root = new File(path, pathname)
        if (!root.exists()) {
            boolean successed = root.mkdir()
            println successed
            if (!successed) {
                println("mkdir '$path' error")
                return
            }
        }

        /* Gradle项目配置文件(全局设置) */
        readTemplate("/templates/settings.gradle.template", params, new File(root.getAbsolutePath(), "settings.gradle"))
        /* 构建文件内容（全局设置） */
        readTemplate("/templates/build.gradle.template", params, new File(root.getAbsolutePath(), "build.gradle"))
        /* 创建插件项目模块 */
        createPluginProjectModule(root.getAbsolutePath(), Sanke.BUILDSRC_NAME, params)
        createDemoProject(root.getAbsolutePath(), Sanke.DEFAULT_APP_NAME, params)

        /* 编译打包demo */
        runGradle(path, pathname, 'gradle', 'clean', 'build')
        runGradle(root.getAbsolutePath(), Sanke.DEFAULT_APP_NAME, 'gradle', 'hello')
    }

    /**
     *
     * @param path          Project path
     * @param pathname      Project dir name
     * @param params        All custom binding_key how to set custom project.
     * @return void
     */
    def createPluginProjectModule(String path, String pathname, Map<String, String> params) {
        /* 构建目录 */
        // 插件目录
        def buildSrc = new File(path, pathname)
        buildSrc.mkdir()

        // Java代码目录
        def javaSrc = new File(buildSrc.getAbsolutePath(), "${Sanke.GRADLE_DIRECTORY_JAVA_SRC}")
        javaSrc.mkdirs()
        def classSrc = new File(javaSrc.getAbsolutePath(), params[Doitsu.BINDING_KEY_PACKAGE_NAME].replace('.', '/'))
        classSrc.mkdirs()
        // 插件META-INF
        def metainf = new File(buildSrc.getAbsolutePath(), "${Sanke.GRADLE_DIRECTORY_PLUGIN_RES}")
        metainf.mkdirs()

        /* Plugin class */
        readTemplate("/templates/gradleplugin.class.main.template", params, new File(classSrc.getAbsolutePath(), (params[Doitsu.BINDING_KEY_CLASS_NAME] as String).plus(".java") ))
        /* 插件Task实现 */
        readTemplate("/templates/gradleplugin.class.task.template", params, new File(classSrc.getAbsolutePath(), (params[Doitsu.BINDING_KEY_CLASS_TASK_NAME] as String).plus(".java") ))
        /* 插件描述文件 */
        readTemplate("/templates/gradleplugin.meta-inf.properties.template", params, new File(metainf.getAbsolutePath(), (params[Doitsu.BINDING_KEY_PLUGIN_ID] as String).plus(".properties") ))
        /* build.gradle */
        readTemplate("/templates/gradleplugin.build.gradle.template", params, new File(buildSrc.getAbsolutePath(), "build.gradle"))

        runGradle(path, pathname, 'gradle', 'clean', 'build', 'upload')
    }

    def createDemoProject(String path, String pathname, Map<String, String> params) {
        /* 构建目录 */
        def app = new File(path, pathname)
        app.mkdir()
        // demo 应用（default Java）
        def javaSrc = new File(app.getAbsolutePath(), "${Sanke.GRADLE_DIRECTORY_JAVA_SRC}")
        javaSrc.mkdirs()
        def classSrc = new File(javaSrc.getAbsolutePath(), params[Doitsu.BINDING_KEY_PACKAGE_NAME].replace('.', '/'))
        classSrc.mkdirs()

        println "app.getAbsolutePath() + $app"
        // 类文件
        readTemplate("/templates/app.class.main.template", params, new File(classSrc.getAbsolutePath(), "Main".plus(".java")))
        // Gradle打包
        readTemplate("/templates/app.build.gradle.template", params, new File(app.getAbsolutePath(), "build.gradle"))
    }

    def runGradle(String path, String pathname, String... command) {
        ProcessBuilder builder = new ProcessBuilder()
        builder.directory(new File(path, pathname))
        builder.command(command)
        def proc = builder.start()
        def inputStream = new InputStreamReader(proc.in)
        BufferedReader bufferedReader = new BufferedReader(inputStream)
        while (true){
            String s = bufferedReader.readLine()
            if (s == null){
                break
            }
            println(s)
        }
    }

    def validJar(){
        def sout = new StringBuilder(), serr = new StringBuilder()
        def proc = "jar -jar app/build/libs/plugin-demo-1.0.0.jar".execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitFor()
        println '''To valid demo plugin can be run.'''
        println "out> $sout err> $serr"

    }


    def typeInformatrion() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
        print "Input:"
        def userInput = br.readLine()
        println "You entered: $userInput"
        return userInput
    }

    def readTemplate(String pathname, Map binding, File to) {
        def fLoc = new File(pathname)
        if (!fLoc.exists()) {
            def rTemplate = pathname
            if (rTemplate.startsWith('/')) {
                rTemplate = rTemplate - '/'
            }
            fLoc = new File(System.getProperty('use.dir', rTemplate))
            if (!fLoc.exists()) {
                fLoc = getClass().getResource(pathname)
            }
        }
        def tReader = fLoc?.newReader()
        if (tReader) {
            def engine = new GStringTemplateEngine()
            Writable writable = engine.createTemplate(tReader).make(binding)
            to.write(writable.toString())
        }
    }

}

