package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Report;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.entityDto.AnimalOwnershipDto;
import pro.sky.animalshelter4.exception.AnimalOwnershipAlreadyCloseException;
import pro.sky.animalshelter4.exception.AnimalOwnershipNotFoundException;
import pro.sky.animalshelter4.repository.AnimalOwnershipRepository;
import pro.sky.animalshelter4.repository.AnimalRepository;

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
    public final static int count_extended_days = 7;


    private final AnimalOwnershipRepository animalOwnershipRepository;
    private final ReportService reportService;
    private final DtoMapperService dtoMapperService;

    private final Logger logger = LoggerFactory.getLogger(AnimalOwnershipService.class);


    public AnimalOwnershipService(AnimalOwnershipRepository animalOwnershipRepository, ReportService reportService, DtoMapperService dtoMapperService) {
        this.animalOwnershipRepository = animalOwnershipRepository;
        this.reportService = reportService;
        this.dtoMapperService = dtoMapperService;
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
    public AnimalOwnershipDto createAnimalOwnership(AnimalOwnershipDto animalOwnershipDto) {
        logger.info("Method createAnimalOwnership was start for create new animalOwnership");
        return dtoMapperService.toDto(animalOwnershipRepository.save(dtoMapperService.toEntity(animalOwnershipDto)));
    }

    /**
     * This method using method repository, allows read AnimalOwnership
     *
     * @param id is not null
     * @return AnimalOwnershipDto
     */
    public AnimalOwnershipDto readAnimalOwnership(Long id) {
        logger.info("Method readAnimalOwnershipDto was start for find animalOwnership by id");
        return dtoMapperService.toDto(
                animalOwnershipRepository.findById(id).
                        orElseThrow(() -> new AnimalOwnershipNotFoundException(String.valueOf(id))));
    }

    /**
     * This method using method repository, allows get all AnimalOwnership
     *
     * @return List<AnimalOwnershipDto>
     */
    public List<AnimalOwnershipDto> getAll() {
        logger.info("Method getAll was start for get all AnimalOwnership");
        return animalOwnershipRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    /**
     * This method using method repository, allows find AnimalOwnership
     *
     * @param id is not null
     * @return AnimalOwnershipDto
     */
    public AnimalOwnership findAnimalOwnership(Long id) {
        logger.info("Method findAnimal was start for find AnimalOwnership by id");
        return animalOwnershipRepository.findById(id).
                orElseThrow(() -> new AnimalOwnershipNotFoundException(String.valueOf(id)));
    }

    /**
     * This method using method repository allows update AnimalOwnership
     *
     * @param animalOwnershipDto is not null
     * @return animalOwnershipDto
     */
    public AnimalOwnershipDto updateAnimalOwnership(AnimalOwnershipDto animalOwnershipDto) {
        logger.info("Method updateAnimalOwnership was start for update AnimalOwnership");
        AnimalOwnership newAnimalOwnership = dtoMapperService.toEntity(animalOwnershipDto);
        AnimalOwnership oldAnimalOwnership = findAnimalOwnership(newAnimalOwnership.getId());
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
    public AnimalOwnershipDto deleteAnimalOwnership(Long id) {
        AnimalOwnership animalOwnership = new AnimalOwnership();
        animalOwnership.setId(id);
        return dtoMapperService.toDto(deleteAnimalOwnership(animalOwnership));
    }
    /**
     * This method using method repository allows del AnimalOwnership
     *
     * @param animalOwnership is not null
     * @return animalOwnershipDto
     */
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

    /**
     * This method using method repository allows get actual AnimalOwnership
     * @param userOwner is not null
     * @return AnimalOwnership
     */
    public AnimalOwnership getActualAnimalOwnership(User userOwner) {
        return animalOwnershipRepository.getActualAnimalOwnership(userOwner.getId(), LocalDate.now());
    }

    /**
     * This method using method repository allows find or create actual report
     *
     * @param userOwner is not null
     * @return animalOwnership
     */
    public Report findOrCreateActualReport(User userOwner) {
        AnimalOwnership animalOwnership = getActualAnimalOwnership(userOwner);
        if (animalOwnership == null) {
            throw new AnimalOwnershipNotFoundException("Actual");
        }
        return reportService.findOrCreateActualReport(animalOwnership);
    }

    /**
     * This method using method repository allows create report
     *
     * @param userOwner is not null
     * @return animalOwnership, diet, feeling, behavior, idMedia
     */
    public Report createReport(User userOwner, String diet, String feeling, String behavior, String idMedia) {
        AnimalOwnership animalOwnership = getActualAnimalOwnership(userOwner);
        if (animalOwnership == null) {
            throw new AnimalOwnershipNotFoundException("Actual");
        }
        return reportService.createUpdateReport(animalOwnership, diet, feeling, behavior, idMedia);
    }

    /**
     * This method using method repository allows get open and not approve report
     *
     * @return animalOwnership
     */
    public Report getOpenAndNotApproveReport() {
        return reportService.getOpenAndNotApproveReport();
    }

    /**
     * This method using method repository allows approve report
     *
     * @param idReport is not null
     * @param approve is not null
     * @return idReport, approve
     */
    public Report approveReport(Long idReport, boolean approve) {
        return reportService.approveReport(idReport, approve);
    }

    /**
     * This method using method repository allows get One Not Approve Open Animal Ownership With Not Trial
     *
     * @return getOneNotApproveOpenAnimalOwnershipWithNotTrial(localDateNow)
     */
    public AnimalOwnership getOneNotApproveOpenAnimalOwnershipWithNotTrial() {
        LocalDate localDateNow = LocalDate.now();
        return animalOwnershipRepository.getOneNotApproveOpenAnimalOwnershipWithNotTrial(localDateNow);
    }

    /**
     * This method using method repository allows approve AnimalOwnership
     *
     * @param idAnimalOwnership is not null
     * @param approve is not null
     * @return animalOwnership
     */
    public AnimalOwnership approveAnimalOwnership(Long idAnimalOwnership, boolean approve) {
        AnimalOwnership animalOwnership = findAnimalOwnership(idAnimalOwnership);
        if (animalOwnership == null) {
            throw new AnimalOwnershipNotFoundException(idAnimalOwnership.toString());
        }
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
    public AnimalOwnership extendTrialAnimalOwnershipForAWeek(Long idAnimalOwnership) {
        AnimalOwnership animalOwnership = findAnimalOwnership(idAnimalOwnership);
        if (animalOwnership == null) {
            throw new AnimalOwnershipNotFoundException(idAnimalOwnership.toString());
        }
        animalOwnership.setDateEndTrial(animalOwnership.getDateEndTrial().plusDays(count_extended_days));
        return animalOwnershipRepository.save(animalOwnership);
    }
}
