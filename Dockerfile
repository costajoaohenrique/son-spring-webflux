#FROM eclipse-temurin:11-jdk-alpine as build
FROM maven:3.8.4-eclipse-temurin-11-alpine as build

WORKDIR /usr/src/app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY . .

RUN mvn clean package \
    && cp target/*.jar app.jar


FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

RUN addgroup --system spring-group && adduser --system spring-user --ingroup spring-group

USER spring-user:spring-group

COPY --from=build "/usr/src/app/app.jar" .

EXPOSE 8080

ENTRYPOINT ["java","-Xmx512m","-Dserver.port=${PORT}","-jar","./app.jar"]