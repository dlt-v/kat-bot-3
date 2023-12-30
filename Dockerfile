FROM openjdk:17
LABEL authors="tmmmi"
COPY target/kat-bot.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]