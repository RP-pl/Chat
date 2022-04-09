package Chat.Dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Groups")
public class Group {
    @Id
    private Long id;
    public String name;
    public List<String> usernames;
    public List<Message> messages;
    private final String style;
    public Group(Long id,List<String> usernames,String name,String style,List<Message> messages){
        this.id = id;
        this.usernames = usernames;
        this.messages = messages;
        this.name = name;
        this.style = style;
    }


    public List<Message> getMessages() {
        return messages;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public long getId() {
        return id;
    }
    public void addUser(User user){
        this.usernames.add(user.getUsername());
    }
    public void addMessage(Message message){
        this.messages.add(message);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", usernames=" + usernames +
                ", messages=" + messages +
                '}';
    }

    public String getStyle() {
        return style;
    }

    public String getName() {
        return name;
    }
}
