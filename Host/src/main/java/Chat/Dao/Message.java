package Chat.Dao;

import java.util.Objects;

public class Message {
    public Long id;
    public String type;
    public String author;
    public String data;
    public String name;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(type, message.type) && Objects.equals(author, message.author) && Objects.equals(data, message.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, author, data);
    }


    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", author='" + author + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
