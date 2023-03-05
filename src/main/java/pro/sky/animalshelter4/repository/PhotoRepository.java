package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.Animal;
import pro.sky.animalshelter4.entity.Photo;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @Query(value = "select photo.* from photo where photo.id=:id and photo.id_shelter=:idShelter"
            , nativeQuery = true)
    Optional<Photo> findByIdAndIdShelter(Long id, Long idShelter);

    @Query(value = "select photo.* from photo where photo.id_shelter=:idShelter"
            , nativeQuery = true)
    List<Photo> findAllByIdShelter(Long idShelter);

    @Query(value = "select * from photo where id_media=:idMedia"
            , nativeQuery = true)
    Photo findByIdPhoto(String idMedia);
}
