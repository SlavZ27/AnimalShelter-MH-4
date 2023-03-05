package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.CallRequest;

import java.util.List;
import java.util.Optional;

/**
 * This class was created to use the database to create methods used in the class callRequestService
 */
public interface CallRequestRepository extends JpaRepository<CallRequest, Long> {


    @Query(value = "select call_request.* from call_request where call_request.is_open=true and id_client=:idUser and call_request.id_shelter=:idShelter limit 1"
            , nativeQuery = true)
    CallRequest getFirstOpenByUserIdForClientWithShelter(Long idUser, Long idShelter);

    @Query(value = "select call_request.* from call_request where call_request.is_open=true and id_volunteer=:idUser and call_request.id_shelter=:idShelter limit 1"
            , nativeQuery = true)
    CallRequest getOpenByUserIdForVolunteerWithShelter(Long idUser, Long idShelter);

    @Query(value = "select call_request.* from call_request where call_request.is_open=true and id_volunteer=:idUser and call_request.id_shelter=:idShelter"
            , nativeQuery = true)
    List<CallRequest> getAllOpenByUserIdForVolunteerWithShelter(Long idUser, Long idShelter);

    @Query(value = "select call_request.* from call_request where call_request.is_open=true and id_client=:idUser and call_request.id_shelter=:idShelter"
            , nativeQuery = true)
    List<CallRequest> getAllOpenByUserIdForClientWithShelter(Long idUser, Long idShelter);

    @Query(value = "select call_request.* from call_request where call_request.id_volunteer=:idUser and call_request.id_shelter=:idShelter"
            , nativeQuery = true)
    List<CallRequest> getAllCallRequestVolunteerWithShelter(Long idUser, Long idShelter);

    @Query(value = "select call_request.* from call_request where call_request.is_open=true and call_request.id_shelter=:idShelter"
            , nativeQuery = true)
    List<CallRequest> getAllOpenCallRequestWithShelter(Long idShelter);

    @Query(value = "select call_request.* from call_request where call_request.is_open=false and call_request.id_shelter=:idShelter"
            , nativeQuery = true)
    List<CallRequest> getAllCloseCallRequestWithShelter(Long idShelter);

    @Query(value = "select call_request.* from call_request where call_request.id=:id and call_request.id_shelter=:idShelter"
            , nativeQuery = true)
    Optional<CallRequest> getByIdWithShelter(Long id, Long idShelter);


}
