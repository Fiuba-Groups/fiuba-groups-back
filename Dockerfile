# Stage 1: The Build Stage
# Use a Java 21 JDK image to build the app
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Copy the application source code
COPY src ./src

# Make the wrapper executable
RUN chmod +x ./gradlew

# Build the application and create the .jar file
# We skip tests here to make the docker build faster
RUN ./gradlew build -x test

# Stage 2: The Final Stage
# Use a lightweight Java 21 JRE image to run the app
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy ONLY the built .jar file from the 'builder' stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port your Spring app runs on
EXPOSE 8080

# This is the command that will run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
