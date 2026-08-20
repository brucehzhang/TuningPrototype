FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src
RUN chmod +x gradlew
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY certs/global-bundle.pem /app/certs/global-bundle.pem
COPY certs/ap-southeast-2-bundle.pem /app/certs/ap-southeast-2-bundle.pem
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]