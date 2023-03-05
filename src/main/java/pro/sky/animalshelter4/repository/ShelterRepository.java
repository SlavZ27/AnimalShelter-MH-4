package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.Shelter;

import java.util.List;
import java.util.Optional;

public interface ShelterRepository extends JpaRepository<Shelter, Long> {

    @Query(value = "select shelter.* from shelter where shelter.text_designation=:shelterDesignation"
            , nativeQuery = true)
    Optional<Shelter> getShelterByshelterDesignation(String shelterDesignation);

    @Query(value = "select shelter.text_designation from shelter"
            , nativeQuery = true)
    List<String> getAllshelterDesignation();
}
