package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.animalshelter4.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * This class was created to use the database to create methods used in the class UserService
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    @Query(value = "select users.* from users where users.id_telegram_chat=:idChatTelegram and users.id_shelter=:idShelter limit 1"
            , nativeQuery = true)
    User findUserWithTelegramChatIdAndShelterId(Long idChatTelegram, Long idShelter);

    @Query(value = "select users.* from users where users.id_telegram_chat=:idChatTelegram and users.id_shelter=:idShelter and users.id in (SELECT animal_ownership.id_user from animal_ownership) limit 1"
            , nativeQuery = true)
    User findUserWithShelterIdAndTelegramChatIdInAnimalOwnership(Long idChatTelegram, Long idShelter);

    @Query(value = "select users.* from users where users.id=:id and id_shelter=:id_shelter limit 1"
            , nativeQuery = true)
    Optional<User> getUserByIdAndShelter(Long id, Long id_shelter);

    @Query(value = "select * from users where users.is_volunteer=true and id_shelter=:id_shelter"
            , nativeQuery = true)
    List<User> getAllVolunteersWithShelter(Long id_shelter);

    @Query(value = "select * from users where is_volunteer=false and id_shelter=:id_shelter"
            , nativeQuery = true)
    List<User> getAllClientsWithShelter(Long id_shelter);

    @Query(value = "select users.* from users,report,animal_ownership where id_shelter=:id_shelter and users.id=animal_ownership.id_user and animal_ownership.id=report.id_animal_ownership and report.id=:id_report limit 1"
            , nativeQuery = true)
    User getUserOwnerReportWithShelter(Long id_report, Long id_shelter);

}
