#!/usr/bin/env bash
# ****************************************************************************************
#  [脚本标题描述] 
#  version    : @version@
#  author     : @author@
#  date       : @date@
#
#  [功能]:
#
#  [执行流程]:
#
#  [传参解析]:
#
#  [环境配置]:
#
# ****************************************************************************************
#!/bin/bash
#############
# 创建基本的Java环境的插件
# 创建后直接Idea导入即可，同步数据
#
# 参数：$1 project_name
#############

#
ARTIFACT_ID=plugin-demo
#
VERSION=1.0.0
# 插件类名
PLUGIN_CLASS_NAME=GreetingPlugin
# 插件任务类名
TASK_CLASS_NAME=Greeting

# 创建项目目录结构
function createPluginProject() {
  mkdir ${1}
  mkdir -p ${1}/buildSrc/src/main/java/${2//./\/}
  mkdir -p ${PROJECT_NAME}/buildSrc/src/main/resources/META-INF/gradle-plugins

  # Plugin Class
  echo "
  package ${2};

  import org.gradle.api.Plugin;
  import org.gradle.api.Project;

  public class ${PLUGIN_CLASS_NAME} implements Plugin<Project> {
      public void apply(Project project) {
          project.getTasks().create(\"hello\", ${TASK_CLASS_NAME}.class, (task) -> {
              task.setMessage(\"Hello\");
              task.setRecipient(\"World\");
          });
      }
  }
  " > ${1}/buildSrc/src/main/java/${2//./\/}/${PLUGIN_CLASS_NAME}.java

  # Task Class
  echo "
  package ${2};

  import org.gradle.api.DefaultTask;
  import org.gradle.api.tasks.TaskAction;

  public class ${TASK_CLASS_NAME} extends DefaultTask {
      private String message;
      private String recipient;

      public String getMessage() { return message; }
      public void setMessage(String message) { this.message = message; }

      public String getRecipient() { return recipient; }
      public void setRecipient(String recipient) { this.recipient = recipient; }

      @TaskAction
      void sayGreeting() {
          System.out.printf(\"%s, %s!\n\", getMessage(), getRecipient());
      }
  }
  " > ${1}/buildSrc/src/main/java/${2//./\/}/${TASK_CLASS_NAME}.java

  echo "
  apply plugin: 'groovy'
  apply plugin: 'maven'

  dependencies {
      compile gradleApi()
      compile localGroovy()
  }

  uploadArchives {
      repositories {
          mavenDeployer {
            pom.groupId = '"${2}"'
            pom.artifactId = '"${ARTIFACT_ID}"'
            pom.project {
                description = 'Description what you think to how to do.'
            }

            pom.version = '"${VERSION}"'
            repository(url: uri('repo'))
          }
      }
  }
  " > ${1}/buildSrc/build.gradle

  # 插件名声明
  echo "implementation-class=${PACKAGE_NAME}.${PLUGIN_CLASS_NAME}" > ${PROJECT_NAME}/buildSrc/src/main/resources/META-INF/gradle-plugins/${PACKAGE_NAME}.demo.properties
}

# 创建项目demo
function createDemo() {
  mkdir -p ${1}/app/src/main/java/${2//./\/}

  echo "
  apply plugin: 'java'
  apply plugin: '"${2}.demo"'

  [compileJava,compileTestJava,javadoc]*.options*.encoding = 'UTF-8'

  archivesBaseName=\"plugin-demo\"
  version='"${VERSION}"'

  jar {
    manifest {
      attributes 'Implementation-Title': 'Gradle jar file example',
                  'Implementation-Version': version,
                  'Main-Class': '"${PACKAGE_NAME}.Main"'
    }
    from { configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } }
  }
  " > ${1}/app/build.gradle

  echo "
  package ${2};

  public class Main {
    public static void main(String[] args){
      System.out.println(\"Hello world!\");
    }
  }
  " > ${1}/app/src/main/java/${2//./\/}/Main.java
}

# 输入的项目名称
if [ -z $1 ] ; then
  read -p "Please type project name [demo]:" project_name
  PROJECT_NAME=${project_name:-demo}
else
  PROJECT_NAME=$1
fi

# 输入包名, 包名和插件名相同
read -p "Please type package name [org.example.greeting]:" package_name
PACKAGE_NAME=${package_name:-org.example.greeting}

# 创建插件项目
createPluginProject ${PROJECT_NAME} ${PACKAGE_NAME}
# 创建Demo
createDemo ${PROJECT_NAME} ${PACKAGE_NAME}

echo "
buildscript {
    repositories {
        maven{
            url uri('buildSrc/repo')
        }
    }
    dependencies {
        classpath '"${PACKAGE_NAME}:${ARTIFACT_ID}:${VERSION}"'
    }
}

repositories{
    maven{
        url '"\$nexusUrl/content/groups/public/"'
    }
}
" > ${PROJECT_NAME}/build.gradle

echo "
include ':app'
" > ${PROJECT_NAME}/settings.gradle

echo "
nexusUrl=http\://your_nexus_server
" > ${PROJECT_NAME}/gradle.properties

cd ${PROJECT_NAME} > /dev/null
echo "
#########################################
#  Do worked at ${PROJECT_NAME}
#########################################
"
cd buildSrc > /dev/null
gradle clean build upload
cd - > /dev/null

cd app > /dev/null
gradle clean build
gradle hello

echo "
#########################################
#  Do valid jar can executable.
#########################################
"
java -jar build/libs/plugin-demo-1.0.0.jar
cd - > /dev/null

cd ..
