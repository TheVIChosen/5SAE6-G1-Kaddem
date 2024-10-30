FROM openjdk:17-jdk-slim
EXPOSE 8082
ADD target/your-app.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
