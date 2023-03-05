package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.service.ChatService;

/**
 * This class was created to use the database to create methods used in the class ChatService
 * They are used in methods{@link ChatService#findChat(Long)#getChatOfVolunteer()}
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Chat getChatById(Long id);

}
