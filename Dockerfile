FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY SCRS ./SCRS
WORKDIR /app/SCRS
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/SCRS/target/SCRS-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
