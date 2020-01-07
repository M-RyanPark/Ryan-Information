FROM maven:3.3-jdk-8-onbuild

FROM openjdk:8-jdk-alpine
EXPOSE 8080
COPY --from=0 /usr/src/app/target/information-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]