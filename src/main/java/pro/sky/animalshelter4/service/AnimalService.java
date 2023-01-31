package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Animal;
import pro.sky.animalshelter4.entity.Shelter;
import pro.sky.animalshelter4.entityDto.AnimalDto;
import pro.sky.animalshelter4.exception.AnimalNotFoundException;
import pro.sky.animalshelter4.exception.ShelterNotFoundException;
import pro.sky.animalshelter4.repository.AnimalRepository;
import pro.sky.animalshelter4.repository.ShelterRepository;

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
    private final DtoMapperService dtoMapperService;
    private final ShelterRepository shelterRepository;
    private final Logger logger = LoggerFactory.getLogger(AnimalService.class);

    public AnimalService(AnimalRepository animalRepository, DtoMapperService dtoMapperService, ShelterRepository shelterRepository) {
        this.animalRepository = animalRepository;
        this.dtoMapperService = dtoMapperService;
        this.shelterRepository = shelterRepository;
    }
    /**
     * This method using method repository, allows adds Animal
     *
     * @param animal is not null
     * @return Animal
     */
    public Animal addAnimalWithShelter(Animal animal, Shelter shelter) {
        logger.info("Method addAnimal was start for create new Animal");
        animal.setShelter(shelter);
        return animalRepository.save(animal);
    }

    public Animal addAnimalWithShelter(String name, Shelter shelter) {
        logger.info("Method addAnimal was start for create new Animal with name = {}", name);
        Animal animal = new Animal();
        animal.setNameAnimal(name);
        return addAnimalWithShelter(animal, shelter);
    }
    /**
     * This method using method repository, allows crate AnimalDto
     * @param animalDto is not null
     * @return AnimalDto
     */
    public AnimalDto createAnimalWithShelterDto(AnimalDto animalDto, String shelterDesignation) {
        logger.info("Method createAnimalDto was start for create new Animal");
        Animal animal = dtoMapperService.toEntity(animalDto, shelterDesignation);
        animal.setId(null);
        return dtoMapperService.toDto(animalRepository.save(animal));
    }
    /**
     * This method using method repository, allows read AnimalDto
     *
     * @param id is not null
     * @return AnimalDto
     */
    public AnimalDto readAnimalWithShelter(Long id, String shelterDesignation) {
        logger.info("Method readAnimal was start for find animal by id");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        return dtoMapperService.toDto(
                animalRepository.findByIdAndIdShelter(id, shelter.getId()).
                        orElseThrow(() -> new AnimalNotFoundException(String.valueOf(id))));
    }
    /**
     * This method using method repository, allows get all AnimalDto
     *
     * @return List<AnimalDto>
     */
    public List<AnimalDto> getAllWithShelter(String shelterDesignation) {
        logger.info("Method getAllAnimals was start for get all animal");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        return animalRepository.findAllWithIdShelter(shelter.getId()).stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public Animal findAnimalWithIdNotBusyWithShelter(Long id, Shelter shelter) {
        logger.info("Method findAnimal was start for find Animal by id");
        return animalRepository.findAnimalWithIdNotBusyWithShelter(id, shelter.getId());
    }
    /**
     * This method using method repository, allows update AnimalDto
     *
     * @param animalDto is not null
     * @return AnimalDto
     */
    public AnimalDto updateAnimalWithShelter(AnimalDto animalDto, String shelterDesignation) {
        logger.info("Method updateAnimal was start for update Animal");
        Animal newAnimal = dtoMapperService.toEntity(animalDto, shelterDesignation);
        Animal oldAnimal = animalRepository.findByIdAndIdShelter(
                newAnimal.getId(),
                newAnimal.getShelter().getId()).orElseThrow(
                () -> new AnimalNotFoundException(String.valueOf(newAnimal.getId())));
        oldAnimal.setNameAnimal(newAnimal.getNameAnimal());
        oldAnimal.setBorn(newAnimal.getBorn());
        oldAnimal.setShelter(newAnimal.getShelter());
        return dtoMapperService.toDto(animalRepository.save(oldAnimal));
    }
    /**
     * This method using method repository, allows del AnimalDto
     *
     * @param id is not null
     * @return AnimalDto
     */
    public AnimalDto deleteAnimalWithShelter(Long id, String shelterDesignation) {
        Animal animal = new Animal();
        animal.setId(id);
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        return dtoMapperService.toDto(deleteAnimalWithShelter(animal, shelter));
    }    /**
     * This method using method repository, allows del Animal
     *
     * @param animal is not null
     * @return Animal
     */

    public Animal deleteAnimalWithShelter(Animal animal, Shelter shelter) {
        logger.info("Method deleteAnimal was start for delete animal");
        if (animal.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of animal");
        }
        Animal animalFound = animalRepository.findByIdAndIdShelter(animal.getId(), shelter.getId()).
                orElseThrow(() -> new AnimalNotFoundException(String.valueOf(animal.getId())));
        animalRepository.delete(animalFound);
        return animalFound;
    }

    public List<Animal> getAllNotBusyAnimalsWithShelter(Shelter shelter) {
        return animalRepository.getAllNotBusyAnimalsWithShelter(shelter.getId());
    }
}
