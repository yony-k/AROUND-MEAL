FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} around-meal.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "/around-meal.jar"]