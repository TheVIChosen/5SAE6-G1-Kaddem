FROM openjdk:17-jdk-slim
EXPOSE 8082
ADD target/kaddem-1.0.jar kaddem.jar
ENTRYPOINT ["java", "-jar", "/kaddem.jar"]
