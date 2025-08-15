#!/bin/bash

source /etc/profile

# 模块 如：luohuo-authority-server
JAR_NAME="$1"
# 环境 如： dev test prod
PROFILES="$2"
# 动作 可选值：restart start stop status none
ACTION="$3"

# 工作空间, 此路径需要提现在服务器创建
WORKSPACE_HOME="/data_${PROFILES}"

cd ${WORKSPACE_HOME}


function validParam()
{
  if [ "${ACTION}" = "" ];
  then
      echo -e "\033[0;31m 未输入执行动作 -a \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
      exit 1
  fi
  if [ "${JAR_NAME}" = "" ];
  then
      echo -e "\033[0;31m 未输入应用名称 -m \033[0m"
      exit 1
  fi
  if [ "${PROFILES}" = "" ];
  then
      echo -e "\033[0;31m 未输入环境参数 -p \033[0m"
      exit 1
  fi
}

validParam

# 无需执行任何操作
if [ "${ACTION}" == "none" ]; then
  exit 1
fi

JAVA_OPT="-server -Xms256M -Xmx256M -Xss512k -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=512M -XX:+UseG1GC"
# 禁止jvm吞掉重复抛出的异常， 发送OOM时记录日志到指定文件
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${WORKSPACE_HOME}/logs/heap_dump_${JAR_NAME}.hprof"
JAVA_OPT="${JAVA_OPT} -Dspring.profiles.active=$PROFILES"
SKY_OPT="-javaagent:${WORKSPACE_HOME}/agent/skywalking-agent.jar -Dskywalking.agent.service_name=${JAR_NAME}"

function start()
{
	count=`ps -ef | grep java | grep $JAR_NAME | grep -v grep | wc -l`
	if [ $count != 0 ];then
		echo "$JAR_NAME is running..."
	else
		echo "Start $JAR_NAME success..."
		echo "nohup java -jar $JAVA_OPT ${WORKSPACE_HOME}/target/${JAR_NAME}.jar ${JAR_NAME} > /dev/null 2>&1 &"

		BUILD_ID=dontKillMe nohup java -jar $JAVA_OPT ${WORKSPACE_HOME}/target/${JAR_NAME}.jar ${JAR_NAME} > /dev/null 2>&1 &
#		BUILD_ID=dontKillMe nohup java ${SKY_OPT} -jar $JAVA_OPT ${WORKSPACE_HOME}/target/${JAR_NAME}.jar ${JAR_NAME} > /dev/null 2>&1 &
	fi
}

function stop()
{
	echo "Stop $JAR_NAME"
	pid=`ps -ef | grep java | grep $JAR_NAME | grep -v grep | awk '{print $2}'`
	count=`ps -ef | grep java | grep $JAR_NAME | grep -v grep | wc -l`

	if [ $count != 0 ];then
	    	kill $pid
    		count=`ps -ef | grep java | grep $JAR_NAME | grep -v grep | wc -l`

      		pid=`ps -ef | grep java | grep $JAR_NAME | grep -v grep | awk '{print $2}'`

      		kill -9 $pid
      		echo "Stop $JAR_NAME Success"
	fi
}

function restart()
{
	stop
	sleep 2
	start
}

function status()
{
    count=`ps -ef | grep java | grep $JAR_NAME | grep -v grep | wc -l`
    if [ $count != 0 ];then
        echo "$JAR_NAME is running..."
    else
        echo "$JAR_NAME is not running..."
    fi
}

mkdir -p ${WORKSPACE_HOME}/backups/${JAR_NAME}/
mkdir -p ${WORKSPACE_HOME}/target/
mkdir -p ${WORKSPACE_HOME}/logs/

echo "准备执行备份&删除&覆盖jar操作"

# 1, 备份
cd ${WORKSPACE_HOME}/backups/${JAR_NAME}/
TIME=`date "+%Y%m%d%H%M"`
cp -rf ${WORKSPACE_HOME}/target/${JAR_NAME}.jar ${WORKSPACE_HOME}/backups/${JAR_NAME}/${JAR_NAME}-${TIME}.jar

# 2, 删除正在运行的jar
rm -rf ${WORKSPACE_HOME}/target/${JAR_NAME}.jar

# 3, 移动新的jar到target
mv ${WORKSPACE_HOME}/temp_jar/${JAR_NAME}.jar ${WORKSPACE_HOME}/target/

cd ${WORKSPACE_HOME}/target/
echo "进入[${WORKSPACE_HOME}/target/]目录,准备执行: ${ACTION}"
ls -l ${WORKSPACE_HOME}/target/${JAR_NAME}.jar

# 4，执行动作
case ${ACTION} in
	start)
	start;;
	stop)
	stop;;
	restart)
	restart;;
	status)
	status;;
	*)
	echo -e "\033[0;31m Usage: \033[0m  \033[0;34m sh  $0  {start|stop|restart|status}  {SpringBootJarName} \033[0m
\033[0;31m Example: \033[0m
	  \033[0;33m sh  $0  start esmart-test.jar \033[0m"
esac

