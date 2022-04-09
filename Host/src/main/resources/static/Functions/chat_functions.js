    var stompClient=null
    var username=null
    var id=null
    var messages=null
    const messageList = document.getElementById('messageList')
    function sendText(){
        stompClient.send("/app/chat/id/"+id,{},JSON.stringify({'author':username,'data':document.getElementById('data').value,"type":"text"}))
                var input = document.getElementById('data');
                input.value = "";
}
function sendEmpty(){
    stompClient.send("/app/chat/id/"+id,{},JSON.stringify({'author':username,'data':'',"type":"text"}));
}
    async function sendFile(input){
        var files = input.files
        for(var file of files){
        if(file.type == "image/jpeg" || file.type == "image/png" || file.type == "image/jpg" ){
                var reader = new FileReader();
                reader.addEventListener("load", function () {
                stompClient.send("/app/chat/id/"+id,{},JSON.stringify({'author':username,'data':reader.result,"type":"image"}))
                }, false)
                if (file) {
                    reader.readAsDataURL(file);
                }
            }
            else{
                var reader = new FileReader();
                            reader.addEventListener("load", async function () {
                            var x = reader.result.split(",")[1]
                            var fname = file.name.split(".")
                            var resp = await fetch("/file/"+id+"/"+fname[0]+"/"+fname[1],{method: "POST",body:x})
                            var name = await resp.json()
                            console.log(name)
                            stompClient.send("/app/chat/id/"+id,{},JSON.stringify({'author':username,'data':"/file/"+id+"/"+name['name'],'name':file.name,"type":"file"}))
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
            var r = await fetch("/check/chat/has/"+id)
            var p = await r.text()
            if(p != "False"){
           username = p;
        document.getElementById('connect').setAttribute("disp","noDisplay");
        document.getElementById('messaging1').removeAttribute("disp");
        document.getElementById('messaging2').removeAttribute("disp");
        document.cookie = "username="+username;
        document.cookie = "id="+id;
        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/ws/chats/'+id, async function (response) {
                messageList.innerHTML = "";
                var chat = JSON.parse(response.body)
                var body = document.getElementsByTagName('body')[0];
                body.id = chat.style;
                var req = await fetch("/check/chat/author/"+id)
                var resp = await req.text()
                messages = chat.messages
                var i=0
                for(var message of chat.messages){
                            var spacer = document.createElement('div')
                            var container = document.createElement('div')
                            container.setAttribute("class","container")
                            var top = document.createElement('div')
                            top.setAttribute("class","top")
                            spacer.setAttribute("class","spacer")
                            var author = document.createElement('div')
                            if(message.author == username){
                                spacer.setAttribute("id","move")
                                container.setAttribute("id","self")
                            }
                            author.setAttribute("class","author")
                            author.innerHTML = "Author: " + message.author + '\n';
                            top.appendChild(author)
                            if(resp != "False"){
                                 var btn = document.createElement('button')
                                 btn.setAttribute("onClick",'del('+i+')')
                                 btn.setAttribute("class","btn-close btn-close-white")
                                 top.appendChild(btn)
                            }
                            container.appendChild(top)
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
                        spacer.appendChild(container)
                        container.appendChild(data)
                        messageList.appendChild(spacer);
                        i++
                }
            });
            sendEmpty();
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
function del(message){
    console.log(message)
    console.log(JSON.stringify(messages[message]))
    stompClient.send("/app/del/chat/"+id,{},JSON.stringify(messages[message]))
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
        var r = await fetch("/check/chat/has/"+id)
        if(await r.text() != "False"){
            document.getElementById('connect').setAttribute("disp","noDisplay");
            document.getElementById('messaging1').removeAttribute("disp");
            document.getElementById('messaging2').removeAttribute("disp");
            var socket = new SockJS('/connect');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                stompClient.subscribe('/ws/chats/'+id, async function (response) {
                    messageList.innerHTML = "";
                    var chat = JSON.parse(response.body);
                    var body = document.getElementsByTagName('body')[0];
                    body.id = chat.style;
                    var req = await fetch("/check/chat/author/"+id)
                    var resp = await req.text()
                    var i=0
                    messages = chat.messages
                    for(var message of chat.messages){
                            var spacer = document.createElement('div')
                            var container = document.createElement('div')
                            container.setAttribute("class","container")
                            var top = document.createElement('div')
                            top.setAttribute("class","top")
                            spacer.setAttribute("class","spacer")
                            var author = document.createElement('div')
                            if(message.author == username){
                                container.setAttribute("id","self")
                                spacer.setAttribute("id","move")
                            }
                            author.setAttribute("class","author")
                            author.innerHTML = "Author: " + message.author + '\n';
                            top.appendChild(author)
                            if(resp != "False"){
                                 var btn = document.createElement('button')
                                 btn.setAttribute("onClick",'del('+i+')')
                                 btn.setAttribute("class","btn-close btn-close-white")
                                 top.appendChild(btn)
                            }
                            container.appendChild(top)
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
                        spacer.appendChild(container)
                        container.appendChild(data)
                        messageList.appendChild(spacer);
                            i++
                    }
                });
                sendEmpty();
            });
            }
            else{
            }
        }
}