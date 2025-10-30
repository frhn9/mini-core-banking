FROM gradle:8.5-jdk21 AS build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY build.gradle settings.gradle gradlew $HOME/
COPY gradle $HOME/gradle
RUN gradle dependencies --no-daemon || return 0
COPY . $HOME
RUN gradle bootJar --no-daemon -x test

FROM openjdk:21-jdk
COPY --from=build /usr/app/build/libs/*.jar /app/mini-core-banking.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/mini-core-banking.jar"]