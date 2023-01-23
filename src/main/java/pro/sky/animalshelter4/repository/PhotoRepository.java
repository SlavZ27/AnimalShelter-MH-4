package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @Query(value = "select * from photo where id_media=:idMedia"
            , nativeQuery = true)
    Photo findByIdPhoto(String idMedia);
}
