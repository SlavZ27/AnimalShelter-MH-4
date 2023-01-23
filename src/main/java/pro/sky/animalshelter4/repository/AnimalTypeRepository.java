package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.AnimalType;

public interface AnimalTypeRepository extends JpaRepository<AnimalType, Long> {

    @Query(value = "select animal_type.* from animal_type where animal_type.type_animal=:nameType"
            , nativeQuery = true)
    AnimalType getAnimalTypeByTypeAnimal(String nameType);

}
