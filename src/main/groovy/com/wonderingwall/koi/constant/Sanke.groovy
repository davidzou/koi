package com.wonderingwall.koi.constant

/**
 * The Default value
 */
interface Sanke {
    /** The gradle plugin source dir standard */
    String BUILDSRC_NAME = "buildSrc"
    /** Module that app demo dir name how to exec a plugin*/
    String DEFAULT_APP_NAME = "app"

    /** Default plugin project name which root dir */
    String DEFAULT_PROJECT_NAME = 'gradle_plugin_demo'
    /** Default project package name */
    String DEFAULT_PACKAGE_NAME = "com.wonderingwall.plugin.demo"
    /** Default plugin name that used like "apply plugin: 'plugin_id'" */
    String DEFAULT_PLUGIN_ID = "com.wonderingwall.plugin"
    /** Default plugin main class name that class extend org.gradle.api.Plugin */
    String DEFAULT_PLUGIN_MAIN_CLASS_NAME = "PluginDemo"
    /** Default plugin task class name that class extend org.gradle.api.DefaultTask */
    String DEFAULT_PLUGIN_TASK_CLASS_NAME = "PluginTask"

    String DEFAULT_PLUGIN_EXTENSTION_NAME = "PluginExtension"

    /** The gradle source dir standard by java language */
    String GRADLE_DIRECTORY_JAVA_SRC = "src/main/java"
    /** The gradle resource dir standard */
    String GRADLE_DIRECTORY_JAVA_RES = "src/main/res"
    /** The gradle library dir standard */
    String GRADLE_DIRECTORY_LIBS = "libs"
    /** The gradle plugin dir standard */
    String GRADLE_DIRECTORY_PLUGIN_RES = "src/main/resources/META-INF/gradle-plugins"
    /** The gradle source dir standard by groovy language */
    String GRADLE_DIRECTORY_GROOVY_SRC = "src/main/groovy"


    String DEFAULT_POM_ARTIFACT_ID = "demo"
    String DEFAULT_POM_GROUP_ID = "com.wonderingwall.plugin"
}