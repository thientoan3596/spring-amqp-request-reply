FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/gateway.jar gateway.jar
EXPOSE 8080
CMD ["java", "-jar", "gateway.jar"]
