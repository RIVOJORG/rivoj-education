FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=target/education-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} education.jar
COPY keystore.p12 /app/keystore.p12
COPY ./src/main/resources/fireBaseKeySDK.json /app/classes/fireBaseKeySDK.json
ENTRYPOINT ["java", "-jar", "education.jar"]
