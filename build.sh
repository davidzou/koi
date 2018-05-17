#!/usr/bin/env bash
# ****************************************************************************************
#  [脚本标题描述] 
#  version    : 1.0.0
#  author     : DavidZou
#  date       : 2018.05.13
#
#  [功能]:     用于打包执行
#
#  [执行流程]:
#
#  [传参解析]:
#
#  [环境配置]:
#
# ****************************************************************************************

version=1.0.3

# 修改build.gradle中定义的版本
sed -ig "s/version=.*/version=\"${version}\"/g" build.gradle
rm build.gradleg
# 修改Kohaku中定义的版本
sed -ig "s/def VERSION =.*/def VERSION = '${version}'/g" src/main/groovy/com/wonderingwall/koi/Kohaku.groovy
rm src/main/groovy/com/wonderingwall/koi/Kohaku.groovyg

# 编译
gradle --no-daemon clean build
# 发布
gradle distTar

#sha256=`shasum -a 256 tarball/${version}/koi-${version}.tar | awk '{print $1}'`
## 更新homebrew-koi
#sed -ig "s/1.0.2\/koi-1.0.2.tar?raw=true/$version\/koi-$version.tar?raw=true/g"
#sed -ig "s/sha256 '.*'/sha256 '$sha256'/g"
#sed -ig "s/version '.*'/version '$version'/g" ../homebrew-koi/Formula/koi.rb



