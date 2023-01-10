package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.Chat;

import java.util.List;

public interface CallRequestRepository extends JpaRepository<CallRequest, Long> {

    @Query(value = "select * from call_request where is_open=true and id_chat_volunteer=:idChat"
            , nativeQuery = true)
    List<CallRequest> getAllOpenByChatId(Long idChat);

    @Query(value = "select * from call_request where is_open=true and id_chat_client=:chatClientId limit 1"
            , nativeQuery = true)
    CallRequest getFirstOpenByChatClientId(Long chatClientId);

}
