# Multi-stage build for Spring Boot backend
# Using Eclipse Temurin (supports ARM64/Apple Silicon)
# Build context: repo root (rule-engine/), set via docker-compose.yml context: ..
FROM eclipse-temurin:8-jdk AS build

WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Copy pom.xml and lib folder (needed for custom htmlunit dependency)
COPY rule_engine/ruleengine/pom.xml .
COPY rule_engine/ruleengine/lib     ./lib

# First, run initialize phase to install custom htmlunit JAR from lib folder
RUN mvn initialize -B

# Copy source code
COPY rule_engine/ruleengine/src ./src

# Build the application
RUN mvn package -DskipTests

# Runtime stage - Using Eclipse Temurin JRE (supports ARM64/Apple Silicon)
FROM eclipse-temurin:8-jre

WORKDIR /app

# Install wget for healthchecks
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Copy the JAR from build stage
COPY --from=build /app/target/ruleengine.jar /app/ruleengine.jar

# Bake XSLT templates into the image at /opt/xslt (not overridden by any Docker volume)
COPY ["ivf Form", "/opt/xslt"]

# Create directories for other persistent app data
RUN mkdir -p /opt/project/tricon/files \
    /opt/project/tricon/log \
    /opt/project/tricon/client \
    /opt/project/tricon/chrome \
    /tmp/ruleengine/xml

# Expose the port (will be 8080 in the container)
EXPOSE 8080

# Set active profile via environment variable (can be overridden)
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
ENTRYPOINT ["java", "-jar", "/app/ruleengine.jar"]
