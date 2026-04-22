# Use Amazon Corretto JDK 17 as the base image for building
FROM amazoncorretto:17-alpine-jdk AS builder

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src src

# Build the application
RUN ./gradlew clean build -x test

FROM amazoncorretto:17-alpine

# Set working directory
WORKDIR /app

# Create a non-root user
RUN addgroup -g 1001 -S spring && \
    adduser -S spring -u 1001

# Copy the built jar from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Change ownership of the app directory to the spring user
RUN chown -R spring:spring /app

# Switch to the non-root user
USER spring

# Expose the port that the application runs on
EXPOSE 8080

# Set JVM options for better container performance
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
