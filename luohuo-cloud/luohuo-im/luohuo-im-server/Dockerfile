FROM eclipse-temurin:25-jre
MAINTAINER qianqian xxxx@qq.com

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

ARG JAR_FILE
COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["java", "--sun-misc-unsafe-memory-access=allow", "-Xmx512m", "-Djava.security.egd=file:/dev/./urandom", "-Ddruid.mysql.usePingMethod=false", "-jar", "/app.jar"]
CMD ["--spring.profiles.active=docker"]
