package Chat.Repositories;

import Chat.Dao.Style;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StyleRepository extends MongoRepository<Style,String> {
    public Style findStyleByName(String name);
    public List<Style> findAll();
}
