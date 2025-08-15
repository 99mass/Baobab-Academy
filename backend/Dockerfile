# Stage 1: Build
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build avec Java 21
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

RUN addgroup -g 1001 -S baobab && adduser -S baobab -u 1001 -G baobab

RUN mkdir -p /app/uploads /app/logs && chown -R baobab:baobab /app

COPY --from=build /app/target/*.jar app.jar

USER baobab

ENV JAVA_OPTS="-Xmx1024m -Xms512m -XX:+UseG1GC -XX:+UseContainerSupport"

# Exposer le port
EXPOSE 8080

# Health check intégré
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]