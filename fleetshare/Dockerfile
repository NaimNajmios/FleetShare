# ==========================================
# Stage 1: Build the application using Maven
# ==========================================
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy the Maven wrapper and pom.xml first to cache dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make the wrapper executable and download dependencies (this caches the layer)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code and build the application
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ==========================================
# Stage 2: Create the lightweight production image
# ==========================================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create the uploads directory for persistent storage
RUN mkdir -p /app/uploads
VOLUME /app/uploads

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port (Railway uses the PORT environment variable)
EXPOSE 8080

# Run the application using the production profile
ENTRYPOINT ["java", "-Dserver.port=${PORT:8080}", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
