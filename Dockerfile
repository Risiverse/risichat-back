FROM maven:3.8.6-openjdk-18-slim AS build
COPY lib /lib
COPY src /src
COPY pom.xml /pom.xml
COPY .env /.env
RUN mvn test
RUN mvn clean compile assembly:single

FROM openjdk:18-alpine
COPY --from=build /target/risichat-back-jar-with-dependencies.jar /risichat-back.jar
COPY --from=build .env /.env
CMD ["java", "-jar", "/risichat-back.jar"]