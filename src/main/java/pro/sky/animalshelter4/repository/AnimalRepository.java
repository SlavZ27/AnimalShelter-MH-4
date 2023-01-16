package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.Animal;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    @Query(value = "select animal.* from animal where animal.id not in (SELECT animal_ownership.id_animal from animal_ownership);"
            , nativeQuery = true)
    List<Animal> getAllNotBusyAnimals();

    @Query(value = "select * from animal where animal.id_animal_type is null limit 1"
            , nativeQuery = true)
    Animal getNotComplement();

}
