package Chat.Dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Chats")
public class Chat {
    @Id
    public long id;
    public List<Message> messages;
    public String password;
    public String style;
    public String userID;
    public Chat(long id,String password,String style,List<Message> messages,String userID){
        this.id = id;
        this.messages = messages;
        this.password = password;
        this.style = style;
        this.userID = userID;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Message> getMessages() {
        return messages;
    }
    public void addMessage(Message message){
        this.messages.add(message);
    }
    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", messages=" + messages +
                '}';
    }
}
