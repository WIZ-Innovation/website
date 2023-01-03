FROM openjdk:11-jdk
COPY vertx-base-1.0.jar /app.jar
ADD config /config
ADD libs /libs
CMD ["java", "-jar", "-Dlog4j2.configurationFile=config/log4j2.yml -Dfile.encoding=UTF-8", "app.jar"]