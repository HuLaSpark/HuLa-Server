#!groovy
pipeline {
    agent any

    environment {
        // 根据项目或部署服务器 可能需要更改一次的变量
        JAR_NAME = "${SERVER_NAME}-server"

        // 推送时需要忽略的项目前缀
        removePrefix = "${SERVER_NAME}/${JAR_NAME}/target/"

        // 需要推送到服务器端的文件(jar)
        sourceFiles = "${SERVER_NAME}/${JAR_NAME}/target/${JAR_NAME}.jar"
    }

    stages {
        stage('替换环境参数') {
            steps {
                script {
                    // 工作空间
                    WORKSPACE_HOME = "src/main"

                    // 服务端执行的脚本 sh run.sh luohuo-gateway-server luohuo-gateway prod restart
                    EXEC_COMMAND = "bash -x -s < ${WORKSPACE_HOME}/bin/run.sh ${JAR_NAME} ${SERVER_NAME} ${PROFILES} ${ACTION}"
                    echo "您选择了如下参数："
                    echo "拉取分支： ${branch}, 打包命令：${MAVEN_COMMAND}, 运行环境参数：${PROFILES}，启动动作：${ACTION}"
                }
            }
        }

        stage('maven编译代码') {
            steps {
                script {
                    if ("${MAVEN_COMMAND}" != "none") {
                        sh "mvn clean ${MAVEN_COMMAND} -T2 -Dmaven.compile.fork=true -Dmaven.test.skip=true -P ${PROFILES}"
                        // cd /home/jenkins/work/workspace/cloud
                        // mvn clean install -T2 -Dmaven.compile.fork=true -Dmaven.test.skip=true -P prod
                    } else {
                        echo "无需编译项目 (适用于代码没有改动的场景)"
                    }
                }
            }
        }
    }
}