package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Photo;
import pro.sky.animalshelter4.entity.Report;
import pro.sky.animalshelter4.entity.Shelter;
import pro.sky.animalshelter4.entityDto.ReportDto;
import pro.sky.animalshelter4.exception.ReportNotFoundException;
import pro.sky.animalshelter4.exception.ShelterNotFoundException;
import pro.sky.animalshelter4.repository.PhotoRepository;
import pro.sky.animalshelter4.repository.ReportRepository;
import pro.sky.animalshelter4.repository.ShelterRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class handles ovner requests, using repository and other service
 */
@Service
public class ReportService {
    public final static String MESSAGE_REPORT_CREATE = "Report create";
    public final static String MESSAGE_SEND_PHOTO = "Send photo";
    public final static String MESSAGE_WRITE_DIET = "Write diet";
    public final static String MESSAGE_WRITE_FEELING = "Write feeling";
    public final static String MESSAGE_WRITE_BEHAVIOR = "Write behavior";
    public final static String MESSAGE_REPORT_IS_PLACED_GOOD = "Report is placed good";
    public final static String MESSAGE_REPORT_IS_PLACED_BAD = "Report is placed bad";
    public final static String MESSAGE_REPORT_IS_PLACED_BAD_OWNER = "Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно, как необходимо. Пожалуйста, подойди ответственнее к этому занятию. В противном случае волонтеры приюта будут обязаны самолично проверять условия содержания собаки";
    public final static String MESSAGE_ALL_REPORT_ARE_APPROVE = "All report are approve";
    public final static String MESSAGE_NOTIFICATION_ABOUT_REPORT = "Don't forget to send pet reports every day";
    public final static String MESSAGE_NEED_CONTACT_OWNER = "Problem with reports. Need contact with owners by AnimalOwnerships:\n";
    public final static String MESSAGE_APPROVE_OR_NOT = "Good or bad?";
    public final static String BUTTON_GOOD = "good";
    public final static String BUTTON_BAD = "bad";
    private final ReportRepository reportRepository;
    private final PhotoService photoService;

    private final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final PhotoRepository photoRepository;
    private final DtoMapperService dtoMapperService;
    private final ShelterRepository shelterRepository;


    public ReportService(ReportRepository reportRepository, PhotoService photoService,
                         PhotoRepository photoRepository, DtoMapperService dtoMapperService, ShelterRepository shelterRepository) {
        this.reportRepository = reportRepository;
        this.photoService = photoService;
        this.photoRepository = photoRepository;
        this.dtoMapperService = dtoMapperService;
        this.shelterRepository = shelterRepository;
    }

    /**
     * This method, using the method repository, allows you to create a new report
     * Using{@link ReportRepository#save(Object)}
     *
     * @param report is not null
     * @return new report
     */
    public Report addReport(Report report) {
        logger.info("Method addReport was start for create new Report");
        return reportRepository.save(report);
    }

    /**
     * This method, using the method repository, allows you to create a new report
     * Using{@link ReportRepository#save(Object)}
     *
     * @param reportDto is not null
     * @return new report
     */
    public ReportDto createReport(ReportDto reportDto, String shelterDesignation) {
        logger.info("Method createReport was start for create new report");
        Report report = dtoMapperService.toEntity(reportDto, shelterDesignation);
        report.setId(null);
        return dtoMapperService.toDto(reportRepository.save(report));
    }

    /**
     * This method, using the method class, allows find report by id
     * Using{@link ReportService#findReport(Long, Shelter)}
     *
     * @param id is not null
     * @return report
     */
    public ReportDto readReport(Long id, String shelterDesignation) {
        logger.info("Method readReport was start for find report by id");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        return dtoMapperService.toDto(findReport(id, shelter));
    }


    /**
     * This method, using the method repository, allows find report by id
     * Using {@link ReportRepository#findById(Object)}
     *
     * @param id is not null
     * @return report
     */
    public Report findReport(Long id, Shelter shelter) {
        logger.info("Method findReport was start for find Report by id");
        return reportRepository.getByIdWithShelter(id, shelter.getId()).
                orElseThrow(() -> new ReportNotFoundException(String.valueOf(id)));
    }

