FROM eclipse-temurin:25-jdk-alpine as builder
LABEL author=knckj
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .
COPY lib/ lib/

RUN ./mvnw install:install-file \
    -Dfile=lib/program-ab.jar \
    -DgroupId=org.local \
    -DartifactId=programab \
    -Dversion=1.0.0-local \
    -Dpackaging=jar \
    -q

COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-jar", "app.jar"]
