package Chat.Controllers;

import Chat.Dao.Group;
import Chat.Dao.Message;
import Chat.Dao.User;
import Chat.Repositories.GroupRepository;
import Chat.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Principal;
import java.time.Duration;
import java.util.Collections;

@Controller
public class GroupController {
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserRepository userRepository;

    @MessageMapping("/group/id/{id}")
    @SendTo("/ws/groups/{id}")
    public Group handleChats(@DestinationVariable long id, Message message) {
        Group group = groupRepository.findGroupById(id);
        if (!message.data.equals("") && group != null) {
            group.addMessage(message);
            groupRepository.save(group);
        }
        if (group != null) {
            Collections.reverse(group.messages);
        }
        return group;
    }
    @GetMapping("/check/group/has/{id}")
    @ResponseBody
    public String checkHasAccess(@PathVariable Long id, Principal p){
        User user = userRepository.findUserByUsername(p.getName());
        Group group = groupRepository.findGroupById(id);
        if(group == null){
            return "False";
        }
        else if(!user.groups.contains(group.getId())){
            return "False";
        }
        else{
            return user.getUsername();
        }
    }
    @GetMapping("/check/group/author/{id}")
    @ResponseBody
    public String checkIfAuthor(@PathVariable Long id, Principal p){
        User user = userRepository.findUserByUsername(p.getName());
        Group group = groupRepository.findGroupById(id);
        if(group == null){
            return "False";
        }
        else if(!group.usernames.contains(user.getUsername())){
            return "False";
        }
        else{
            return user.getUsername();
        }
    }
    @MessageMapping("/del/group/{id}")
    @SendTo("/ws/groups/{id}")
    public Group deleteMessage(@DestinationVariable long id,Message m) throws IOException, InterruptedException {
        Group group = groupRepository.findGroupById(id);;
        if(m.type.equals("file")){
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(180)).build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://172.16.238.105:5000/del/"+m.data.split("/")[3])).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        }
        group.messages.remove(m);
        groupRepository.save(group);
        Collections.reverse(group.messages);
        return group;
    }
}