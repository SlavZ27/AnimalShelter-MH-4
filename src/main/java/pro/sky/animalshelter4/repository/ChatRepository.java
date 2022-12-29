package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animalshelter4.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Chat getChatById(Long id);

}
