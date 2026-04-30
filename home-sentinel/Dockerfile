# 1. Build Stage
FROM gradle:8.10-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew bootJar --no-daemon -x test

# 2. Runtime Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /home/gradle/src/build/libs/app.jar .
ENTRYPOINT ["java", "-jar", "app.jar"]
