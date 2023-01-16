package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.UnfinishedRequestTelegram;
/**
 * This class was created to use the database to create methods used in the class UnfinishedRequestService
 */
public interface UnfinishedRequestTelegramRepository extends JpaRepository<UnfinishedRequestTelegram, Long> {

    @Query(value = "select * from unfinished_request_telegram where id_chat_telegram=:idChat"
            , nativeQuery = true)
    UnfinishedRequestTelegram findByIdChat(Long idChat);
}
