package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Photo;
import pro.sky.animalshelter4.entity.Report;
import pro.sky.animalshelter4.exception.ReportNotFoundException;
import pro.sky.animalshelter4.repository.PhotoRepository;
import pro.sky.animalshelter4.repository.ReportRepository;

import java.time.LocalDate;

@Service
public class ReportService {
    public final static String MESSAGE_REPORT_CREATE = "Report create";
    public final static String MESSAGE_SEND_PHOTO = "Send photo";
    public final static String MESSAGE_WRITE_DIET = "Write diet";
    public final static String MESSAGE_WRITE_FEELING = "Write feeling";
    public final static String MESSAGE_WRITE_BEHAVIOR = "Write behavior";
    public final static String MESSAGE_REPORT_IS_PLACED_GOOD = "Report is placed good";
    public final static String MESSAGE_REPORT_IS_PLACED_BAD = "Report is placed bad";
    public final static String MESSAGE_ALL_REPORT_ARE_APPROVE = "All report are approve";
    public final static String MESSAGE_APPROVE_OR_NOT = "Good or bad?";
    public final static String BUTTON_GOOD = "good";
    public final static String BUTTON_BAD = "bad";
    private final ReportRepository reportRepository;
    private final PhotoService photoService;

    private final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final PhotoRepository photoRepository;


    public ReportService(ReportRepository reportRepository, PhotoService photoService,
                         PhotoRepository photoRepository) {
        this.reportRepository = reportRepository;
        this.photoService = photoService;
        this.photoRepository = photoRepository;
    }

    public Report addReport(Report report) {
        logger.info("Method addReport was start for create new Report");
        return reportRepository.save(report);
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

    public Report findReport(Long id) {
        logger.info("Method findReport was start for find Report by id");
        return reportRepository.findById(id).
                orElseThrow(() -> new ReportNotFoundException(String.valueOf(id)));
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
        Report report = reportRepository.findById(idReport).orElseThrow(()->
                new ReportNotFoundException(idReport.toString()));
        report.setApprove(approve);
        return reportRepository.save(report);
    }
}
