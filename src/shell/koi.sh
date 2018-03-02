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

BREW_HOME="/usr/local/Cellar/koi/@version@"

# brew install
java -jar "$BREW_HOME/koi-@version@.jar" $@
