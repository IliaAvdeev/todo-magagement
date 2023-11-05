FROM openjdk:21-jdk
LABEL authors="Ilia Avdeev"
MAINTAINER Ilia Avdeev
ADD target/ /app
RUN chmod -R o-rwx /app
CMD ["java", "-jar", "/app/todo-management-1.0.0-SNAPSHOT.jar"]
EXPOSE 8080