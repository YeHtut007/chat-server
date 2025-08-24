# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -DskipTests=true dependency:go-offline
COPY src ./src
RUN ./mvnw -DskipTests=true clean package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
ENV PORT=8080
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Dserver.port=${PORT}","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]
