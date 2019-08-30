FROM gradle:5.4.1-jdk11 AS gradle
WORKDIR /spring-fu
COPY src src
COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts
RUN gradle --quiet bootJar

FROM openjdk:11.0.3-jdk-slim
WORKDIR /spring-fu
COPY --from=gradle /spring-fu/build/libs/spring-fu.jar app.jar
CMD ["java", "-server", "-XX:+UseNUMA", "-XX:+UseParallelGC", "-Dlogging.level.root=INFO", "-jar", "app.jar"]
