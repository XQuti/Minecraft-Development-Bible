# Multi-stage build for security and smaller image size
FROM eclipse-temurin:24-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY config config

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src src

# Build the application
RUN ./gradlew build -x test

# Production stage
FROM eclipse-temurin:24-jre-alpine

# Create non-root user for security (Alpine Linux)
RUN addgroup -S mdbuser && adduser -S mdbuser -G mdbuser

# Set working directory
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/build/libs/mdb-0.0.1-SNAPSHOT.jar app.jar

# Change ownership to non-root user
RUN chown -R mdbuser:mdbuser /app

# Switch to non-root user
USER mdbuser

# Expose port
EXPOSE 8080

# Install curl for health checks (Alpine Linux)
USER root
RUN apk add --no-cache curl
USER mdbuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application with JVM optimizations
CMD ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]