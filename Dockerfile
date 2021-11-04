FROM 192.168.7.238:5000/images/openjdk-8-jdk-alpine:latest
USER root
VOLUME /tmp
RUN mkdir /data/
COPY ./data/* /data
RUN chmod -R 775 /data
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]
