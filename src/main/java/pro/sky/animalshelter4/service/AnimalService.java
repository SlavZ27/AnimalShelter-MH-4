package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Animal;
import pro.sky.animalshelter4.entity.AnimalType;
import pro.sky.animalshelter4.entityDto.AnimalDto;
import pro.sky.animalshelter4.entityDto.ChatDto;
import pro.sky.animalshelter4.exception.AnimalNotFoundException;
import pro.sky.animalshelter4.repository.AnimalRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalService {
    public final static String MESSAGE_ANIMALS_IS_ABSENT = "Sorry. Animals are absent";
    public final static String MESSAGE_ANIMAL_NOT_FOUND = "Sorry. Animals not found";
    public final static String CAPTION_SELECT_ANIMAL = "Select animal";
    public final static String MESSAGE_ANIMAL_CREATED = "Created animal";
    public final static String MESSAGE_ANIMAL_UPDATER = "Updated animal";
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

    public Animal addAnimal(Animal animal) {
        logger.info("Method addAnimal was start for create new Animal");
        return animalRepository.save(animal);
    }

    public AnimalDto createAnimalDto(AnimalDto animalDto) {
        logger.info("Method createAnimalDto was start for create new Animal");
        return dtoMapperService.toDto(animalRepository.save(dtoMapperService.toEntity(animalDto)));
    }

    public AnimalDto readAnimal(Long id) {
        logger.info("Method readAnimal was start for find animal by id");
        return dtoMapperService.toDto(
                animalRepository.findById(id).
                        orElseThrow(() -> new AnimalNotFoundException(String.valueOf(id))));
    }

    public List<AnimalDto> getAll() {
        logger.info("Method getAllAnimals was start for get all animal");
        return animalRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }


    public Animal findAnimal(Long id) {
        logger.info("Method findAnimal was start for find Animal by id");
        return animalRepository.findById(id).
                orElseThrow(() -> new AnimalNotFoundException(String.valueOf(id)));
    }

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

    public AnimalDto deleteAnimal(Long id) {
        Animal animal = new Animal();
        animal.setId(id);
        return dtoMapperService.toDto(deleteAnimal(animal));
    }

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

    public List<Animal> getAllNotBusyAnimals() {
        return animalRepository.getAllNotBusyAnimals();
    }

    public Animal getNotComplement() {
        return animalRepository.getNotComplement();
    }

    public List<AnimalType> getAllAnimalType() {
        return animalTypeService.getAll();
    }


    public Animal updateAnimal(Long idAnimal, Long idAnimalType) {
        Animal animal = animalRepository.findById(idAnimal).orElseThrow(() ->
                new AnimalNotFoundException(String.valueOf(idAnimal)));
        AnimalType animalType = animalTypeService.findAnimalType(idAnimalType);
        animal.setAnimalType(animalType);
        return animalRepository.save(animal);
    }

}
