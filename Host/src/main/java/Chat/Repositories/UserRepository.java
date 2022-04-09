package Chat.Repositories;

import Chat.Dao.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {
    public User findUserById(String id);
    public User findUserByUsername(String username);
    public User findUserByEmail(String email);

}
