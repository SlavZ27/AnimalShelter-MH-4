package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.AnimalOwnership;

public interface AnimalOwnershipRepository extends JpaRepository<AnimalOwnership, Long> {

    @Query(value = "select animal_ownership.* from animal_ownership where animal_ownership.id_user=:idUserOwner and animal_ownership.date_end_trial>now();"
            , nativeQuery = true)
    AnimalOwnership getActualAnimalOwnership(Long idUserOwner);
}
