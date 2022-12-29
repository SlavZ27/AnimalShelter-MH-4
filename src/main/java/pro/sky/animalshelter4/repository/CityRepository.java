package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animalshelter4.entity.City;

public interface CityRepository extends JpaRepository<City, Long> {
}
