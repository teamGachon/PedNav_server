FROM openjdk:17-jdk-slim
WORKDIR /app
COPY audar-server.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
