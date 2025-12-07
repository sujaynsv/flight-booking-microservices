# FROM maven:3.8.2-jdk-8 # for Java 8

FROM maven:3.8.5-openjdk-17


WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
