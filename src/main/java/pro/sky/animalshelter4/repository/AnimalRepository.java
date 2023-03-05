package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.Animal;
import pro.sky.animalshelter4.entity.Shelter;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    @Query(value = "select animal.* from animal where animal.id_shelter=:idShelter and animal.id not in (SELECT animal_ownership.id_animal from animal_ownership)"
            , nativeQuery = true)
    List<Animal> getAllNotBusyAnimalsWithShelter(Long idShelter);

    @Query(value = "select animal.* from animal where animal.id=:idAnimal and animal.id_shelter=:idShelter and animal.id not in (SELECT animal_ownership.id_animal from animal_ownership) limit 1"
            , nativeQuery = true)
    Animal findAnimalWithIdNotBusyWithShelter(Long idAnimal, Long idShelter);

    @Query(value = "select animal.* from animal where animal.id=:id and animal.id_shelter=:idShelter"
            , nativeQuery = true)
    Optional<Animal> findByIdAndIdShelter(Long id, Long idShelter);

    @Query(value = "select animal.* from animal where animal.id_shelter=:idShelter"
            , nativeQuery = true)
    List<Animal> findAllWithIdShelter(Long idShelter);
}
