package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animalshelter4.entity.UnfinishedRequest;

public interface UnfinishedRequestRepository extends JpaRepository<UnfinishedRequest, Long> {

}
