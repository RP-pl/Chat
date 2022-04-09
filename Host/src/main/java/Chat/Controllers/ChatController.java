package Chat.Controllers;

import Chat.Dao.Chat;
import Chat.Dao.Message;
import Chat.Dao.User;
import Chat.Repositories.ChatRepository;
import Chat.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Principal;
import java.time.Duration;
import java.util.Collections;

@Controller
public class ChatController {
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    UserRepository userRepository;

    @MessageMapping("/chat/id/{id}")
    @SendTo("/ws/chats/{id}")
    public Chat handleChats(@DestinationVariable long id, Message message) {
        Chat chat = chatRepository.findChatById(id);
        if (!message.data.equals("") && chat != null) {
            chat.addMessage(message);
            chatRepository.save(chat);
        }
        if (chat != null) {
            Collections.reverse(chat.messages);
        }
        return chat;
    }
    @GetMapping("/check/chat/has/{id}")
    @ResponseBody
    public String checkHasAccess(@PathVariable Long id, Principal p){
        User user = userRepository.findUserByUsername(p.getName());
        Chat c = chatRepository.findChatById(id);
        if(c == null){
            return "False";
        }
        else if(!user.getChats().contains(c.id)){
            return "False";
        }
        else{
            return user.getUsername();
        }
    }
    @GetMapping("/check/chat/author/{id}")
    @ResponseBody
    public String checkIfAuthor(@PathVariable Long id, Principal p){
        User user = userRepository.findUserByUsername(p.getName());
        Chat c = chatRepository.findChatById(id);
        if(c == null){
            return "False";
        }
        else if(!user.getId().equals(c.userID)){
            return "False";
        }
        else{
            return user.getUsername();
        }
    }
    @MessageMapping("/del/chat/{id}")
    @SendTo("/ws/chats/{id}")
    public Chat deleteMessage(@DestinationVariable long id,Message m) throws IOException, InterruptedException {
        Chat c = chatRepository.findChatById(id);
        if(m.type.equals("file")){
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(180)).build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://172.16.238.105:5000/del/"+m.data.split("/")[3])).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        }
        c.messages.remove(m);
        chatRepository.save(c);
        Collections.reverse(c.messages);
        return c;
    }
}
