package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animalshelter4.entity.UnfinishedRequestTelegram;
/**
 * This class was created to use the database to create methods used in the class UnfinishedRequestService
 */
public interface UnfinishedRequestRepository extends JpaRepository<UnfinishedRequestTelegram, Long> {

}
