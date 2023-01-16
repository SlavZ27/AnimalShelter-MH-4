package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.AnimalType;
import pro.sky.animalshelter4.exception.AnimalTypeNotFoundException;
import pro.sky.animalshelter4.repository.AnimalTypeRepository;

import java.util.List;

@Service
public class AnimalTypeService {

    private final AnimalTypeRepository animalTypeRepository;
    private final Logger logger = LoggerFactory.getLogger(AnimalTypeService.class);


    public AnimalTypeService(AnimalTypeRepository animalTypeRepository) {
        this.animalTypeRepository = animalTypeRepository;
    }


    public AnimalType findAnimalType(Long id) {
        logger.info("Method findAnimalType was start for find AnimalType by id");
        return animalTypeRepository.findById(id).
                orElseThrow(() -> new AnimalTypeNotFoundException(String.valueOf(id)));
    }

    public AnimalType findAnimalType(String nameType) {
        logger.info("Method findAnimalType was start for find AnimalType by nameType");
        if (nameType == null) {
            return null;
        }
        return animalTypeRepository.getAnimalTypeByTypeAnimal(nameType);
    }

    public List<AnimalType> getAll() {
        return animalTypeRepository.findAll();
    }

}
