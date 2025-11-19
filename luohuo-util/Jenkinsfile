#!groovy
pipeline {
    agent any

    stages {
        stage('maven 本地编译luohuo-util模块') {
            steps {
                echo "maven 本地编译luohuo-util模块"
                sh "pwd"
                sh 'mvn clean ${MAVEN_COMMAND} -T8 -Dmaven.compile.fork=true -Dmaven.test.skip=true'
            }
        }
    }

}


