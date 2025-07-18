# --- Stage 1: Build with Maven ---
FROM maven:3.9-eclipse-temurin-17 AS build
# Install Node.js & npm
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs
# Set work directory
WORKDIR /app

# Copy pom.xml and download dependencies first (for caching)
COPY pom.xml .
COPY .mvn .mvn
RUN mvn dependency:go-offline

# Now copy the full source
COPY . .

# Build the application
RUN mvn clean install -DskipTests

# --- Stage 2: Run the Spring Boot app ---
FROM eclipse-temurin:17-jdk-jammy

# Create app directory
WORKDIR /app

# Create a non-root user (optional but recommended)
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Copy built jar from previous stage
COPY --from=build /app/target/*.jar SpringAI.jar

# Use non-root user
USER spring

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "SpringAI.jar"]
