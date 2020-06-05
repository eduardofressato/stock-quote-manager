FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD

MAINTAINER Eduardo Fressato

COPY pom.xml /build/

COPY src /build/src/

WORKDIR /build/

RUN mvn package

FROM openjdk:8-jre-alpine

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/stock-quote-manager.jar /app/

ENTRYPOINT ["java", "-jar", "stock-quote-manager.jar"]
