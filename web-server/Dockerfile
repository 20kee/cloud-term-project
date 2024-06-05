# OpenJDK 기반 이미지 사용
FROM openjdk:17-jdk-alpine

ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar" ]