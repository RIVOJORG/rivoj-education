FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=target/education-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} education.jar
COPY keystore.p12 /app/keystore.p12
COPY ./fireBaseKeySDK.json /app/fireBaseKeySDK.json
ENTRYPOINT ["java", "-jar", "education.jar"]
