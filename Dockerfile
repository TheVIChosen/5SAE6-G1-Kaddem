FROM openjdk:17-jdk-alpine

WORKDIR /kaddem-app

# Copy the jar file from the target directory into the container
COPY target/kaddem-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port on which the app will run
#EXPOSE 8080
EXPOSE 8089

# Command to run the application
ENTRYPOINT ["java", "-jar", "./app.jar"]

