# Stage 1: Build JAR with Maven & Eclipse Temurin 21
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /workspace

# Copy Maven wrapper and POM first to leverage Docker layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B

# Copy source code and build application
COPY src src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Minimal Production JRE Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Run as dedicated unprivileged system user for container security
RUN groupadd -r landguard && useradd -r -g landguard -s /bin/false landguard

# Copy built artifact from builder
COPY --from=builder --chown=landguard:landguard /workspace/target/*.jar /app/app.jar

USER landguard

EXPOSE 8080

ENV PORT=8080 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dserver.port=${PORT} -jar /app/app.jar"]
