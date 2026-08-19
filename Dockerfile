FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Security: Run as a dedicated non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the pre-built JAR from the Jenkins workspace target folder
COPY target/*.jar app.jar

# Matches your application's configured port
EXPOSE 8080

# Production JVM container optimizations to prevent OOM kills
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]