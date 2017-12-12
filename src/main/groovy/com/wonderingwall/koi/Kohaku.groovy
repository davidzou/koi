package com.wonderingwall.koi

import com.wonderingwall.koi.constant.Doitsu
import com.wonderingwall.koi.constant.Senke
import groovy.text.GStringTemplateEngine

/**
 *
 */
class Kohaku {

    static void main(String[] args) {
        Kohaku main = new Kohaku()
        main.help(args)
    }

    def help(String[] args) {
        def cli = new CliBuilder(usage: "koi [options] [targets]", header: "Options")
        cli.h(longOpt: 'help', "It is show this.")
        cli.n(longOpt: 'project-name', argName: 'name', required: true, args: 1, "工程项目名称")
        cli._(longOpt: 'project-path', argName: 'path', args: 1, optionalArg: true, "项目存放路径，默认为'.'命令执行的当前目录")
        cli._(longOpt: 'project-app-name', argName: 'name', args: 1, optionalArg: true, "项目中Demo名称，一般以app命名")
        cli.N(longOpt: 'package-name', "包名, 包含了路径MainClass的路径")
        cli._(longOpt: 'plugin-apply-name', "插件引用名称 apply plugin :'xxx' ")
        cli._(longOpt: 'plugin-class-name', "插件主类名")
        cli._(longOpt: 'plugin-class-task-name', "插件任务类名")
        cli._(longOpt: 'plugin-class-extension-name', "插件扩展类名")
        cli._(longOpt: 'plugin-artifact-id', "")
        cli._(longOpt: 'plugin-group-id', "")
        cli.v(longOpt: 'version', "版本号")

        // 无参时显示帮助
        if (args.size() == 0) { usage(cli) }
        // 处理参数
        args.find { param ->
            if ("-h" == param) { usage(cli) }
            if ("-v" == param) { version() }
        }
        def option = cli.parse(args)
        // 参数不合法，或者传参不正确直接返回
        if (!option) { return }
        // 显示帮助信息
        if (option.h) {usage(cli)}
        // 显示版本
        if (option.v) {version()}

        createProject(option.'project-path'?:'.', option.n?:Senke.DEFAULT_PROJECT_NAME,
                [
                        (Doitsu.BINDING_KEY_PLUGIN_MODULE_APP_NAME) : option.'project-app-name'?:Senke.DEFAULT_APP_NAME,                // Demo项目名
                        (Doitsu.BINDING_KEY_PACKAGE_NAME) : option.N?:Senke.DEFAULT_PACKAGE_NAME,                                       // 包名
                        (Doitsu.BINDING_KEY_PLUGIN_NAME) : option.'plugin-apply-name'?:Senke.DEFAULT_PLUGIN_NAME,                       // 插件名
                        (Doitsu.BINDING_KEY_CLASS_NAME) : option.'plugin-class-name'?:Senke.DEFAULT_PLUGIN_MAIN_CLASS_NAME,             // 插件主类
                        (Doitsu.BINDING_KEY_CLASS_TASK_NAME) : option.'plugin-class-task-name'?:Senke.DEFAULT_PLUGIN_TASK_CLASS_NAME,   // 任务类
                        (Doitsu.BINDING_KEY_POM_ARTIFACT_ID) : option.'plugin-artifact-id'?:'plugin',
                        (Doitsu.BINDING_KEY_POM_GROUP_ID) : option.''?:Senke.DEFAULT_PACKAGE_NAME,
                ]
        )
    }

    def version() {
        println '''version: koi 1.0.0'''; System.exit(0)
    }
    def usage(CliBuilder cli) {
        cli.usage(); System.exit(0)
    }

    def createProject(String path, String pathname, Map params) {
        println "[createProject] -- path: $path , pathnaem: $pathname , params: $params"

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
        createPluginProjectModule(root.getAbsolutePath(), Senke.BUILDSRC_NAME, params)
        createDemoProject(root.getAbsolutePath(), Senke.DEFAULT_APP_NAME, params)

        /* 编译打包demo */
        runGradle(path, pathname, 'gradle', 'clean', 'build')
        runGradle(root.getAbsolutePath(), Senke.DEFAULT_APP_NAME, 'gradle', 'hello')
    }

    def createPluginProjectModule(String path, String pathname, Map<String, String> params) {
        /* 构建目录 */
        // 插件目录
        def buildSrc = new File(path, pathname)
        buildSrc.mkdir()

        // Java代码目录
        def javaSrc = new File(buildSrc.getAbsolutePath(), "${Senke.GRADLE_DIRECTORY_JAVA_SRC}")
        javaSrc.mkdirs()
        def classSrc = new File(javaSrc.getAbsolutePath(), params[Doitsu.BINDING_KEY_PACKAGE_NAME].replace('.', '/'))
        classSrc.mkdirs()
        // 插件META-INF
        def metainf = new File(buildSrc.getAbsolutePath(), "${Senke.GRADLE_DIRECTORY_PLUGIN_RES}")
        metainf.mkdirs()

        /* Plugin class */
        readTemplate("/templates/gradleplugin.class.main.template", params, new File(classSrc.getAbsolutePath(), (params[Doitsu.BINDING_KEY_CLASS_NAME] as String).plus(".java") ))
        /* 插件Task实现 */
        readTemplate("/templates/gradleplugin.class.task.template", params, new File(classSrc.getAbsolutePath(), (params[Doitsu.BINDING_KEY_CLASS_TASK_NAME] as String).plus(".java") ))
        /* 插件描述文件 */
        readTemplate("/templates/gradleplugin.meta-inf.properties.template", params, new File(metainf.getAbsolutePath(), (params[Doitsu.BINDING_KEY_PLUGIN_NAME] as String).plus(".properties") ))
        /* build.gradle */
        readTemplate("/templates/gradleplugin.build.gradle.template", params, new File(buildSrc.getAbsolutePath(), "build.gradle"))

        runGradle(path, pathname, 'gradle', 'clean', 'build', 'upload')
    }

    def createDemoProject(String path, String pathname, Map<String, String> params) {
        /* 构建目录 */
        def app = new File(path, pathname)
        app.mkdir()
        // demo 应用（default Java）
        def javaSrc = new File(app.getAbsolutePath(), "${Senke.GRADLE_DIRECTORY_JAVA_SRC}")
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

