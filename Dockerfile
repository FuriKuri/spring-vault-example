FROM maven:alpine
WORKDIR /usr/src/app
COPY . .
RUN mvn package

FROM openjdk:8-alpine
COPY --from=0 /usr/src/app/target/*.jar ./app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]