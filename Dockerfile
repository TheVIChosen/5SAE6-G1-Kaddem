FROM openjdk:11-jdk-slim
EXPOSE 8082
ADD target/kaddem-0.0.1-SNAPSHOT.jar kaddem.jar
ENTRYPOINT ["java", "-jar", "/kaddem.jar"]
