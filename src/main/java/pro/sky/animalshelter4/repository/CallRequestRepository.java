package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.Chat;

import java.util.List;

/**
 * This class was created to use the database to create methods used in the class callRequestService
 */
public interface CallRequestRepository extends JpaRepository<CallRequest, Long> {



    @Query(value = "select * from call_request where is_open=true and id_client=:idUser limit 1"
            , nativeQuery = true)
    CallRequest getFirstOpenByUserIdForClient(Long idUser);

    @Query(value = "select * from call_request where is_open=true and id_volunteer=:idUser"
            , nativeQuery = true)
    List<CallRequest> getAllOpenByUserIdForVolunteer(Long idUser);

    @Query(value = "select * from call_request where is_open=true and id_client=:idUser"
            , nativeQuery = true)
    List<CallRequest> getAllOpenByUserIdForClient(Long idUser);

    @Query(value = "select * from call_request where id_volunteer=:idUser"
            , nativeQuery = true)
    List<CallRequest> getAllCallRequestVolunteer(Long idUser);

    @Query(value = "select * from call_request where is_open=true"
            , nativeQuery = true)
    List<CallRequest> getAllOpenCallRequest();

    @Query(value = "select * from call_request where is_open=false"
            , nativeQuery = true)
    List<CallRequest> getAllCloseCallRequest();

}
