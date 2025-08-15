#!/bin/bash

# 设置默认参数
command="restart"

# 如果第四个参数被传递了，则使用传递的值
if [ ! -z "$4" ]; then
    command="$4"
fi

sh /home/jenkins/work/workspace/luohuo-cloud/src/main/bin/run.sh luohuo-oauth-server luohuo-oauth prod "$command"
