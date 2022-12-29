package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animalshelter4.entity.CallRequest;

public interface CallRequestRepository extends JpaRepository<CallRequest, Long> {
}
