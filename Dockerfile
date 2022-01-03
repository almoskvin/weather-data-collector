FROM openjdk:17
ADD target/weather-data-collector.jar weather-data-collector.jar
ENTRYPOINT ["java", "-jar", "weather-data-collector.jar"]
EXPOSE 8080