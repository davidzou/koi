package com.wonderingwall.koi.constant

interface Doitsu {
    /**
     *
     */
    String BINDING_KEY_PACKAGE_NAME = "package_name"
    String BINDING_KEY_CLASS_NAME = "plugin_class_name"
    String BINDING_KEY_CLASS_TASK_NAME = "plugin_class_task_name"

    /**
     * 插件名
     */
    String BINDING_KEY_PLUGIN_NAME = "plugin_name"

    /**
     * 用于配置settings.gradle中 include包含的项目名称
     *
     * {@link Senke#DEFAULT_APP_NAME}
     */
    String BINDING_KEY_PLUGIN_MODULE_APP_NAME = "plugin_module_app_name"

    String BINDING_KEY_POM_ARTIFACT_ID = "plugin_pom_artifact_id"
    String BINDING_KEY_POM_GROUP_ID = "plugin_pom_group_id"

//    cli.n(longOpt: 'project-name', argName: 'name', required: true, args: 1, "工程项目名称")
//    cli._(longOpt: 'project-path', argName: 'path', args: 1, optionalArg: true, "项目存放路径，默认为'.'命令执行的当前目录")
//    cli._(longOpt: 'project-app-name', argName: 'name', args: 1, optionalArg: true, "项目中Demo名称，一般以app命名")
//    cli.N(longOpt: 'package-name', "包名, 包含了路径MainClass的路径")
//    cli._(longOpt: 'plugin-apply-name', "插件引用名称 apply plugin :'xxx' ")
//    cli._(longOpt: 'plugin-class-name', "插件主类名")
//    cli._(longOpt: 'plugin-class-task-name', "插件任务类名")
//    cli._(longOpt: 'plugin-class-extension-name', "插件扩展类名")
}
