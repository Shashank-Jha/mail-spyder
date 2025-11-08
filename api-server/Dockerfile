FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/api-server-1.0.0-runner.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
