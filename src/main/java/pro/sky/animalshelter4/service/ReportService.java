package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Photo;
import pro.sky.animalshelter4.entity.Report;
import pro.sky.animalshelter4.entityDto.AnimalDto;
import pro.sky.animalshelter4.entityDto.ReportDto;
import pro.sky.animalshelter4.exception.ReportNotFoundException;
import pro.sky.animalshelter4.repository.PhotoRepository;
import pro.sky.animalshelter4.repository.ReportRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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


    public ReportService(ReportRepository reportRepository, PhotoService photoService,
                         PhotoRepository photoRepository, DtoMapperService dtoMapperService) {
        this.reportRepository = reportRepository;
        this.photoService = photoService;
        this.photoRepository = photoRepository;
        this.dtoMapperService = dtoMapperService;
    }

    public Report addReport(Report report) {
        logger.info("Method addReport was start for create new Report");
        return reportRepository.save(report);
    }

    public ReportDto createReport(ReportDto reportDto) {
        logger.info("Method createReport was start for create new report");
        return dtoMapperService.toDto(reportRepository.save(dtoMapperService.toEntity(reportDto)));
    }

    public ReportDto readReport(Long id) {
        logger.info("Method readReport was start for find report by id");
        return dtoMapperService.toDto(findReport(id));
    }

    public Report findReport(Long id) {
        logger.info("Method findReport was start for find Report by id");
        return reportRepository.findById(id).
                orElseThrow(() -> new ReportNotFoundException(String.valueOf(id)));
    }

    public List<ReportDto> getAll() {
        logger.info("Method getAll was start for get all Report");
        return reportRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public Report findOrCreateActualReport(AnimalOwnership animalOwnership) {
        logger.info("Method findOrCreateActualReport was start");
        LocalDate localDate = LocalDate.now();
        Report report = reportRepository.findReportByIdAnimalOwnershipAndDate(animalOwnership.getId(), localDate);
        if (report == null) {
            report = new Report();
            report.setAnimalOwnership(animalOwnership);
            report.setReportDate(LocalDate.now());
            return reportRepository.save(report);
        }
        return report;
    }

    public ReportDto updateReport(ReportDto reportDto) {
        logger.info("Method updateCallRequest was start for update callRequest");
        Report newReport = dtoMapperService.toEntity(reportDto);
        Report oldReport = findReport(newReport.getId());
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

    public ReportDto deleteReport(Long id) {
        Report report = new Report();
        report.setId(id);
        return dtoMapperService.toDto(deleteReport(report));
    }

    public Report deleteReport(Report report) {
        logger.info("Method deleteReport was start for delete Report");
        if (report.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of Report");
        }
        Report reportFound = reportRepository.findById(report.getId()).
                orElseThrow(() -> new ReportNotFoundException(String.valueOf(report.getId())));
        reportRepository.delete(reportFound);
        return reportFound;
    }

    public Report createUpdateReport(AnimalOwnership animalOwnership, String diet, String feeling, String behavior, String idMedia) {
        Report report = findOrCreateActualReport(animalOwnership);

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

    public Report getOpenAndNotApproveReport() {
        return reportRepository.getOpenAndNotApproveReport();
    }

    public Report approveReport(Long idReport, boolean approve) {
        Report report = reportRepository.findById(idReport).orElseThrow(() ->
                new ReportNotFoundException(idReport.toString()));
        report.setApprove(approve);
        return reportRepository.save(report);
    }
}
