    var stompClient=null
    var username=null
    var id=null
    const messageList = document.getElementById('messageList')
    function sendText(){
        stompClient.send("/app/id/"+id,{},JSON.stringify({'author':username,'data':document.getElementById('data').value,"type":"text"}))
                var input = document.getElementById('data');
                input.value = "";
}
    async function sendFile(input){
        var files = input.files
        for(var file of files){
        if(file.type == "image/jpeg" || file.type == "image/png" || file.type == "image/jpg" ){
                var reader = new FileReader();
                reader.addEventListener("load", function () {
                stompClient.send("/app/id/"+id,{},JSON.stringify({'author':username,'data':reader.result,"type":"image"}))
                }, false)
                if (file) {
                    reader.readAsDataURL(file);
                }
            }
            else{
                var reader = new FileReader();
                            reader.addEventListener("load", async function () {
                            var x = reader.result.split(",")[1]
                            console.log(x)
                            var fname = file.name.split(".")
                            var resp = await fetch("/file/"+id+"/"+fname[0]+"/"+fname[1],{method: "POST",body:x})
                            var name = await resp.json()
                            console.log(name)
                            stompClient.send("/app/id/"+id,{},JSON.stringify({'author':username,'data':"/file/"+id+"/"+name['name'],'name':file.name,"type":"file"}))
                            }, false)
                            if (file) {
                                reader.readAsDataURL(file);
                            }
                        }
            }
        var input = document.getElementById('finput');
        input.value = "";
    }

async   function connectWithoutCookies() {
    var socket = new SockJS('/connect');
    stompClient = Stomp.over(socket);
    id =document.getElementById('chatID').value;
    if(id != ""&&id!=null){
            var r = await fetch("/check/"+id)
            var p = await r.text()
            if(p != "False"){
           username = p;
        document.getElementById('connect').setAttribute("disp","noDisplay");
        document.getElementById('messaging1').removeAttribute("disp");
        document.getElementById('messaging2').removeAttribute("disp");
        document.cookie = "username="+username;
        document.cookie = "id="+id;
        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/chats/'+id, function (response) {
                messageList.innerHTML = "";
                var chat = JSON.parse(response.body)
                var body = document.getElementsByTagName('body')[0];
                body.id = chat.style;
                for(var message of chat.messages){
                        var container = document.createElement('div')
                        container.setAttribute("class","container")
                        var author = document.createElement('div')
                        if(message.author == username){
                            container.setAttribute("id","self")
                        }
                        author.setAttribute("class","author")
                        author.innerHTML = "Author: " + message.author + '\n';
                        container.appendChild(author)
                        if(message.type == "text"){
                            var data = document.createElement('div')
                            data.setAttribute("class","data")
                            data.innerHTML = "Message: " + message.data;
                        }
                        if(message.type == "image"){
                            var data = document.createElement('img')
                            data.setAttribute("src",message.data)
                            data.setAttribute("onclick","openNewTab(this.src)")
                            data.setAttribute("class","img")
                        }
                        if(message.type == "file"){
                            var data = document.createElement('a')
                            data.setAttribute("href",message.data)
                            data.setAttribute("download",message.name)
                            data.innerHTML =  message.name
                        }
                        container.appendChild(data)
                        //container.setAttribute("style","left:80%")
                        messageList.appendChild(container);
                }
            });
            sendText();
        });
        }
        else{
        }
    }
    else{
    }
}
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
        window.document.cookie = "username=; id=;";
        document.getElementById('connect').removeAttribute("disp");
        document.getElementById('messaging1').setAttribute("disp","noDisplay");
        document.getElementById('messaging2').setAttribute("disp","noDisplay");
    }
    console.log("Disconnected");
}

function openNewTab(src){
    var url = src;
    popup = window.open(src,"Image");
}
function toggleFile(){
    var finput = document.getElementById('finput');
    if(finput.hasAttribute('disp')){
        finput.removeAttribute('disp')
    }
    else{
        finput.setAttribute("disp","noDisplay");
    }
}
function handlePress(e){
if(e.keyCode === 13){
        e.preventDefault();
        sendText();
     }
}
async function connectWithCookies(){
        cookies = document.cookie;
        for(var cookie of document.cookie.split(";")){
          var name = cookie.trim().split("=")[0]
          var value = cookie.trim().split("=")[1]
          if(name=="username"){
                username=value;
          }
          if(name=="id"){
                id=value;
          }
        }
         console.log(id)
         console.log(username)
        if(id!=""&&username!=""&&id!=null){
        var r = await fetch("/check/"+id)
        if(await r.text() != "False"){
            document.getElementById('connect').setAttribute("disp","noDisplay");
            document.getElementById('messaging1').removeAttribute("disp");
            document.getElementById('messaging2').removeAttribute("disp");
            var socket = new SockJS('/connect');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                stompClient.subscribe('/chats/'+id, function (response) {
                    messageList.innerHTML = "";
                    var chat = JSON.parse(response.body);
                    var body = document.getElementsByTagName('body')[0];
                    body.id = chat.style;
                    for(var message of chat.messages){
                            var container = document.createElement('div')
                            container.setAttribute("class","container")
                            var author = document.createElement('div')
                            if(message.author == username){
                                container.setAttribute("id","self")
                            }
                            author.setAttribute("class","author")
                            author.innerHTML = "Author: " + message.author + '\n';
                            container.appendChild(author)
                            if(message.type == "text"){
                                var data = document.createElement('div')
                                data.setAttribute("class","data")
                                data.innerHTML = "Message: " + message.data;
                            }
                            if(message.type == "image"){
                                var data = document.createElement('img')
                                data.setAttribute("src",message.data)
                                data.setAttribute("onclick","openNewTab(this.src)")
                                data.setAttribute("class","img")
                            }
                            if(message.type == "file"){
                                var data = document.createElement('a')
                                data.setAttribute("href",message.data)
                                data.setAttribute("download",message.name)
                                data.innerHTML =  message.name
                            }
                            container.appendChild(data)
                            //container.setAttribute("style","left:80%")
                            messageList.appendChild(container);
                    }
                });
                sendText();
            });
            }
            else{
            }
        }
}