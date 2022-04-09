FROM ubuntu:latest
RUN apt-get update
RUN apt-get install mongodb -y
RUN mkdir -p /data/db
ENTRYPOINT mongod --bind_ip 0.0.0.0
EXPOSE 27017