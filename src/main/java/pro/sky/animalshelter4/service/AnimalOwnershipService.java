package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Report;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.exception.AnimalOwnershipNotFoundException;
import pro.sky.animalshelter4.repository.AnimalOwnershipRepository;

import java.time.LocalDate;

@Service
public class AnimalOwnershipService {
    public final static String MESSAGE_SUCCESSFUL_CREATION = "OK.";
    public final static String MESSAGE_TRIAL_IS_OVER = "Trial period of animal ownership is over:\n";
    public final static String MESSAGE_ALL_ANIMAL_OWNERSHIP_ARE_APPROVE = "All AnimalOwnership are approve";
    public final static String MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_GOOD = "AnimalOwnership is placed good";
    public final static String MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_BAD = "AnimalOwnership is placed bad";
    public final static String MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_BAD_OWNER = "Recommendations for the owner";


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
        return animalOwnershipRepository.getActualAnimalOwnership(userOwner.getId(), LocalDate.now());
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

    public Report getOpenAndNotApproveReport() {
        return reportService.getOpenAndNotApproveReport();
    }

    public Report approveReport(Long idReport, boolean approve) {
        return reportService.approveReport(idReport, approve);
    }

    public AnimalOwnership getOneNotApproveOpenAnimalOwnershipWithNotTrial() {
        return animalOwnershipRepository.getOneNotApproveOpenAnimalOwnershipWithNotTrial();
    }

    public AnimalOwnership approveAnimalOwnership(Long idAnimalOwnership, boolean approve) {
        AnimalOwnership animalOwnership = findAnimalOwnership(idAnimalOwnership);
        if (animalOwnership == null) {
            throw new AnimalOwnershipNotFoundException(idAnimalOwnership.toString());
        }
        animalOwnership.setApprove(approve);
        animalOwnership.setOpen(false);
        return animalOwnershipRepository.save(animalOwnership);
    }

    public AnimalOwnership extendTrialAnimalOwnershipForAWeek(Long idAnimalOwnership) {
        AnimalOwnership animalOwnership = findAnimalOwnership(idAnimalOwnership);
        if (animalOwnership == null) {
            throw new AnimalOwnershipNotFoundException(idAnimalOwnership.toString());
        }
        animalOwnership.setDateEndTrial(animalOwnership.getDateEndTrial().plusDays(7));
        return animalOwnershipRepository.save(animalOwnership);
    }
}
