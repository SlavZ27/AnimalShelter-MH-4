package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Photo;
import pro.sky.animalshelter4.entity.Report;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.exception.AnimalNotFoundException;
import pro.sky.animalshelter4.exception.AnimalOwnershipNotFoundException;
import pro.sky.animalshelter4.repository.AnimalOwnershipRepository;

@Service
public class AnimalOwnershipService {
    public final static String MESSAGE_SUCCESSFUL_CREATION = "OK.";

    private final AnimalOwnershipRepository animalOwnershipRepository;
    private final ReportService reportService;

    private final Logger logger = LoggerFactory.getLogger(AnimalOwnershipService.class);


    public AnimalOwnershipService(AnimalOwnershipRepository animalOwnershipRepository, ReportService reportService) {
        this.animalOwnershipRepository = animalOwnershipRepository;
        this.reportService = reportService;
    }

    public AnimalOwnership addAnimalOwnership(AnimalOwnership animalOwnership) {
        logger.info("Method addAnimal was start for create new AnimalOwnership");
        return animalOwnershipRepository.save(animalOwnership);
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

    public AnimalOwnership findAnimalOwnership(Long id) {
        logger.info("Method findAnimal was start for find AnimalOwnership by id");
        return animalOwnershipRepository.findById(id).
                orElseThrow(() -> new AnimalOwnershipNotFoundException(String.valueOf(id)));
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

    public AnimalOwnership deleteAnimalOwnership(AnimalOwnership animalOwnership) {
        logger.info("Method deleteAnimalOwnership was start for delete AnimalOwnership");
        if (animalOwnership.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of AnimalOwnership");
        }
        AnimalOwnership animalOwnershipFound = animalOwnershipRepository.findById(animalOwnership.getId()).
                orElseThrow(() -> new AnimalOwnershipNotFoundException(String.valueOf(animalOwnership.getId())));
        animalOwnershipRepository.delete(animalOwnershipFound);
        return animalOwnershipFound;
    }

    public AnimalOwnership getActualAnimalOwnership(User userOwner) {
        return animalOwnershipRepository.getActualAnimalOwnership(userOwner.getId());
    }

    public Report findOrCreateActualReport(User userOwner) {
        AnimalOwnership animalOwnership = getActualAnimalOwnership(userOwner);
        if (animalOwnership == null) {
            throw new AnimalOwnershipNotFoundException();
        }
        return reportService.findOrCreateActualReport(animalOwnership);
    }

    public Report createReport(User userOwner, String diet, String feeling, String behavior, String idMedia) {
        AnimalOwnership animalOwnership = getActualAnimalOwnership(userOwner);
        if (animalOwnership == null) {
            throw new AnimalOwnershipNotFoundException();
        }
        return reportService.createUpdateReport(animalOwnership, diet, feeling, behavior, idMedia);
    }
}
