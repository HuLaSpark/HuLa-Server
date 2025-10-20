#!/bin/bash

MODULER=$1
DIRECTORY=$2
PROFILES=$3
ACTION=$4

# linux中项目根目录
BASE_PATH=/home/jenkins/work/workspace/luohuo-cloud

if [ "$ACTION" = "" ]; then
    echo -e "\033[0;31m 未输入操作名 \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
    exit 1
fi

if [ "$MODULER" = "" ]; then
    echo -e "\033[0;31m 未输入应用名 \033[0m"
    exit 1
fi

if [ "$PROFILES" = "" ]; then
    PROFILES="prod"
fi

case $MODULER in
  "luohuo-ai-server")
    JAVA_OPT="-server -Xms1024M -Xmx1024M -Xss256k -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=256M -XX:+UseG1GC"
    ;;
  "luohuo-system-server")
    JAVA_OPT="-server -Xms1024M -Xmx1024M -Xss256k -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=256M -XX:+UseG1GC"
    ;;
  "luohuo-oauth-server")
      JAVA_OPT="-server -Xms1024M -Xmx1024M -Xss256k -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=256M -XX:+UseG1GC"
      ;;
  "luohuo-gateway-server")
      JAVA_OPT="-server -Xms1600M -Xmx2048M -Xss256k -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=256M -XX:+UseG1GC"
      ;;
  "luohuo-base-server")
    JAVA_OPT="-server -Xms1024M -Xmx1024M -Xss256k -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=256M -XX:+UseG1GC"
    ;;
  "luohuo-ws-server")
     JAVA_OPT="-server -Xms1024M -Xmx1024M -Xss256k -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=256M -XX:+UseG1GC"
      ;;
  "luohuo-im-server")
       JAVA_OPT="-server -Xms1680M -Xmx2048M -Xss256k -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=256M -XX:+UseG1GC"
        ;;
 *)
    echo "module 未配置内存: $MODULER, 使用默认配置..."
    JAVA_OPT="-server -Xms1024M -Xmx1024M -Xss256k -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=256M -XX:+UseG1GC"
    ;;
esac

JAVA_OPT="$JAVA_OPT -Dspring.profiles.active=$PROFILES"
echo "启动 [$MODULER] 服务真实配置 ====>>> $JAVA_OPT"

LOG_DIR="$BASE_PATH/logs"
LOG_FILE="$LOG_DIR/$MODULER.log"

if [ ! -d "$LOG_DIR" ]; then
    mkdir -p $LOG_DIR
fi

start() {
    count=$(ps -ef | grep java | grep $MODULER | grep -v grep | wc -l)
    if [ $count -ne 0 ]; then
        echo "$MODULER is already running..."
    else
        echo "Starting $MODULER..."
        JENKINS_NODE_COOKIE=dontKillMe nohup java -jar $JAVA_OPT $BASE_PATH/$DIRECTORY/$MODULER/target/$MODULER.jar > $LOG_FILE 2>&1 &
        sleep 5
        count=$(ps -ef | grep java | grep $MODULER | grep -v grep | wc -l)
        if [ $count -ne 0 ]; then
            echo "Start $MODULER success... logs in $LOG_FILE"
        else
            echo "Start $MODULER failed. Check logs in $LOG_FILE"
        fi
    fi
}

stop() {
    echo "Stopping $MODULER..."
    pid=$(ps -ef | grep java | grep $MODULER | grep -v grep | awk '{print $2}')
    if [ -n "$pid" ]; then
        kill $pid
        sleep 2
        count=$(ps -ef | grep java | grep $MODULER | grep -v grep | wc -l)
        if [ $count -eq 0 ]; then
            echo "Stop $MODULER success"
        else
            echo "Force stopping $MODULER..."
            kill -9 $pid
            echo "Stop $MODULER success"
        fi
    else
        echo "$MODULER is not running..."
    fi
}

restart() {
    stop
    sleep 2
    start
}

status() {
    count=$(ps -ef | grep java | grep $MODULER | grep -v grep | wc -l)
    if [ $count -ne 0 ]; then
        echo "$MODULER is running..."
    else
        echo "$MODULER is not running..."
    fi
}

case $ACTION in
    start)
        start;;
    stop)
        stop;;
    restart)
        restart;;
    status)
        status;;
    *)
        echo -e "\033[0;31m Usage: \033[0m  \033[0;34m sh $0 {start|stop|restart|status} {SpringBootJarName} \033[0m"
        echo -e "\033[0;31m Example: \033[0m"
        echo -e "\033[0;33m sh $0 start esmart-test.jar \033[0m"
esac
