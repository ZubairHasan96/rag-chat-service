# build stage
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# copy only sources required for build (do not assume .mvn or mvnw exist)
COPY pom.xml .
COPY src ./src

# run maven package (skip tests for faster image builds)
RUN mvn -B -f pom.xml -DskipTests package

# runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# copy the built jar (matches any produced jar name)
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]