FROM maven:3.8.3-openjdk-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

FROM amazoncorretto:17-alpine-jdk
COPY --from=build /app/target/*.jar /app/mancala-service.jar
ENTRYPOINT ["java", "-jar", "/app/mancala-service.jar"]
