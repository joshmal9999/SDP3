# Use OpenJDK 21 as the parent image
FROM eclipse-temurin:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy Gradle Wrapper and project files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts
COPY src src

# Ensure gradlew has execution permission
RUN chmod +x gradlew

# Compile the Java application
RUN ./gradlew build

# Run the application in headless mode
CMD ["java", "-Djava.awt.headless=true", "-jar", "build/libs/Invaders-SDP-1.0-SNAPSHOT.jar"]

