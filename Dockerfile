# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code into the container & build the application
COPY src ./src
RUN  mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/CICD-0.0.1-SNAPSHOT.jar .

# Expose the application port
EXPOSE 8080

# Set the command to run the application
ENTRYPOINT ["java", "-jar", "/app/CICD-0.0.1-SNAPSHOT.jar"]
