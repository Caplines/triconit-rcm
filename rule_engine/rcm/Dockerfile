# Multi-stage build for RCM Spring Boot backend (WAR)
# Build context: rule_engine/ directory (set via docker-compose context: .)
#
# Stage 1 — Build Angular frontend (maven-war-plugin bundles it into the WAR)
FROM node:18-alpine AS frontend-build

WORKDIR /app

# Install dependencies (using --legacy-peer-deps for Angular 15 compatibility)
COPY rcm-client-app/package*.json ./
RUN npm install --legacy-peer-deps

# Build production bundle
COPY rcm-client-app/ .
RUN npm run build -- --configuration=production


# Stage 2 — Build Spring Boot WAR
FROM eclipse-temurin:8-jdk AS build

WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Copy pom.xml first for layer caching
COPY rcm/pom.xml .

# Copy Angular dist to the path expected by maven-war-plugin:
#   pom.xml uses ${basedir}/../rcm-client-app/dist/rcm-client-app
#   ${basedir} = /app  →  target path = /rcm-client-app/dist/rcm-client-app
COPY --from=frontend-build /app/dist/rcm-client-app /rcm-client-app/dist/rcm-client-app

# Copy source code
COPY rcm/src ./src

# Build the WAR (skip tests)
RUN mvn package -DskipTests


# Stage 3 — Runtime
FROM eclipse-temurin:8-jre

WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the executable WAR from build stage
COPY --from=build /app/target/rcm.war /app/rcm.war

# Create directories for persistent app data (matched to application-prod.properties paths)
RUN mkdir -p /opt/project/tricon/files \
    /opt/project/tricon/log \
    /opt/project/attachments \
    /opt/project/pdf \
    /opt/project/xml

# Expose the application port
EXPOSE 8081

# Spring profile (overridable via docker-compose environment)
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app/rcm.war"]
