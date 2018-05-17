#!/bin/bash
gradle clean build

#java -jar build/libs/koi-1.0.0.jar -n project_plugin --project-path ~/Desktop/for_test/ --plugin-language grooy
#java -jar build/libs/koi-1.0.0.jar -n project_plugin --project-path ~/Desktop/for_test/ --plugin-language groovy
java -jar build/libs/koi-1.0.3.jar -n project_plugin --project-path ~/Desktop/for_test/ --plugin-language groovy

