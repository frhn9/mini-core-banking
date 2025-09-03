FROM maven:alpine AS build
ENV HOME=/usr/app
RUN "mkdir -p $HOME"
WORKDIR $HOME
COPY pom.xml $HOME
RUN mvn verify --fail-never -DskipTests
COPY . $HOME
RUN mvn package -DskipTests

FROM openjdk:21-jdk
COPY --from=build /usr/app/target/*.jar /app/example.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/example.jar"]