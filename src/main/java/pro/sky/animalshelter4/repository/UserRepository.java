package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    @Query(value = "select users.* from users,telegram_chat where users.id_telegram_chat=telegram_chat.id and telegram_chat.id=:idChat and users.is_volunteer=true"
            , nativeQuery = true)
    User getByIdTelegramChatAndVolunteer(Long idChat);

    @Query(value = "select users.* from users,telegram_chat where users.id_telegram_chat=telegram_chat.id and telegram_chat.id=:idChat"
            , nativeQuery = true)
    User getByIdTelegramChat(Long idChat);

    @Query(value = "select * from users where users.is_volunteer=true"
            , nativeQuery = true)
    List<User> getAllVolunteers();

    @Query(value = "select * from users where is_volunteer=false"
            , nativeQuery = true)
    List<User> getAllClients();

}
