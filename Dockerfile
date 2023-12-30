FROM openjdk:17
WORKDIR /usr/src/app
COPY ./target/kat-bot-3-1.0-SNAPSHOT.jar /usr/src/app
ENTRYPOINT ["java", "-jar", "kat-bot-3-1.0-SNAPSHOT.jar"]
