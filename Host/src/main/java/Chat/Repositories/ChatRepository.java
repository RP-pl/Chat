package Chat.Repositories;

import Chat.Dao.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, Long> {
    public Chat findChatById(long id);

}
