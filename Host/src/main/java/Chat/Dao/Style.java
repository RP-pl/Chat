package Chat.Dao;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Styles")
public class Style {
    public String name;
    public Style(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