    /**
     * This method, using the method repository, finds all report
     * Using {@link ReportRepository#findAll()}
     *
     * @return list report
     */
    public List<ReportDto> getAll(String shelterDesignation) {
        logger.info("Method getAll was start for get all Report");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        return reportRepository.getAllWithShelter(shelter.getId()).stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    /**
     * This method,using the method repository and dto class dtoMapperService , allows update old report by reportDto
     * Using {@link DtoMapperService#toEntity(ReportDto, String)}
     * Using {@link ReportRepository#save(Object)}
     *
     * @param reportDto is not null
     * @return new report
     */
    public ReportDto updateReport(ReportDto reportDto, String shelterDesignation) {
        logger.info("Method updateCallRequest was start for update callRequest");
        Report newReport = dtoMapperService.toEntity(reportDto, shelterDesignation);
        Report oldReport = findReport(newReport.getId(), newReport.getShelter());
        if (oldReport == null) {
            throw new ReportNotFoundException(String.valueOf(newReport.getId()));
        }
        oldReport.setAnimalOwnership(newReport.getAnimalOwnership());
        oldReport.setReportDate(newReport.getReportDate());
        oldReport.setDiet(newReport.getDiet());
        oldReport.setFeeling(newReport.getFeeling());
        oldReport.setBehavior(newReport.getBehavior());
        oldReport.setPhoto(newReport.getPhoto());
        oldReport.setApprove(newReport.isApprove());
        return dtoMapperService.toDto(reportRepository.save(oldReport));
    }

    /**
     * This method,using  the method class, allows del finished report by id
     * Using{@link DtoMapperService#toDto(Report)}
     *
     * @param id is not null
     * @return del report
     */
    public ReportDto deleteReport(Long id, String shelterDesignation) {
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        Report report = new Report();
        report.setId(id);
        return dtoMapperService.toDto(deleteReport(report, shelter));
    }

    /**
     * This method,using  the method repository, allows del finished report by report
     * Using{@link ReportRepository#findById(Object)}
     * Using{@link ReportRepository#delete(Object)}
     *
     * @param report is not null
     * @return del report
     */
    public Report deleteReport(Report report, Shelter shelter) {
        logger.info("Method deleteReport was start for delete Report");
        if (report.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of Report");
        }
        Report reportFound = reportRepository.getByIdWithShelter(report.getId(), shelter.getId()).
                orElseThrow(() -> new ReportNotFoundException(String.valueOf(report.getId())));
        reportRepository.delete(reportFound);
        return reportFound;
    }

    /**
     * This method, using the method repository, find or create new report by id Ownership animal
     * Using {@link ReportRepository#findReportByIdAnimalOwnershipAndDateWithShelter(Long, LocalDate, Long)}
     * Using {@link ReportRepository#save(Object)}
     *
     * @param animalOwnership is not null
     * @return report
     */
    public Report findOrCreateActualReportWithOwnership(AnimalOwnership animalOwnership) {
        logger.info("Method findOrCreateActualReportWithOwnership was start");
        LocalDate localDate = LocalDate.now();
        Report report = reportRepository.findReportByIdAnimalOwnershipAndDateWithShelter(
                animalOwnership.getId(),
                localDate,
                animalOwnership.getShelter().getId());
        if (report == null) {
            report = new Report();
            report.setAnimalOwnership(animalOwnership);
            report.setReportDate(LocalDate.now());
            return reportRepository.save(report);
        }
        return report;
    }

    /**
     * This method,using  the method repository,allows generate update report, and using class
     * Using{@link PhotoRepository#findByIdPhoto(String)}
     * Using{@link PhotoService#addPhoto(Photo)}
     * Using{@link ReportRepository#save(Object)}
     *
     * @param animalOwnership
     * @param diet
     * @param feeling
     * @param behavior
     * @param idMedia         is not null
     * @return report
     */
    public Report updateReportWithAnimalOwnership(AnimalOwnership animalOwnership, String diet, String feeling, String behavior, String idMedia) {
        Report report = findOrCreateActualReportWithOwnership(animalOwnership);

        report.setAnimalOwnership(animalOwnership);
        if (diet != null) {
            report.setDiet(diet);
        }
        if (feeling != null) {
            report.setFeeling(feeling);
        }
        if (behavior != null) {
            report.setBehavior(behavior);
        }
        if (idMedia != null) {
            Photo photo = photoRepository.findByIdPhoto(idMedia);
            if (photo == null) {
                photo = new Photo();
            }
            photo.setIdMedia(idMedia);
            photo = photoService.addPhoto(photo);
            report.setPhoto(photo);
        }
        report.setReportDate(LocalDate.now());
        return reportRepository.save(report);
    }

    /**
     * This method, using the method repository , allow find open and not approve report
     * Using{@link ReportRepository#getOpenAndNotApproveReportWithShelter(Long)}
     *
     * @return report
     */
    public Report getOpenAndNotApproveReportWithShelter(Shelter shelter) {
        return reportRepository.getOpenAndNotApproveReportWithShelter(shelter.getId());
    }

    /**
     * This method, using the methods repository, allows you to approve a report by the report ID
     * Using{@link ReportRepository#findById(Object)}
     * Using{@link ReportRepository#save(Object)}
     *
     * @param idReport is not null
     * @param approve  is not null
     * @return report
     */
    public Report approveReport(Shelter shelter, Long idReport, boolean approve) {
        Report report = reportRepository.getByIdWithShelter(idReport, shelter.getId()).orElseThrow(() ->
                new ReportNotFoundException(idReport.toString()));
        report.setApprove(approve);
        return reportRepository.save(report);
    }
}
