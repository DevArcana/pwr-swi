FROM amazoncorretto:20 as build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw
RUN ./mvnw verify --fail-never

COPY src src
RUN ./mvnw install -DskipTests

FROM amazoncorretto:20
RUN mkdir -p /opt/app
COPY --from=build /app/target/*.jar /opt/app/backend.jar

ENTRYPOINT ["java","-jar", "/opt/app/backend.jar"]
