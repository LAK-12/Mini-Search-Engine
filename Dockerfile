# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# ---- Run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
# copy the fat jar produced by the assembly plugin
COPY --from=build /app/target/*-jar-with-dependencies.jar app.jar
# Render sets PORT; your App reads it via getPort()
EXPOSE 8080
CMD ["java","-jar","/app/app.jar"]
