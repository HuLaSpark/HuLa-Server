FROM openjdk:21
EXPOSE 9190
ADD hula-im-service-v1.5.0.jar  app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","-Djava.security.egd=file:/dev/./urandom","/app.jar","--spring.profiles.active=prod"]
