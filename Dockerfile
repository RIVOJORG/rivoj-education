FROM openjdk:17
EXPOSE 8443
ARG JAR_FILE=target/education-0.0.1-SNAPSHOT.jar


ADD ${JAR_FILE} education


COPY keystore.p12 /app/keystore.p12


ENTRYPOINT ["java","-jar", "education"]
