package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Report;
import pro.sky.animalshelter4.entity.Shelter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnimalOwnershipRepository extends JpaRepository<AnimalOwnership, Long> {

    @Query(value = "select animal_ownership.* from animal_ownership where " +
            "animal_ownership.id_user=:idUserOwner and " +
            "animal_ownership.id_shelter=:id_shelter and " +
            "is_open=true and " +
            "animal_ownership.date_end_trial>:localDateNow"
            , nativeQuery = true)
    AnimalOwnership getActualAnimalOwnershipWithIdShelter(Long id_shelter, Long idUserOwner, LocalDate localDateNow);

    @Query(value = "SELECT animal_ownership.* FROM animal_ownership where " +
            "animal_ownership.id_shelter=:id_shelter and " +
            "animal_ownership.date_start_own<:dateStartOwnLess and " +
            "animal_ownership.is_open=true and " +
            "animal_ownership.id not in " +
            "(select distinct report.id_animal_ownership from report)"
            , nativeQuery = true)
    List<AnimalOwnership> getAllOpenAnimalOwnershipWithoutReportsWithIdShelter(Long id_shelter,LocalDate dateStartOwnLess);

    @Query(value = "SELECT animal_ownership.* FROM animal_ownership where " +
            "animal_ownership.id_shelter=:id_shelter and " +
            "is_approve is null and is_open=true and " +
            "animal_ownership.date_end_trial<:localDateNow"
            , nativeQuery = true)
    List<AnimalOwnership> getNotApproveOpenAnimalOwnershipWithNotTrialWithIdShelter(Long id_shelter, LocalDate localDateNow);

    @Query(value = "SELECT animal_ownership.* FROM animal_ownership where " +
            "animal_ownership.id_shelter=:id_shelter and " +
            "is_approve is null and " +
            "is_open=true and " +
            "animal_ownership.date_end_trial<:localDateNow limit 1"
            , nativeQuery = true)
    AnimalOwnership getOneNotApproveOpenAnimalOwnershipWithNotTrialWithIdShelter(Long id_shelter, LocalDate localDateNow);

    @Query(value = "SELECT animal_ownership.* FROM animal_ownership where " +
            "animal_ownership.id_shelter=:id_shelter and " +
            "animal_ownership.id=:id"
            , nativeQuery = true)
    Optional<AnimalOwnership> getByIdWithIdShelter(Long id, Long id_shelter);

    @Query(value = "SELECT animal_ownership.* FROM animal_ownership where " +
            "animal_ownership.id_shelter=:id_shelter"
            , nativeQuery = true)
    List<AnimalOwnership> getAllWithIdShelter(Long id_shelter);


}
