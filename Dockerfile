# Using AdoptOpenJDK 21 as the base image
#FROM ubuntu:latest
FROM eclipse-temurin:21

LABEL authors="danij"

WORKDIR /app

COPY target/dani-imdb-0.0.1-SNAPSHOT.jar /app/dani-imdb-0.0.1-SNAPSHOT.jar

# Specify the command to run your application
CMD ["java", "-jar", "dani-imdb-0.0.1-SNAPSHOT.jar"]