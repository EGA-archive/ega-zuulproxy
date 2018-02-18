#!/bin/bash
SERVICE_NAME=ZuulService
PATH_TO_JAR=/zuul-server-1.0.0.BUILD-SNAPSHOT.jar
PROCESSCNT=$(ps x | grep -v grep | grep -c "zuul-server-1.0.0.BUILD-SNAPSHOT.jar")
#PID=$(ps aux | grep "zuul-server-1.0.0.BUILD-SNAPSHOT.jar" | grep -v grep | awk '{print $2}')
if [ $PROCESSCNT == 0 ]; then
    echo "Starting $SERVICE_NAME ..."
    nohup java -jar $PATH_TO_JAR 2>> /dev/null >> /dev/null &
    echo "$SERVICE_NAME started ..."
#else
#    echo "$SERVICE_NAME is already running ..."
fi
