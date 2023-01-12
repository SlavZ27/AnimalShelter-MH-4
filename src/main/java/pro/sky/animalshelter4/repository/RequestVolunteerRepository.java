package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animalshelter4.entity.RequestVolunteer;
import pro.sky.animalshelter4.service.ChatService;

/**
 * This class was created to use the database to create methods used in the class RequestVolunteerService
 */
public interface RequestVolunteerRepository extends JpaRepository<RequestVolunteer, Long> {
}
