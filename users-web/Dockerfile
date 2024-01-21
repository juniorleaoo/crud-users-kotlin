FROM openjdk:21-jdk

ARG JAR_FILE=build/libs/*.jar
COPY ./users-web/build/libs/users-web-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]