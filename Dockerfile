FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar
COPY src/main/resources/db/migration /app/resources/db/migration


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]