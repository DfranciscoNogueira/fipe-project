# === Stage 1: build ===
FROM maven:3.9.9-eclipse-temurin-17 as build
WORKDIR /app
COPY . /app
RUN mvn -DskipTests package

# === Stage 2: runtime ===
FROM eclipse-temurin:17-jre
WORKDIR /work
COPY --from=build /app/target/*-runner.jar /work/app.jar
EXPOSE 8080
ENV QUARKUS_PROFILE=docker
ENTRYPOINT ["java","-jar","/work/app.jar"]
