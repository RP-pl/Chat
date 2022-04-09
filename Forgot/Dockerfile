FROM ubuntu:latest
COPY ./forgot_password_service.py /home
RUN apt-get update
RUN apt-get install python3.8 -y
RUN apt-get install python3-pip -y
RUN pip3 install pymongo
RUN pip3 install redis
RUN pip3 install Flask
ENTRYPOINT python3 /home/forgot_password_service.py
EXPOSE 8084