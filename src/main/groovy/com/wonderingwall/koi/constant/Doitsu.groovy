package com.wonderingwall.koi.constant


interface Doitsu {
    /**
     * 用于配置settings.gradle中 include包含的项目名称
     *
     * {@link Sanke#DEFAULT_APP_NAME}
     */
    String BINDING_KEY_PLUGIN_MODULE_APP_NAME = "plugin_module_app_name"

    /**
     * 工程项目包名
     *
     * {@link Sanke#DEFAULT_PACKAGE_NAME}
     */
    String BINDING_KEY_PACKAGE_NAME = "package_name"
    /**
     * 插件名
     * <pre>
     *     apply plugin: 'plugin_id'
     * </pre>
     * {@link Sanke#DEFAULT_PLUGIN_ID}
     */
    String BINDING_KEY_PLUGIN_ID = "plugin_id"
    /**
     * 编译插件名称
     * <pre>
     *     apply plugin: 'java'
     * </pre>
     */
    String BINDING_KEY_COMPILE_PLUGIN_ID = "compile_plugin_id"
    /**
     * 插件类（入口）
     *
     * org.gradle.api.Plugin
     * {@link Sanke#DEFAULT_PLUGIN_MAIN_CLASS_NAME}
     */
    String BINDING_KEY_CLASS_NAME = "plugin_class_name"
    /**
     * 任务类（Task）
     *
     * org.gradle.api.DefaultTask
     * {@link Sanke#DEFAULT_PLUGIN_TASK_CLASS_NAME}
     */
    String BINDING_KEY_CLASS_TASK_NAME = "plugin_class_task_name"

    /**
     * 自定义参数
     */
    String BINDING_KEY_CLASS_EXTENSION_NAME = "plugin_class_extension_name"

    /**
     * class path plugin name what to deploy maven repo
     *
     * <pre>
     *     classpath 'group:artifact:version'
     * </pre>
     */
    String BINDING_KEY_POM_ARTIFACT_ID = "plugin_pom_artifact_id"
    /**
     * class path plugin name what to deploy maven repo. It always is package_name normal
     *
     * <pre>
     *     classpath 'group:artifact:version'
     * </pre>
     */
    String BINDING_KEY_POM_GROUP_ID = "plugin_pom_group_id"
}
