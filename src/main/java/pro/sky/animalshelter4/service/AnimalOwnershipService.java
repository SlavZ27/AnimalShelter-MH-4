package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Report;
import pro.sky.animalshelter4.entity.Shelter;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.entityDto.AnimalDto;
import pro.sky.animalshelter4.entityDto.AnimalOwnershipDto;
import pro.sky.animalshelter4.exception.*;
import pro.sky.animalshelter4.repository.AnimalOwnershipRepository;
import pro.sky.animalshelter4.repository.AnimalRepository;
import pro.sky.animalshelter4.repository.ShelterRepository;
import pro.sky.animalshelter4.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * * This class is necessary to manage actions with the owner and the animal
 * The class must have many dependencies so that it can work correctly.
 * * And also respond to requests received from {@link AnimalRepository}
 */
@Service
public class AnimalOwnershipService {
    public final static String MESSAGE_SUCCESSFUL_CREATION = "OK.";
    public final static String MESSAGE_ALREADY_CLOSE = "AnimalOwnership already close";
    public final static String MESSAGE_TRIAL_IS_OVER = "Trial period of animal ownership is over:\n";
    public final static String MESSAGE_ALL_ANIMAL_OWNERSHIP_ARE_APPROVE = "All AnimalOwnership are approve";
    public final static String MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_GOOD = "AnimalOwnership is placed good";
    public final static String MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_BAD = "AnimalOwnership is placed bad";
    public final static String MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_BAD_OWNER = "Recommendations for the owner";
    public final static int COUNT_EXTENDED_DAYS_1 = 7;
    public final static int COUNT_EXTENDED_DAYS_2 = 30;


    private final AnimalOwnershipRepository animalOwnershipRepository;
    private final ReportService reportService;
    private final DtoMapperService dtoMapperService;
    private final ShelterRepository shelterRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;


    private final Logger logger = LoggerFactory.getLogger(AnimalOwnershipService.class);


    public AnimalOwnershipService(AnimalOwnershipRepository animalOwnershipRepository, ReportService reportService, DtoMapperService dtoMapperService, ShelterRepository shelterRepository, AnimalRepository animalRepository, UserRepository userRepository) {
        this.animalOwnershipRepository = animalOwnershipRepository;
        this.reportService = reportService;
        this.dtoMapperService = dtoMapperService;
        this.shelterRepository = shelterRepository;
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
    }

    /**
     * This method using method repository, allows add AnimalOwnership
     *
     * @param animalOwnership is not null
     * @return animalOwnership
     */
    public AnimalOwnership addAnimalOwnership(AnimalOwnership animalOwnership) {
        logger.info("Method addAnimal was start for create new AnimalOwnership");
        return animalOwnershipRepository.save(animalOwnership);
    }

    /**
     * This method using method repository, allows create AnimalOwnership
     *
     * @param animalOwnershipDto is not null
     * @return animalOwnershipDto
     */
    public AnimalOwnershipDto createAnimalOwnership(AnimalOwnershipDto animalOwnershipDto, String shelterDesignation) {
        logger.info("Method createAnimalOwnership was start for create new animalOwnership");
        AnimalOwnership animalOwnership = dtoMapperService.toEntity(animalOwnershipDto, shelterDesignation);
        animalOwnership.setId(null);
        return dtoMapperService.toDto(animalOwnershipRepository.save(animalOwnership));
    }

    /**
     * This method using method repository, allows read AnimalOwnership
     *
     * @param id is not null
     * @return AnimalOwnershipDto
     */
    public AnimalOwnershipDto readAnimalOwnership(Long id, String shelterDesignation) {
        logger.info("Method readAnimalOwnershipDto was start for find animalOwnership by id");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        return dtoMapperService.toDto(
                animalOwnershipRepository.getByIdWithIdShelter(id, shelter.getId()).
                        orElseThrow(() -> new AnimalOwnershipNotFoundException(String.valueOf(id))));
    }

