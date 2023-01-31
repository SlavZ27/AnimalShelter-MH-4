package pro.sky.animalshelter4.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Report;
import pro.sky.animalshelter4.entity.Shelter;
import pro.sky.animalshelter4.repository.AnimalOwnershipRepository;
import pro.sky.animalshelter4.repository.ReportRepository;
import pro.sky.animalshelter4.repository.ShelterRepository;
import pro.sky.animalshelter4.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    public final static int COUNT_LATE_DAY = 1;
    public final static int COUNT_LATE_DAY_VERY_BAD = 2;
    public final static int DELAY_NOTIFICATION_HOURS = 20;
    private final ReportRepository reportRepository;
    private final AnimalOwnershipRepository animalOwnershipRepository;
    private final ChatService chatService;
    private final ShelterRepository shelterRepository;

    public ScheduleService(ReportRepository reportRepository, AnimalOwnershipRepository animalOwnershipRepository, ChatService chatService, ShelterRepository shelterRepository) {
        this.reportRepository = reportRepository;
        this.animalOwnershipRepository = animalOwnershipRepository;
        this.chatService = chatService;
        this.shelterRepository = shelterRepository;
    }

// в случае, если пользователь не присылает информацию, напоминать об
// этом, а если проходит более 2 дней, то отправлять запрос волонтеру на
// связь с усыновителем.*
//Как только период в 30 дней заканчивается, волонтеры принимают решение о том, остается собака у
// хозяина или нет. Испытательный срок может быть пройден, может быть продлен на любое количество дней, а
// может быть не пройден.*


    @Scheduled(fixedDelay = 3_600_000)
    public void checkLateReportsAndNoReportsWithTime() {
        if (10 < LocalTime.now().getHour() && LocalTime.now().getHour() < 20) {
            checkLateReportsAndNoReports();
        }
    }

    @Scheduled(fixedDelay = 40_000_000)
    public void checkNotApproveOpenAnimalOwnershipWithNotTrialWithTime() {
        if (10 < LocalTime.now().getHour() && LocalTime.now().getHour() < 20) {
            checkNotApproveOpenAnimalOwnershipWithNotTrial();
        }
    }

    /**
     * This method check report Client AnimalOwnership
     */
    public void checkLateReportsAndNoReports() {
        List<Shelter> shelterList = shelterRepository.findAll();
        for (int i = 0; i < shelterList.size(); i++) {
            Shelter shelter = shelterList.get(i);

            LocalDate localDateLate = LocalDate.now().minusDays(COUNT_LATE_DAY);
            LocalDate localDateViolators = LocalDate.now().minusDays(COUNT_LATE_DAY_VERY_BAD);

            List<AnimalOwnership> lateList = getLateList(shelter, localDateLate);
            List<AnimalOwnership> noReportList =
                    getNoReportList(shelter, LocalDate.now().plusDays(1).minusDays(COUNT_LATE_DAY));
            lateList.addAll(noReportList);

            List<AnimalOwnership> actualLateList = lateList.stream()
                    .filter(animalOwnership ->
                            animalOwnership.getOwner().
                                    getDateLastNotification() == null ||
                                    animalOwnership.getOwner().
                                            getDateLastNotification().
                                            plusHours(DELAY_NOTIFICATION_HOURS).
                                            isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList());
            if (lateList.size() > 0) {
                chatService.sendNotificationToOwnerAboutReport(actualLateList);
            }

            List<AnimalOwnership> violatorsList = getLateList(shelter, localDateViolators);
            List<AnimalOwnership> violatorsNoReportList =
                    getNoReportList(shelter, LocalDate.now().plusDays(0).minusDays(COUNT_LATE_DAY));
            violatorsList.addAll(violatorsNoReportList);

            if (violatorsList.size() > 0) {
                chatService.sendRequestToVolunteerToContactOwnerWithShelter(shelterList.get(i), violatorsList);
            }
        }
    }

    public List<AnimalOwnership> getLateList(Shelter shelter, LocalDate lastDate) {
        return reportRepository.getLatestUniqueOwnerReportWithOpenAnimalOwnershipWithShelter(
                        shelter.getId(),
                        LocalDate.now()).stream().
                filter(report -> report.getReportDate().isBefore(lastDate)).
                map(Report::getAnimalOwnership).
                collect(Collectors.toList());
    }

    public List<AnimalOwnership> getNoReportList(Shelter shelter, LocalDate dateStartOwnLess) {
        return animalOwnershipRepository.getAllOpenAnimalOwnershipWithoutReportsWithIdShelter(shelter.getId(), dateStartOwnLess);
    }


    public void checkNotApproveOpenAnimalOwnershipWithNotTrial() {
        List<Shelter> shelterList = shelterRepository.findAll();
        for (int i = 0; i < shelterList.size(); i++) {
            List<AnimalOwnership> animalOwnershipList =
                    animalOwnershipRepository.getNotApproveOpenAnimalOwnershipWithNotTrialWithIdShelter(
                            shelterList.get(i).getId(), LocalDate.now());
            if (animalOwnershipList.size() > 0) {
                chatService.sendNotApproveOpenAnimalOwnershipWithNotTrial(shelterList.get(i), animalOwnershipList);
            }
        }
    }
}
