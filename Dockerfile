FROM openjdk:21
EXPOSE 9190
ADD hula-im-service-v2.6.8.jar  app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","-Djava.security.egd=file:/dev/./urandom","/app.jar"]
