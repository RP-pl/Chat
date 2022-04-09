FROM debian:latest
COPY ./Chat-1.0-SNAPSHOT.jar /home
RUN apt-get install -y openjdk-14-jdk
ENTRYPOINT java -jar /home/Chat-1.0-SNAPSHOT.jar
EXPOSE 8080