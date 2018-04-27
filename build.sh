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
version=1.0.2

sed -ig "s/version=.*/version=\"${version}\"/g" build.gradle
rm build.gradleg

gradle --no-daemon clean build

gradle distTar

