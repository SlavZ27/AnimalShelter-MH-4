package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Animal;
import pro.sky.animalshelter4.entity.AnimalType;
import pro.sky.animalshelter4.exception.AnimalNotFoundException;
import pro.sky.animalshelter4.repository.AnimalRepository;

import java.util.List;

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
    private final Logger logger = LoggerFactory.getLogger(AnimalService.class);

    public AnimalService(AnimalRepository animalRepository, AnimalTypeService animalTypeService) {
        this.animalRepository = animalRepository;
        this.animalTypeService = animalTypeService;
    }

    public Animal addAnimal(Animal animal) {
        logger.info("Method addAnimal was start for create new Animal");
        return animalRepository.save(animal);
    }

//    public CallRequestDto createCallRequest(CallRequestDto callRequestDto) {
//        logger.info("Method createCallRequest was start for create new CallRequest");
//        return dtoMapperService.toDto(callRequestRepository.save(dtoMapperService.toEntity(callRequestDto)));
//    }

//    public CallRequestDto readCallRequest(Long id) {
//        logger.info("Method readCallRequest was start for find CallRequest by id");
//        return dtoMapperService.toDto(
//                callRequestRepository.findById(id).
//                        orElseThrow(() -> new CallRequestNotFoundException(String.valueOf(id))));
//    }

    public Animal findAnimal(Long id) {
        logger.info("Method findAnimal was start for find Animal by id");
        return animalRepository.findById(id).
                orElseThrow(() -> new AnimalNotFoundException(String.valueOf(id)));
    }

//    public CallRequestDto updateCallRequest(CallRequestDto callRequestDto) {
//        logger.info("Method updateCallRequest was start for update callRequest");
//        CallRequest newCallRequest = dtoMapperService.toEntity(callRequestDto);
//        CallRequest oldCallRequest = findCallRequest(newCallRequest.getId());
//        if (oldCallRequest == null) {
//            throw new CallRequestNotFoundException(String.valueOf(newCallRequest.getId()));
//        }
//        oldCallRequest.setOpen(newCallRequest.isOpen());
//        oldCallRequest.setVolunteer(newCallRequest.getVolunteer());
//        oldCallRequest.setClient(newCallRequest.getClient());
//        oldCallRequest.setLocalDateTimeOpen(newCallRequest.getLocalDateTimeOpen());
//        oldCallRequest.setLocalDateTimeClose(newCallRequest.getLocalDateTimeClose());
//        return dtoMapperService.toDto(callRequestRepository.save(oldCallRequest));
//    }

//    public CallRequestDto deleteCallRequest(Long id) {
//        CallRequest callRequest = new CallRequest();
//        callRequest.setId(id);
//        return dtoMapperService.toDto(deleteCallRequest(callRequest));
//    }

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
