FROM ubuntu
COPY ./Email_microservice.py /home
RUN apt-get update
RUN apt-get install python3.8 -y
RUN apt-get install python3-pip -y
RUN pip3 install pymongo
RUN pip3 install redis
RUN pip3 install Flask
ENTRYPOINT python3 /home/main.py
EXPOSE 8082
