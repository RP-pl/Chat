package Chat.Repositories;

import Chat.Dao.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupRepository extends MongoRepository<Group, Long> {
    public Group findGroupById(long id);
    public List<Group> findAll();
}