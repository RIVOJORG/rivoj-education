FROM openjdk:17
EXPOSE 8080
ARG JAR_FILE=target/education-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} edcation
ENTRYPOINT ["java","-jar", "education"]