    /**
     * This method using method repository, allows get all AnimalOwnership
     *
     * @return List<AnimalOwnershipDto>
     */
    public List<AnimalOwnershipDto> getAll(String shelterDesignation) {
        logger.info("Method getAll was start for get all AnimalOwnership");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        return animalOwnershipRepository.getAllWithIdShelter(shelter.getId()).stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    /**
     * This method using method repository, allows find AnimalOwnership
     *
     * @param id is not null
     * @return AnimalOwnershipDto
     */
    private AnimalOwnership findAnimalOwnershipWithShelter(Long id, Shelter shelter) {
        logger.info("Method findAnimal was start for find AnimalOwnership by id");
        return animalOwnershipRepository.getByIdWithIdShelter(id, shelter.getId()).
                orElseThrow(() -> new AnimalOwnershipNotFoundException(String.valueOf(id)));
    }

    /**
     * This method using method repository allows update AnimalOwnership
     *
     * @param animalOwnershipDto is not null
     * @return animalOwnershipDto
     */
    public AnimalOwnershipDto updateAnimalOwnership(AnimalOwnershipDto animalOwnershipDto, String shelterDesignation) {
        logger.info("Method updateAnimalOwnership was start for update AnimalOwnership");
        AnimalOwnership newAnimalOwnership = dtoMapperService.toEntity(animalOwnershipDto, shelterDesignation);
        AnimalOwnership oldAnimalOwnership = findAnimalOwnershipWithShelter(
                newAnimalOwnership.getId(),
                newAnimalOwnership.getShelter());
        if (oldAnimalOwnership == null) {
            throw new AnimalOwnershipNotFoundException(String.valueOf(newAnimalOwnership.getId()));
        }
        oldAnimalOwnership.setOwner(newAnimalOwnership.getOwner());
        oldAnimalOwnership.setAnimal(newAnimalOwnership.getAnimal());
        oldAnimalOwnership.setDateStartOwn(newAnimalOwnership.getDateStartOwn());
        oldAnimalOwnership.setDateEndTrial(newAnimalOwnership.getDateEndTrial());
        oldAnimalOwnership.setAnimal(newAnimalOwnership.getAnimal());
        oldAnimalOwnership.setOpen(newAnimalOwnership.isOpen());
        return dtoMapperService.toDto(animalOwnershipRepository.save(oldAnimalOwnership));
    }

    /**
     * This method using method repository allows del AnimalOwnership
     *
     * @param id is not null
     * @return animalOwnershipDto
     */
    public AnimalOwnershipDto deleteAnimalOwnership(Long id, String shelterDesignation) {
        AnimalOwnership animalOwnership = new AnimalOwnership();
        animalOwnership.setId(id);
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        return dtoMapperService.toDto(deleteAnimalOwnership(animalOwnership, shelter));
    }

    /**
     * This method using method repository allows del AnimalOwnership
     *
     * @param animalOwnership is not null
     * @return animalOwnershipDto
     */
    public AnimalOwnership deleteAnimalOwnership(AnimalOwnership animalOwnership, Shelter shelter) {
        logger.info("Method deleteAnimalOwnership was start for delete AnimalOwnership");
        if (animalOwnership.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of AnimalOwnership");
        }
        AnimalOwnership animalOwnershipFound = animalOwnershipRepository.getByIdWithIdShelter(
                        animalOwnership.getId(),
                        shelter.getId()).
                orElseThrow(() -> new AnimalOwnershipNotFoundException(String.valueOf(animalOwnership.getId())));
        animalOwnershipRepository.delete(animalOwnershipFound);
        return animalOwnershipFound;
    }

    /**
     * This method using method repository allows get actual AnimalOwnership
     *
     * @param userOwner is not null
     * @return AnimalOwnership
     */
    public AnimalOwnership getActualAnimalOwnershipWithShelter(User userOwner) {
        return animalOwnershipRepository.getActualAnimalOwnershipWithIdShelter(
                userOwner.getShelter().getId(),
                userOwner.getId(), LocalDate.now());
    }

    /**
     * This method using method repository allows find or create actual report
     *
     * @param userOwner is not null
     * @return animalOwnership
     */
    public Report findOrCreateTodayReportWithOwner(User userOwner) {
        AnimalOwnership animalOwnership = getActualAnimalOwnershipWithShelter(userOwner);
        if (animalOwnership == null) {
            throw new AnimalOwnershipNotFoundException("");
        }
        return reportService.findOrCreateActualReportWithOwnership(animalOwnership);
    }

    /**
     * This method using method repository allows create report
     *
     * @param userOwner is not null
     * @return animalOwnership, diet, feeling, behavior, idMedia
     */
    public Report updateReportWithOwner(User userOwner, String diet, String feeling, String behavior, String idMedia) {
        AnimalOwnership animalOwnership = getActualAnimalOwnershipWithShelter(userOwner);
        if (animalOwnership == null) {
            throw new AnimalOwnershipNotFoundException("");
        }
        return reportService.updateReportWithAnimalOwnership(animalOwnership, diet, feeling, behavior, idMedia);
    }

    /**
     * This method using method repository allows get open and not approve report
     *
     * @return animalOwnership
     */
    public Report getOpenAndNotApproveReportWithShelter(Shelter shelter) {
        return reportService.getOpenAndNotApproveReportWithShelter(shelter);
    }

    /**
     * This method using method repository allows approve report
     *
     * @param idReport is not null
     * @param approve  is not null
     * @return idReport, approve
     */
    public Report approveReportWithShelter(Long idReport, Shelter shelter, boolean approve) {
        return reportService.approveReport(shelter, idReport, approve);
    }

    /**
     * This method using method repository allows get One Not Approve Open Animal Ownership With Not Trial
     *
     * @return getOneNotApproveOpenAnimalOwnershipWithNotTrial(localDateNow)
     */
    public AnimalOwnership getOneNotApproveOpenAnimalOwnershipWithNotTrialWithShelter(Shelter shelter) {
        LocalDate localDateNow = LocalDate.now();
        return animalOwnershipRepository.getOneNotApproveOpenAnimalOwnershipWithNotTrialWithIdShelter(shelter.getId(), localDateNow);
    }

    /**
     * This method using method repository allows approve AnimalOwnership
     *
     * @param idAnimalOwnership is not null
     * @param approve           is not null
     * @return animalOwnership
     */
    public AnimalOwnership approveAnimalOwnershipWithShelter(Shelter shelter, Long idAnimalOwnership, boolean approve) {
        AnimalOwnership animalOwnership = animalOwnershipRepository.
                getByIdWithIdShelter(idAnimalOwnership, shelter.getId()).
                orElseThrow(() -> new AnimalOwnershipNotFoundException(idAnimalOwnership.toString()));
        if (!animalOwnership.isOpen()) {
            throw new AnimalOwnershipAlreadyCloseException(idAnimalOwnership.toString());
        }
        animalOwnership.setApprove(approve);
        animalOwnership.setOpen(false);
        return animalOwnershipRepository.save(animalOwnership);
    }

    /**
     * This method using method repository allows extend trial animal ownership for a week
     *
     * @param idAnimalOwnership is not null
     * @return animalOwnership
     */
    public AnimalOwnership extendTrialAnimalOwnershipWithShelter(Shelter shelter, Long idAnimalOwnership, int countDays) {
        AnimalOwnership animalOwnership = animalOwnershipRepository.
                getByIdWithIdShelter(idAnimalOwnership, shelter.getId()).
                orElseThrow(() -> new AnimalOwnershipNotFoundException(idAnimalOwnership.toString()));
        animalOwnership.setDateEndTrial(animalOwnership.getDateEndTrial().plusDays(countDays));
        return animalOwnershipRepository.save(animalOwnership);
    }
}
