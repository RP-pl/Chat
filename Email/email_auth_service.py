import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

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
    server.login("email", "passwd")
    msg = MIMEMultipart('alternative')
    msg['Subject'] = "Activate account"
    msg['From'] = 'email'
    msg['To'] = email
    markup = """
    <html>
        <head>
            <style>
                #h1{
                    font-wight:bold;
                    background-color:blue;
                    color:white;
                }
                .header-content{
                    display:flex;
                    font-family: Poppins,sans-serif;

                }
                .card{
                    display:block;
                    width:100%;
                }
                .header{
                    font-family: Poppins,sans-serif;
                    border-top-right-radius: 16px;
                    border-top-left-radius: 16px;
                    border: 1px solid #DCDCDC;
                    background-color:#1892ef;
                    color:white;
                    margin-top:0px;
                    padding-top:0px;
                }
                .footer{
                    margin-top:0px;
                    padding-top:0px;
                    border-bottom-right-radius: 16px;
                    border-bottom-left-radius: 16px;
                    border:1px solid #DCDCDC;
                    background-color:#1892ef;
                    font-family: Poppins,sans-serif;
                }
                .body{
                    border: 1px solid #DCDCDC;
                    background-color: #F5F5F5;
                    font-family: Poppins,sans-serif;
                    height:200px;
                }
                .center-h{
                    margin-top: auto;
                    margin-bottom: auto;
                    padding-top: 10px;
                    padding-bottom: 10px;
                }
                .center-v{
                    margin-left:auto;
                    margin-right:auto;
                    width:fit-content;
                    width:-moz-fit-content;
                }
                .button{
                    margin-top:auto;
                    margin-bottom:auto;
                    text-align: center;
                    width:100%;
                    display:inline-block;
                    color:white !important;
                    text-decoration:none;
                }
                .button-body{
                    background-color: #1892ef;
                    width:100%;
                    padding:10px
                }
            </style>
        </head>
        <body>
            <div class="card">
                <div class="header">
                    <div class="header-content">
                        <image class="center-h" width="30" height="30" src="data:image/png;base64,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AFFqBAAAiFIAAESpEQAAIEoBABClRgAAgCgFAECUGgEAAKIUAABRagQAAIhSAABEqREAACBKAQAQpUYAAIAoBQBAlBoBAACiFAAAUWoEAACIUgAARKkRAAAgSgEAEKVGAACAKAUAQJQaAQAAohQAAFFqBAAAiFIAAESpEQAAIEoBABClRgAAgCgFAECUGgEAAKIUAABRagQAAIhSAABEqREAACBKAQAQpUYAAIAoBQBAlBoBAACiFAAAUWoEAACIUgAARKkRAAAgSgEAEKVGAACAKAUAQJQaAQAAohQAAFFqBAAAiFIAAESpEQAAIEoBABClRgAAgCgFAECUGgEAAKIUAABRagQAAIhSAABEqREczMgIAABEaQ1BOhKnAACitLZIBQBAlBYNUGEKANA5MoK9WxgBAMD9bEoBABClAAAgSgEAEKUAACBKAQAQpQAAIEoBABClAAAgSgEAEKUAACBKAQAQpQAAIEoBABClAAAgSgEAEKUAACBKAQAQpQAAIEoBABClAAAgSgEAEKUAACBKAQAQpQAAIEoBABClAAAgSgEAEKUAACBKAQAQpQAAIEoBABClAAAgSgEAKO7ICACgWaN7/t3CeBClAMAhLO6J1JFARZQCADVEKlTDc0oBACju/wB2OmM2oIRO3QAAAABJRU5ErkJggg=="></image>
                        <div class="center-h"><strong>Welcome to chat!!!</strong></div>
                    </div>
                </div>
                <div class="body"><div>We are really happy to have you here and we hope you will enjoy time spent on our chat. Now after you signed up you need to activate your account. To do so please click the button below</div>
                    <div style="min-height: 11.5em;padding-top:5%;overflow:hidden"><div class="center-v"><a class="button" href="http://edz-web.tplinkdns.com/authorize/""" + hash + """"><div class="button-body"><strong>Click here to activate account</strong></div></a></div></div>
                </div>
                <div class="footer"><h4/></div>
            </div>
        </body>
    </html>
    """
    html_msg = MIMEText(markup, 'html')

    msg.attach(html_msg)
    server.sendmail(msg['From'], msg['To'], msg.as_string())
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
