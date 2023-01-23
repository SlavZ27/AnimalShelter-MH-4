package pro.sky.animalshelter4.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Report;
import pro.sky.animalshelter4.repository.AnimalOwnershipRepository;
import pro.sky.animalshelter4.repository.ReportRepository;
import pro.sky.animalshelter4.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ReportRepository reportRepository;
    private final AnimalOwnershipRepository animalOwnershipRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;

    public ScheduleService(ReportRepository reportRepository, AnimalOwnershipRepository animalOwnershipRepository, UserRepository userRepository, ChatService chatService) {
        this.reportRepository = reportRepository;
        this.animalOwnershipRepository = animalOwnershipRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
    }

    //В базу новых усыновителей пользователь попадает через волонтера, который его туда заносит. Задача бота
// принимать на вход информацию и
// в случае, если пользователь не присылает информацию, напоминать об
// этом, а если проходит более 2 дней, то отправлять запрос волонтеру на
// связь с усыновителем.*
//Как только период в 30 дней заканчивается, волонтеры принимают решение о том, остается собака у
// хозяина или нет. Испытательный срок может быть пройден, может быть продлен на любое количество дней, а
// может быть не пройден.*

    /**
     * This method check report Client AnimalOwnership
     */
    @Scheduled(fixedDelay = 3_600_000)
    public void checkLateReportsAndNoReports() {
        int countLateDay = 1;
        int countLateDayVeryBad = 2;
        int delayNotificationHours = 20;
        if (10 < LocalTime.now().getHour() && LocalTime.now().getHour() < 20) {
            LocalDate localDateLate = LocalDate.now().minusDays(countLateDay);
            List<AnimalOwnership> noReportList = animalOwnershipRepository.getAllOpenAnimalOwnershipWithoutReports();
            List<AnimalOwnership> lateList = reportRepository.getLatestUniqueOwnerReportWithOpenAnimalOwnership().stream().
                    filter(report -> report.getReportDate().isBefore(localDateLate)).
                    map(Report::getAnimalOwnership).
                    collect(Collectors.toList());
            lateList.addAll(noReportList);

            List<AnimalOwnership> actualLateList = lateList.stream()
                    .filter(animalOwnership ->
                            animalOwnership.getOwner().
                                    getDateLastNotification() == null ||
                                    animalOwnership.getOwner().
                                            getDateLastNotification().plusHours(delayNotificationHours).isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList());
            if (lateList.size() > 0) {
                chatService.sendNotificationAboutReport(actualLateList);
            }
            List<AnimalOwnership> actualViolatorsList = lateList.stream()
                    .filter(animalOwnership ->
                            animalOwnership.getOwner().
                                    getDateLastNotification() == null || animalOwnership.getOwner().
                                    getDateLastNotification().plusDays(countLateDayVeryBad).isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList());
            if (lateList.size() > 0) {
                chatService.sendRequestToVolunteerToContactOwner(actualLateList);
            }
        }

    }

    @Scheduled(fixedDelay = 40_000_000)
    public void checkNotApproveOpenAnimalOwnershipWithNotTrial() {
        List<AnimalOwnership> animalOwnershipList = animalOwnershipRepository.getNotApproveOpenAnimalOwnershipWithNotTrial(LocalDate.now());
        if (animalOwnershipList.size() > 0) {
            chatService.sendNotApproveOpenAnimalOwnershipWithNotTrial(animalOwnershipList);
        }
    }
}
