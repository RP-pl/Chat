FROM ubuntu:latest
RUN apt-get update
RUN apt-get install redis -y
ENTRYPOINT redis-server --protected-mode no
EXPOSE 6379