FROM maven:3.9.8-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY ./stats/pom.xml ./stats/
COPY ./stats/statsDto/pom.xml ./stats/statsDto/
COPY ./stats/statsServer/pom.xml ./stats/statsServer/
COPY ./stats/statsClient/pom.xml ./stats/statsClient/

COPY ./stats/statsDto/src ./stats/statsDto/src
COPY ./stats/statsServer/src ./stats/statsServer/src
COPY ./stats/statsClient/src ./stats/statsClient/src


RUN mvn clean package -pl ./stats/statsClient -am -DskipTests

FROM openjdk:21-jdk-slim
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring
WORKDIR /app
COPY --from=builder /app/stats/statsClient/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]

