package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import pro.sky.animalshelter4.Generator;
import pro.sky.animalshelter4.controller.AnimalController;
import pro.sky.animalshelter4.entity.*;
import pro.sky.animalshelter4.listener.TelegramBotUpdatesListener;
import pro.sky.animalshelter4.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ScheduleServiceTest {
    @LocalServerPort
    private int port;
    private final static String SHELTER1 = "DOG";
    private final static String SHELTER2 = "CAT";

    @InjectMocks
    @Autowired
    private ScheduleService scheduleService;
    @MockBean
    private TelegramBot telegramBot;
    @Autowired
    private AnimalOwnershipRepository animalOwnershipRepository;
    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private ShelterRepository shelterRepository;
    @Autowired
    private CallRequestRepository callRequestRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UnfinishedRequestTelegramRepository unfinishedRequestTelegramRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AnimalOwnershipService animalOwnershipService;
    @Autowired
    private AnimalService animalService;
    @Autowired
    private ShelterService shelterService;
    @Autowired
    private CallRequestService callRequestService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private CommandService commandService;
    @Autowired
    private DtoMapperService dtoMapperService;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private TelegramBotSenderService telegramBotSenderService;
    @Autowired
    private TelegramMapperService telegramMapperService;
    @Autowired
    private TelegramUnfinishedRequestService telegramUnfinishedRequestService;
    @Autowired
    private UserService userService;
    @Autowired
    private TelegramBotUpdatesService telegramBotUpdatesService;
    @MockBean
    private TelegramBotUpdatesListener telegramBotUpdatesListener;
    private final Generator generator = new Generator();
    private final Random random = new Random();
    private AnimalOwnership animalOwnership_m15_p15_m0;
    private AnimalOwnership animalOwnership_m15_p15_m1;
    private AnimalOwnership animalOwnership_m15_p15_m2;
    private AnimalOwnership animalOwnership_m15_p15_m3;
    private AnimalOwnership animalOwnership_m0;
    private AnimalOwnership animalOwnership_m1;
    private AnimalOwnership animalOwnership_m2;
    private AnimalOwnership animalOwnership_m35_m5_m5;
    private AnimalOwnership animalOwnership_m35_m5_m6;
    private AnimalOwnership animalOwnership_m35_m5_m7;
    private AnimalOwnership animalOwnership_m31_m1_m1;
    private AnimalOwnership animalOwnership_m31_m1_m2;
    private AnimalOwnership animalOwnership_m31_m1_m3;
    private AnimalOwnership animalOwnership_m30_m0_m0;
    private AnimalOwnership animalOwnership_m30_m0_m1;
    private AnimalOwnership animalOwnership_m30_m0_m2;

    @BeforeEach
    public void generateData() {
        reportRepository.deleteAll();
        photoRepository.deleteAll();
        animalOwnershipRepository.deleteAll();
        callRequestRepository.deleteAll();
        userRepository.deleteAll();
        animalRepository.deleteAll();
        unfinishedRequestTelegramRepository.deleteAll();
        chatRepository.deleteAll();
        shelterRepository.deleteAll();

//        int shelterInt = CAT DOG;
        // count of all other entities = count * shelterInt
        int userVolunteerInt = 5;
        int userClientInt = 100;
        int chatInt = userClientInt + userVolunteerInt;
        int callRequestInt = 40;                            // callRequest < chat
        int animalInt = 50;
        int animalOwnershipInt = 30;                        // animal > animalOwnership
        int reportInt = 30;
        int photoInt = reportInt;


        //generate and remember shelter
        Shelter shelterDog = new Shelter();
        shelterDog.setNameShelter("Shelter of dog");
        shelterDog.setPhone("123456789");
        shelterDog.setAddress("123 123 123");
        shelterDog.setshelterDesignation("DOG");
        Shelter shelterCat = new Shelter();
        shelterCat.setNameShelter("Shelter of cat");
        shelterCat.setPhone("987654321");
        shelterCat.setAddress("456 4564 45");
        shelterCat.setshelterDesignation("CAT");
        shelterRepository.save(shelterDog);
        shelterRepository.save(shelterCat);
        List<Shelter> shelterList = new ArrayList<>();
        shelterList.add(shelterDog);
        shelterList.add(shelterCat);

        for (int k = 0; k < shelterList.size(); k++) {
            Shelter shelter = shelterList.get(k);
            //generate and remember userVolunteer with chat
            List<User> userVolunteerList = new ArrayList<>();
            List<Chat> chatVolunteerList = new ArrayList<>();
            for (int i = 0; i < userVolunteerInt; i++) {
                //generate chat
                Chat chatV = generator.generateChat(-1L, "", "", "", null, true);
                chatV.setShelter(shelter);
                chatV = chatRepository.save(chatV);
                //generate userVolunteer with chatV
                User userVolunteer = generator.generateUser(null, null, chatV, null, null, true, null, true);
                userVolunteer.setShelter(shelter);
                userVolunteer = userRepository.save(userVolunteer);
                userVolunteerList.add(userVolunteer);
                chatVolunteerList.add(chatV);
            }
            //generate and remember userClient with chat
            List<User> userClientList = new ArrayList<>();
            List<Chat> chatClientList = new ArrayList<>();
            for (int j = 0; j < userClientInt; j++) {
                //generate chatC
                Chat chatC = generator.generateChat(-1L, "", "", "", null, true);
                chatC.setShelter(shelter);
                chatC = chatRepository.save(chatC);
                //generate userClient with chatC
                User userClient = generator.generateUser(null, null, chatC, null, null, false, null, true);
                userClient.setShelter(shelter);
                userClient = userRepository.save(userClient);
                userClientList.add(userClient);
                chatClientList.add(chatC);
            }
            //generate callRequest
            List<User> userClientList2 = new ArrayList<>(userClientList);
            for (int i = 0; i < callRequestInt; i++) {
                CallRequest callRequest = new CallRequest();
                callRequest.setVolunteer(userVolunteerList.get(random.nextInt(userVolunteerList.size())));
                int index = random.nextInt(userClientList2.size());
                callRequest.setClient(userClientList2.get(i));
                userClientList2.remove(index);
                callRequest.setOpen(generator.generateBool());
                callRequest.setLocalDateTimeClose(generator.generateDateTime(true, LocalDateTime.now()));
                callRequest.setLocalDateTimeOpen(generator.generateDateTime(true, callRequest.getLocalDateTimeClose()));
                callRequest.setShelter(shelter);
                callRequestRepository.save(callRequest);
            }
            //generate animalType
            List<Animal> animalList = new ArrayList<>();
            for (int j = 0; j < animalInt; j++) {
                //generate and remember animal
                Animal animal = new Animal();
                animal.setNameAnimal(generator.generateNameIfEmpty(null));
                animal.setBorn(generator.generateDate(true, LocalDate.now()));
                animal.setShelter(shelter);
                animal = animalRepository.save(animal);
                animalList.add(animal);
            }
            //generate and remember animalOwnership
            List<AnimalOwnership> animalOwnershipList = new ArrayList<>();
            for (int i = 0; i < animalOwnershipInt; i++) {
                AnimalOwnership animalOwnership = new AnimalOwnership();
                int index = random.nextInt(userClientList.size());
                animalOwnership.setOwner(userClientList.get(index));
                userClientList.remove(index);
                index = random.nextInt(animalList.size());
                animalOwnership.setAnimal(animalList.get(index));
                animalList.remove(index);
                Boolean b = generator.generateBoolWithNull();
                if (b != null) {
                    animalOwnership.setApprove(b);
                }
                animalOwnership.setOpen(generator.generateBool());
                animalOwnership.setDateEndTrial(generator.generateDate(false, LocalDate.now()));
                animalOwnership.setDateStartOwn(generator.generateDate(true, animalOwnership.getDateEndTrial()));
                animalOwnership.setShelter(shelter);
                animalOwnership = animalOwnershipRepository.save(animalOwnership);
                animalOwnershipList.add(animalOwnership);
            }
            //generate report and photo
            for (int i = 0; i < reportInt; i++) {
                Photo photo = new Photo();
                photo.setIdMedia(generator.generateMessageIfEmpty(null));
                photo.setShelter(shelter);
                photo = photoRepository.save(photo);
                Report report = new Report();
                report.setReportDate(generator.generateDate(true, LocalDate.now()));
                report.setPhoto(photo);
                int index = random.nextInt(animalOwnershipList.size());
                report.setAnimalOwnership(animalOwnershipList.get(index));
                animalOwnershipList.remove(index);
                Boolean b = generator.generateBoolWithNull();
                if (b != null) {
                    report.setApprove(b);
                }
                report.setDiet(generator.generateMessageIfEmpty(null));
                report.setBehavior(generator.generateMessageIfEmpty(null));
                report.setFeeling(generator.generateMessageIfEmpty(null));
                report.setShelter(shelter);
                reportRepository.save(report);
            }
        }
        //need clear data
        reportRepository.deleteAll();
        photoRepository.deleteAll();
        animalOwnershipRepository.deleteAll();
        //select shelter DOG
        Shelter shelter = shelterRepository.getShelterByshelterDesignation("DOG").
                orElse(null);
        //get userList of clients of DOG
        List<User> clientListDog = userRepository.findAll().stream().
                filter(user ->
                        !user.isVolunteer() &&
                                user.getShelter().getshelterDesignation().equals("DOG")).
                collect(Collectors.toList());
        //get animalList of clients of DOG
        List<Animal> animalListDog = animalRepository.findAll().stream().
                filter(animal -> animal.getShelter().getshelterDesignation().equals("DOG")).
                collect(Collectors.toList());
        //AnimalOwnerships with +-15 days with report now
        animalOwnership_m15_p15_m0 = generateAnimalOwnershipWithReport(
                -15, +15, 0,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships with +-15 days with report -1 day
        animalOwnership_m15_p15_m1 = generateAnimalOwnershipWithReport(
                -15, +15, -1,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships with +-15 days with report -2 days
        animalOwnership_m15_p15_m2 = generateAnimalOwnershipWithReport(
                -15, +15, -2,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships with +-15 days with report -3 days
        animalOwnership_m15_p15_m3 = generateAnimalOwnershipWithReport(
                -15, +15, -3,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships with -5 days with report -5
        animalOwnership_m35_m5_m5 = generateAnimalOwnershipWithReport(
                -35, -5, -5,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships with -5 days with report -6
        animalOwnership_m35_m5_m6 = generateAnimalOwnershipWithReport(
                -35, -5, -6,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships with -5 days with report -7
        animalOwnership_m35_m5_m7 = generateAnimalOwnershipWithReport(
                -35, -5, -7,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships with -1 days with report -1
        animalOwnership_m31_m1_m1 = generateAnimalOwnershipWithReport(
                -31, -1, -1,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships with -1 days with report -2
        animalOwnership_m31_m1_m2 = generateAnimalOwnershipWithReport(
                -31, -1, -2,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships with -1 days with report -3
        animalOwnership_m31_m1_m3 = generateAnimalOwnershipWithReport(
                -31, -1, -3,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships with now with report now
        animalOwnership_m30_m0_m0 = generateAnimalOwnershipWithReport(
                -30, 0, 0,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships with now with report -1
        animalOwnership_m30_m0_m1 = generateAnimalOwnershipWithReport(
                -30, 0, -1,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships with now with report -2
        animalOwnership_m30_m0_m2 = generateAnimalOwnershipWithReport(
                -30, 0, -2,
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships start now without report
        animalOwnership_m0 = generateAnimalOwnership(
                true, null,
                LocalDate.now(), LocalDate.now().plusDays(30),
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships start now-1 without report
        animalOwnership_m1 = generateAnimalOwnership(
                true, null,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(29),
                clientListDog, animalListDog, shelter);
        //AnimalOwnerships start now-2 without report
        animalOwnership_m2 = generateAnimalOwnership(
                true, null,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(28),
                clientListDog, animalListDog, shelter);
    }

    @AfterEach
    public void clearData() {
        reportRepository.deleteAll();
        photoRepository.deleteAll();
        animalOwnershipRepository.deleteAll();
        callRequestRepository.deleteAll();
        userRepository.deleteAll();
        animalRepository.deleteAll();
        unfinishedRequestTelegramRepository.deleteAll();
        chatRepository.deleteAll();
        shelterRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertThat(telegramBot).isNotNull();
        assertThat(animalOwnershipRepository).isNotNull();
        assertThat(animalRepository).isNotNull();
        assertThat(shelterRepository).isNotNull();
        assertThat(callRequestRepository).isNotNull();
        assertThat(chatRepository).isNotNull();
        assertThat(photoRepository).isNotNull();
        assertThat(reportRepository).isNotNull();
        assertThat(unfinishedRequestTelegramRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(animalOwnershipService).isNotNull();
        assertThat(animalService).isNotNull();
        assertThat(shelterService).isNotNull();
        assertThat(callRequestService).isNotNull();
        assertThat(chatService).isNotNull();
        assertThat(commandService).isNotNull();
        assertThat(dtoMapperService).isNotNull();
        assertThat(photoService).isNotNull();
        assertThat(reportService).isNotNull();
        assertThat(scheduleService).isNotNull();
        assertThat(telegramBotSenderService).isNotNull();
        assertThat(telegramBotUpdatesService).isNotNull();
        assertThat(telegramMapperService).isNotNull();
        assertThat(telegramUnfinishedRequestService).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(telegramBotUpdatesListener).isNotNull();
    }

    @Test
    void getLateAndNoReportListTest() {
        Shelter shelter = shelterRepository.findAll().stream().filter(shelter1 ->
                shelter1.getshelterDesignation().equals("DOG")).findFirst().orElse(null);

        LocalDate localDateLate = LocalDate.now().minusDays(ScheduleService.COUNT_LATE_DAY);
        LocalDate localDateViolators = LocalDate.now().minusDays(ScheduleService.COUNT_LATE_DAY_VERY_BAD);

        List<AnimalOwnership> lateList = scheduleService.getLateList(shelter, localDateLate);
        List<AnimalOwnership> violatorsList = scheduleService.getLateList(shelter, localDateViolators);
        List<AnimalOwnership> noReportList = scheduleService.getNoReportList(shelter, LocalDate.now());
        List<AnimalOwnership> violatorsNoReportList =
                scheduleService.getNoReportList(shelter, LocalDate.now().minusDays(1));

        Assertions.assertThat(lateList).
                usingRecursiveFieldByFieldElementComparatorIgnoringFields().
                isEqualTo(List.of(animalOwnership_m15_p15_m2, animalOwnership_m15_p15_m3));
        Assertions.assertThat(violatorsList).
                usingRecursiveFieldByFieldElementComparatorIgnoringFields().
                isEqualTo(List.of(animalOwnership_m15_p15_m3));
        Assertions.assertThat(noReportList).
                usingRecursiveFieldByFieldElementComparatorIgnoringFields().
                isEqualTo(List.of(animalOwnership_m1, animalOwnership_m2));
        Assertions.assertThat(violatorsNoReportList).
                usingRecursiveFieldByFieldElementComparatorIgnoringFields().
                isEqualTo(List.of(animalOwnership_m2));
    }

    @Test
    void checkLateReportsAndNoReportsTest() {
        //Set dateLastNotification of users = now - 2 days
        userRepository.findAll().stream().
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                filter(user -> !user.isVolunteer()).
                forEach(user -> {
                    user.setDateLastNotification(LocalDateTime.now().minusDays(2));
                    userRepository.save(user);
                });

        List<String> idChatOfVolunteers = userRepository.findAll().stream().
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                filter(User::isVolunteer).
                map(user -> user.getChatTelegram().getId().toString()).
                collect(Collectors.toList());

        scheduleService.checkLateReportsAndNoReports();

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);

        verify(telegramBot, times(6)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(6);

        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);
        SendMessage actual4 = actualList.get(4);
        SendMessage actual5 = actualList.get(5);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(
                animalOwnership_m15_p15_m2.getOwner().getChatTelegram().getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(
                ReportService.MESSAGE_NOTIFICATION_ABOUT_REPORT);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(
                animalOwnership_m15_p15_m3.getOwner().getChatTelegram().getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(
                ReportService.MESSAGE_NOTIFICATION_ABOUT_REPORT);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(
                animalOwnership_m1.getOwner().getChatTelegram().getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(
                ReportService.MESSAGE_NOTIFICATION_ABOUT_REPORT);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(
                animalOwnership_m2.getOwner().getChatTelegram().getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(
                ReportService.MESSAGE_NOTIFICATION_ABOUT_REPORT);

        assertTrue(idChatOfVolunteers.contains(actual4.getParameters().get("chat_id").toString()));
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(
                ReportService.MESSAGE_NEED_CONTACT_OWNER);

        assertTrue(idChatOfVolunteers.contains(actual5.getParameters().get("chat_id").toString()));
        String validateStr = "AnimalOwnership\nOwner:";
        assertTrue(actual5.getParameters().get("text").toString().contains(validateStr) &&
                actual5.getParameters().get("text").toString().contains(animalOwnership_m15_p15_m3.toString()) &&
                actual5.getParameters().get("text").toString().contains(animalOwnership_m2.toString())
        );
    }

    @Test
    void checkNotApproveOpenAnimalOwnershipWithNotTrial() {
        List<String> idChatOfVolunteers = userRepository.findAll().stream().
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                filter(User::isVolunteer).
                map(user -> user.getChatTelegram().getId().toString()).
                collect(Collectors.toList());

        scheduleService.checkNotApproveOpenAnimalOwnershipWithNotTrial();

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);

        verify(telegramBot, times(2)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(2);

        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);

        assertTrue(idChatOfVolunteers.contains(actual0.getParameters().get("chat_id").toString()));
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(
                AnimalOwnershipService.MESSAGE_TRIAL_IS_OVER);

        assertTrue(idChatOfVolunteers.contains(actual1.getParameters().get("chat_id").toString()));
        String validateStr = "AnimalOwnership\nOwner:";
        assertTrue(actual1.getParameters().get("text").toString().contains(validateStr) &&
                actual1.getParameters().get("text").toString().contains(animalOwnership_m35_m5_m5.toString()) &&
                actual1.getParameters().get("text").toString().contains(animalOwnership_m35_m5_m6.toString()) &&
                actual1.getParameters().get("text").toString().contains(animalOwnership_m35_m5_m7.toString()) &&
                actual1.getParameters().get("text").toString().contains(animalOwnership_m31_m1_m1.toString()) &&
                actual1.getParameters().get("text").toString().contains(animalOwnership_m31_m1_m2.toString()) &&
                actual1.getParameters().get("text").toString().contains(animalOwnership_m31_m1_m3.toString())
        );
    }

    private AnimalOwnership generateAnimalOwnershipWithReport(
            int dateStartOwnPlusDays,
            int dateEndTrialPlusDays,
            int dateLastReportPlusDays,
            List<User> clientListDog,
            List<Animal> animalListDog,
            Shelter shelter) {
        AnimalOwnership animalOwnership = generateAnimalOwnership(
                true, null,
                LocalDate.now().plusDays(dateStartOwnPlusDays),
                LocalDate.now().plusDays(dateEndTrialPlusDays),
                clientListDog, animalListDog, shelter);
        generateReport(true, LocalDate.now().plusDays(dateLastReportPlusDays), animalOwnership, shelter);
        return animalOwnership;
    }

    private AnimalOwnership generateAnimalOwnership(
            boolean isOpen,
            Boolean isApprove,
            LocalDate dateStartOwn,
            LocalDate dateEndTrial,
            List<User> ownerList,
            List<Animal> animalList,
            Shelter shelter) {
        AnimalOwnership animalOwnership = new AnimalOwnership();
        animalOwnership.setOpen(isOpen);
        animalOwnership.setApprove(isApprove);
        animalOwnership.setDateStartOwn(dateStartOwn);
        animalOwnership.setDateEndTrial(dateEndTrial);
        int indexTemp = random.nextInt(ownerList.size());
        animalOwnership.setOwner(ownerList.get(indexTemp));
        ownerList.remove(indexTemp);
        indexTemp = random.nextInt(animalList.size());
        animalOwnership.setAnimal(animalList.get(indexTemp));
        animalList.remove(indexTemp);
        animalOwnership.setShelter(shelter);
        return animalOwnershipRepository.save(animalOwnership);
    }

    private Report generateReport(
            Boolean isApprove,
            LocalDate reportDate,
            AnimalOwnership animalOwnership,
            Shelter shelter) {
        Report report = new Report();
        report.setApprove(isApprove);
        report.setDiet(generator.generateMessageIfEmpty(null));
        report.setFeeling(generator.generateMessageIfEmpty(null));
        report.setBehavior(generator.generateMessageIfEmpty(null));
        report.setAnimalOwnership(animalOwnership);
        report.setReportDate(reportDate);
        report.setShelter(shelter);
        Photo photo = new Photo();
        photo.setShelter(shelter);
        photo.setIdMedia(generator.generateMessageIfEmpty(null));
        report.setPhoto(photoRepository.save(photo));
        return reportRepository.save(report);
    }


}