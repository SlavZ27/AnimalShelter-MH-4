package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Animal;
import pro.sky.animalshelter4.entity.AnimalType;
import pro.sky.animalshelter4.entityDto.AnimalDto;
import pro.sky.animalshelter4.exception.AnimalNotFoundException;
import pro.sky.animalshelter4.repository.AnimalRepository;
import pro.sky.animalshelter4.repository.AnimalTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is necessary to manage actions with animals
 * The class must have many dependencies so that it can work correctly.
 * And also respond to requests received from {@link AnimalRepository}
 */
@Service
public class AnimalService {
    public final static String MESSAGE_ANIMALS_IS_ABSENT = "Sorry. Animals are absent";
    public final static String MESSAGE_ANIMAL_NOT_FOUND = "Sorry. Animals not found";
    public final static String CAPTION_SELECT_ANIMAL = "Select animal";
    public final static String MESSAGE_ANIMAL_CREATED = "Created animal";
    public final static String MESSAGE_ANIMAL_UPDATED = "Updated animal";
    public final static String MESSAGE_ALL_ANIMAL_COMPLEMENT = "All animal are ok";
    public final static String CAPTION_SELECT_TYPE_OF_ANIMAL = "Select type of animal";
    public final static String CAPTION_WRITE_NAME_OF_ANIMAL = "Write name of animal";

    private final AnimalRepository animalRepository;
    private final AnimalTypeService animalTypeService;
    private final DtoMapperService dtoMapperService;
    private final Logger logger = LoggerFactory.getLogger(AnimalService.class);

    public AnimalService(AnimalRepository animalRepository, AnimalTypeService animalTypeService, DtoMapperService dtoMapperService) {
        this.animalRepository = animalRepository;
        this.animalTypeService = animalTypeService;
        this.dtoMapperService = dtoMapperService;
    }

    /**
     * This method using method repository, allows adds Animal
     *
     * @param animal is not null
     * @return Animal
     */
    public Animal addAnimal(Animal animal) {
        logger.info("Method addAnimal was start for create new Animal");
        return animalRepository.save(animal);
    }

    /**
     * This method using method repository, allows crate AnimalDto
     * @param animalDto is not null
     * @return AnimalDto
     */
    public AnimalDto createAnimalDto(AnimalDto animalDto) {
        logger.info("Method createAnimalDto was start for create new Animal");
        return dtoMapperService.toDto(animalRepository.save(dtoMapperService.toEntity(animalDto)));
    }

    /**
     * This method using method repository, allows read AnimalDto
     *
     * @param id is not null
     * @return AnimalDto
     */
    public AnimalDto readAnimal(Long id) {
        logger.info("Method readAnimal was start for find animal by id");
        return dtoMapperService.toDto(
                animalRepository.findById(id).
                        orElseThrow(() -> new AnimalNotFoundException(String.valueOf(id))));
    }

    /**
     * This method using method repository, allows get all AnimalDto
     *
     * @return List<AnimalDto>
     */
    public List<AnimalDto> getAll() {
        logger.info("Method getAllAnimals was start for get all animal");
        return animalRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }


    /**
     * This method using method repository, allows find Animal
     *
     * @param id is not null
     * @return Animal
     */
    public Animal findAnimal(Long id) {
        logger.info("Method findAnimal was start for find Animal by id");
        return animalRepository.findById(id).
                orElseThrow(() -> new AnimalNotFoundException(String.valueOf(id)));
    }

    /**
     * This method using method repository, allows update AnimalDto
     *
     * @param animalDto is not null
     * @return AnimalDto
     */
    public AnimalDto updateAnimal(AnimalDto animalDto) {
        logger.info("Method updateAnimal was start for update Animal");
        Animal newAnimal = dtoMapperService.toEntity(animalDto);
        Animal oldAnimal = findAnimal(newAnimal.getId());
        if (oldAnimal == null) {
            throw new AnimalNotFoundException(String.valueOf(newAnimal.getId()));
        }
        oldAnimal.setNameAnimal(newAnimal.getNameAnimal());
        oldAnimal.setBorn(newAnimal.getBorn());
        oldAnimal.setAnimalType(newAnimal.getAnimalType());
        return dtoMapperService.toDto(animalRepository.save(oldAnimal));
    }

    /**
     * This method using method repository, allows del AnimalDto
     *
     * @param id is not null
     * @return AnimalDto
     */
    public AnimalDto deleteAnimal(Long id) {
        Animal animal = new Animal();
        animal.setId(id);
        return dtoMapperService.toDto(deleteAnimal(animal));
    }

    /**
     * This method using method repository, allows del Animal
     *
     * @param animal is not null
     * @return Animal
     */
    public Animal deleteAnimal(Animal animal) {
        logger.info("Method deleteAnimal was start for delete animal");
        if (animal.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of animal");
        }
        Animal animalFound = animalRepository.findById(animal.getId()).
                orElseThrow(() -> new AnimalNotFoundException(String.valueOf(animal.getId())));
        animalRepository.delete(animalFound);
        return animalFound;
    }

    /**
     * This method using method repository, allows get all not busy Animals
     *
     * @return  List<Animal>
     */
    public List<Animal> getAllNotBusyAnimals() {
        return animalRepository.getAllNotBusyAnimals();
    }

    /**
     * This method using method repository, allows get not complement Animals
     *
     * @return  Animal
     */
    public Animal getNotComplement() {
        return animalRepository.getNotComplement();
    }

    /**
     * This method using method repository, allows get all  Animal Type
     *
     * @return List<AnimalType>
     */
    public List<AnimalType> getAllAnimalType() {
        return animalTypeService.getAll();
    }


    /**
     * This method using method repository, allows update Animal
     *
     * @param idAnimal is not null
     * @param idAnimalType is not null
     * @return Animal
     */
    public Animal updateAnimal(Long idAnimal, Long idAnimalType) {
        Animal animal = animalRepository.findById(idAnimal).orElseThrow(() ->
                new AnimalNotFoundException(String.valueOf(idAnimal)));
        AnimalType animalType = animalTypeService.findAnimalType(idAnimalType);
        animal.setAnimalType(animalType);
        return animalRepository.save(animal);
    }

}
