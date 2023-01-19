package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.AnimalType;
import pro.sky.animalshelter4.entityDto.AnimalTypeDto;
import pro.sky.animalshelter4.exception.AnimalTypeNotFoundException;
import pro.sky.animalshelter4.repository.AnimalTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is necessary to manage actions with animals
 * The class must have many dependencies so that it can work correctly.
 * And also respond to requests received from {@link AnimalTypeRepository}
 */
@Service
public class AnimalTypeService {

    private final AnimalTypeRepository animalTypeRepository;
    private final DtoMapperService dtoMapperService;
    private final Logger logger = LoggerFactory.getLogger(AnimalTypeService.class);


    public AnimalTypeService(AnimalTypeRepository animalTypeRepository, DtoMapperService dtoMapperService) {
        this.animalTypeRepository = animalTypeRepository;
        this.dtoMapperService = dtoMapperService;
    }

    public AnimalTypeDto createAnimalType(AnimalTypeDto animalTypeDto) {
        logger.info("Method createAnimalType was start for create new AnimalType");
        return dtoMapperService.toDto(animalTypeRepository.save(dtoMapperService.toEntity(animalTypeDto)));
    }

    public AnimalTypeDto readAnimalType(Long id) {
        logger.info("Method readAnimalType was start for find AnimalType by id");
        return dtoMapperService.toDto(
                animalTypeRepository.findById(id).
                        orElseThrow(() -> new AnimalTypeNotFoundException(String.valueOf(id))));
    }

    public List<AnimalTypeDto> getAllDto() {
        logger.info("Method getAllDto was start for get all AnimalType");
        return animalTypeRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public AnimalTypeDto updateAnimalType(AnimalTypeDto animalTypeDto) {
        logger.info("Method updateAnimalType was start for update AnimalType");
        AnimalType newAnimalType = dtoMapperService.toEntity(animalTypeDto);
        AnimalType oldAnimalType = findAnimalType(newAnimalType.getId());
        if (oldAnimalType == null) {
            throw new AnimalTypeNotFoundException(String.valueOf(newAnimalType.getId()));
        }
        oldAnimalType.setTypeAnimal(newAnimalType.getTypeAnimal());
        return dtoMapperService.toDto(animalTypeRepository.save(oldAnimalType));
    }

    public AnimalTypeDto deleteAnimalType(Long id) {
        AnimalType AnimalType = new AnimalType();
        AnimalType.setId(id);
        return dtoMapperService.toDto(deleteAnimalType(AnimalType));
    }

    public AnimalType deleteAnimalType(AnimalType animalType) {
        logger.info("Method deleteAnimalType was start for delete AnimalType");
        if (animalType.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of AnimalType");
        }
        AnimalType animalTypeFound = animalTypeRepository.findById(animalType.getId()).
                orElseThrow(() -> new AnimalTypeNotFoundException(String.valueOf(animalType.getId())));
        animalTypeRepository.delete(animalTypeFound);
        return animalTypeFound;
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
