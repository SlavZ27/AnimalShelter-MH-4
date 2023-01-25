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

    /**
     * This method using method repository, allows create AnimalTypeDto
     *
     * @param animalTypeDto is not null
     * @return AnimalTypeDto
     */
    public AnimalTypeDto createAnimalType(AnimalTypeDto animalTypeDto) {
        logger.info("Method createAnimalType was start for create new AnimalType");
        return dtoMapperService.toDto(animalTypeRepository.save(dtoMapperService.toEntity(animalTypeDto)));
    }

    /**
     * This method using method repository, allows read AnimalTypeDto
     *
     * @param id is not null
     * @return AnimalTypeDto
     */
    public AnimalTypeDto readAnimalType(Long id) {
        logger.info("Method readAnimalType was start for find AnimalType by id");
        return dtoMapperService.toDto(
                animalTypeRepository.findById(id).
                        orElseThrow(() -> new AnimalTypeNotFoundException(String.valueOf(id))));
    }

    /**
     * This method using method repository, allows get all AnimalTypeDto
     *
     * @return List<AnimalTypeDto>
     */
    public List<AnimalTypeDto> getAllDto() {
        logger.info("Method getAllDto was start for get all AnimalType");
        return animalTypeRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    /**
     * This method using method repository, allows update  AnimalTypeDto
     *
     * @param animalTypeDto is not null
     * @return AnimalTypeDto
     */
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

    /**
     * This method using method repository, allows del AnimalTypeDto
     * @param id is not null
     * @return AnimalTypeDto
     */
    public AnimalTypeDto deleteAnimalType(Long id) {
        AnimalType AnimalType = new AnimalType();
        AnimalType.setId(id);
        return dtoMapperService.toDto(deleteAnimalType(AnimalType));
    }

    /**
     * This method using method repository, allows del AnimalType
     *
     * @param animalType is not null
     * @return AnimalType
     */
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


    /**
     * This method using method repository, allows find AnimalType
     *
     * @param id is not null
     * @return AnimalType
     */
    public AnimalType findAnimalType(Long id) {
        logger.info("Method findAnimalType was start for find AnimalType by id");
        return animalTypeRepository.findById(id).
                orElseThrow(() -> new AnimalTypeNotFoundException(String.valueOf(id)));
    }

    /**
     * This method using method repository, allows find AnimalType
     *
     * @param nameType is not null
     * @return AnimalType
     */

    public AnimalType findAnimalType(String nameType) {
        logger.info("Method findAnimalType was start for find AnimalType by nameType");
        if (nameType == null) {
            return null;
        }
        return animalTypeRepository.getAnimalTypeByTypeAnimal(nameType);
    }

    /**
     * This method using method repository, allows get all AnimalType
     *
     * @return  List<AnimalType>
     */

    public List<AnimalType> getAll() {
        return animalTypeRepository.findAll();
    }

}
