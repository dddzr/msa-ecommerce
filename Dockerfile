FROM eclipse-temurin:17-jdk
COPY build/target/gateway.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
