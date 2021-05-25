import smtplib

from flask import Flask
from flask import request
from pymongo import MongoClient
import hashlib
from redis import Redis
app = Flask(__name__)
redis_db = Redis(host='172.16.238.102',port=6379)
mongo_db = MongoClient('172.16.238.101',27017).ChatDB.Users
@app.route("/",methods=['GET'])
def hello():
    return str(redis_db) + str(mongo_db)
@app.route("/send",methods=['GET'])
def send():
    username:str = request.headers['username']
    email:str = request.headers['email']
    hashfunc = hashlib.new('sha512_256')
    hashfunc.update(username.encode())
    hash = hashfunc.hexdigest()
    redis_db.set(hash,username)
    server = smtplib.SMTP_SSL('smtp.gmail.com', 465)
    server.ehlo()
    server.login("email", "pwd")
    server.sendmail("email", email, "Welcome to Chat!! \n To authorize your account enter in this link:\n"+'http://edz-web.tplinkdns.com/authorize/'+hash)
    server.close()
    return "OK"
@app.route("/authorize/<hash>",methods=['GET'])
def authorize(hash):
    username:str = redis_db.get(hash)
    print(str(username)[1:])
    redis_db.delete(hash)
    mongo_db.update_one({"username":str(username)[2:-1]},{"$set":{"enabled":True}})
    return "OK"
if __name__ == '__main__':
    app.run("0.0.0.0",8082)
