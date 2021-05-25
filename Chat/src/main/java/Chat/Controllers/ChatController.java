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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Collections;

@Controller
public class ChatController {
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    UserRepository userRepository;

    @MessageMapping("/id/{id}")
    @SendTo("/chats/{id}")
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
    @GetMapping("/check/{id}")
    @ResponseBody
    public String check(@PathVariable Long id, Principal p){
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
}
