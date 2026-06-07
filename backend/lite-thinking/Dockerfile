FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /workspace

COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src

RUN chmod +x mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8686

ENV SERVER_PORT=8686

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
