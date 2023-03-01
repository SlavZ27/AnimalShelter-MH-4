package pro.sky.animalshelter4.controller;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import pro.sky.animalshelter4.Generator;
import pro.sky.animalshelter4.entity.*;
import pro.sky.animalshelter4.entityDto.CallRequestDto;
import pro.sky.animalshelter4.entityDto.ReportDto;
import pro.sky.animalshelter4.exception.CallRequestNotFoundException;
import pro.sky.animalshelter4.exception.ReportNotFoundException;
import pro.sky.animalshelter4.listener.TelegramBotUpdatesListener;
import pro.sky.animalshelter4.repository.*;
import pro.sky.animalshelter4.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportControllerTest {
    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "report";
    private final static String SHELTER1 = "DOG";
    private final static String SHELTER2 = "CAT";
    @InjectMocks
    @Autowired
    private ReportController reportController;
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
    private ScheduleService scheduleService;
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
    @Autowired
    private TestRestTemplate testRestTemplate;
    private final Generator generator = new Generator();
    private final Random random = new Random();

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
        int animalTypeInt = 6;
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
    void createReport() {
        Report reportDog =
                reportRepository.findAll().stream().
                        filter(report -> report.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        Report reportCat =
                reportRepository.findAll().stream().
                        filter(report -> report.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);
        ReportDto reportDtoDog = dtoMapperService.toDto(reportDog);
        ReportDto reportDtoCat = dtoMapperService.toDto(reportCat);
        reportRepository.delete(reportDog);
        reportRepository.delete(reportCat);

        final int countReport = reportRepository.findAll().size();

        ReportDto responseEntity1 = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING,
                        reportDtoDog,
                        ReportDto.class);
        ReportDto responseEntity2 = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING,
                        reportDtoCat,
                        ReportDto.class);
        assertEquals(countReport + 2, reportRepository.findAll().size());

        assertThat(responseEntity1).isNotNull();
        assertThat(responseEntity1.getReportDate().toString()).isEqualTo(reportDtoDog.getReportDate().toString());
        assertThat(responseEntity1.getDiet()).isEqualTo(reportDtoDog.getDiet());
        assertThat(responseEntity1.getBehavior()).isEqualTo(reportDtoDog.getBehavior());
        assertThat(responseEntity1.getFeeling()).isEqualTo(reportDtoDog.getFeeling());
        assertThat(responseEntity1.getApprove()).isEqualTo(reportDtoDog.getApprove());
        assertThat(responseEntity1.getIdAnimalOwnership()).isEqualTo(reportDtoDog.getIdAnimalOwnership());
        assertThat(responseEntity1.getIdPhoto()).isEqualTo(reportDtoDog.getIdPhoto());
        assertThat(responseEntity1.getLinkPhoto()).isEqualTo(reportDtoDog.getLinkPhoto());

        assertThat(responseEntity2).isNotNull();
        assertThat(responseEntity2.getReportDate().toString()).isEqualTo(reportDtoCat.getReportDate().toString());
        assertThat(responseEntity2.getDiet()).isEqualTo(reportDtoCat.getDiet());
        assertThat(responseEntity2.getBehavior()).isEqualTo(reportDtoCat.getBehavior());
        assertThat(responseEntity2.getFeeling()).isEqualTo(reportDtoCat.getFeeling());
        assertThat(responseEntity2.getApprove()).isEqualTo(reportDtoCat.getApprove());
        assertThat(responseEntity2.getIdAnimalOwnership()).isEqualTo(reportDtoCat.getIdAnimalOwnership());
        assertThat(responseEntity2.getIdPhoto()).isEqualTo(reportDtoCat.getIdPhoto());
        assertThat(responseEntity2.getLinkPhoto()).isEqualTo(reportDtoCat.getLinkPhoto());
    }

    @Test
    void readReport() {
        Report reportDog =
                reportRepository.findAll().stream().
                        filter(report1 -> report1.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        Report reportCat =
                reportRepository.findAll().stream().
                        filter(report1 -> report1.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);

        ReportDto reportDtoDog = dtoMapperService.toDto(reportDog);
        ReportDto reportDtoCat = dtoMapperService.toDto(reportCat);

        final int countReport = reportRepository.findAll().size();

        ReportDto responseEntity1 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + reportDog.getId(),
                        ReportDto.class);
        ReportDto responseEntity2 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + reportCat.getId(),
                        ReportDto.class);
        ReportDto responseEntity3 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + reportCat.getId(),
                        ReportDto.class);
        assertEquals(countReport, reportRepository.findAll().size());

        assertThat(responseEntity1).isNotNull();
        assertThat(responseEntity1.getReportDate().toString()).isEqualTo(reportDtoDog.getReportDate().toString());
        assertThat(responseEntity1.getId().toString()).isEqualTo(reportDtoDog.getId().toString());
        assertThat(responseEntity1.getDiet()).isEqualTo(reportDtoDog.getDiet());
        assertThat(responseEntity1.getBehavior()).isEqualTo(reportDtoDog.getBehavior());
        assertThat(responseEntity1.getFeeling()).isEqualTo(reportDtoDog.getFeeling());
        assertThat(responseEntity1.getApprove()).isEqualTo(reportDtoDog.getApprove());
        assertThat(responseEntity1.getIdAnimalOwnership()).isEqualTo(reportDtoDog.getIdAnimalOwnership());
        assertThat(responseEntity1.getIdPhoto()).isEqualTo(reportDtoDog.getIdPhoto());
        assertThat(responseEntity1.getLinkPhoto()).isEqualTo(reportDtoDog.getLinkPhoto());

        assertThat(responseEntity2).isNotNull();
        assertThat(responseEntity2.getReportDate().toString()).isEqualTo(reportDtoCat.getReportDate().toString());
        assertThat(responseEntity2.getId().toString()).isEqualTo(reportDtoCat.getId().toString());
        assertThat(responseEntity2.getDiet()).isEqualTo(reportDtoCat.getDiet());
        assertThat(responseEntity2.getBehavior()).isEqualTo(reportDtoCat.getBehavior());
        assertThat(responseEntity2.getFeeling()).isEqualTo(reportDtoCat.getFeeling());
        assertThat(responseEntity2.getApprove()).isEqualTo(reportDtoCat.getApprove());
        assertThat(responseEntity2.getIdAnimalOwnership()).isEqualTo(reportDtoCat.getIdAnimalOwnership());
        assertThat(responseEntity2.getIdPhoto()).isEqualTo(reportDtoCat.getIdPhoto());
        assertThat(responseEntity2.getLinkPhoto()).isEqualTo(reportDtoCat.getLinkPhoto());

        if (responseEntity3.getId() != null) {
            assertThat(responseEntity3.getReportDate().toString()).isNotEqualTo(responseEntity1.getReportDate().toString());
            assertThat(responseEntity3.getId().toString()).isNotEqualTo(responseEntity1.getId().toString());
            assertThat(responseEntity3.getDiet()).isNotEqualTo(responseEntity1.getDiet());
            assertThat(responseEntity3.getBehavior()).isNotEqualTo(responseEntity1.getBehavior());
            assertThat(responseEntity3.getFeeling()).isNotEqualTo(responseEntity1.getFeeling());
            assertThat(responseEntity3.getApprove()).isNotEqualTo(responseEntity1.getApprove());
            assertThat(responseEntity3.getIdAnimalOwnership()).isNotEqualTo(responseEntity1.getIdAnimalOwnership());
            assertThat(responseEntity3.getIdPhoto()).isNotEqualTo(responseEntity1.getIdPhoto());
            assertThat(responseEntity3.getLinkPhoto()).isNotEqualTo(responseEntity1.getLinkPhoto());
        }
    }

    @Test
    void readCallRequestNegative() {
        List<Long> reportIdList = reportRepository.findAll().stream().map(Report::getId).collect(Collectors.toList());
        Long index = (long) random.nextInt(reportIdList.size());
        while (reportIdList.contains(index)) {
            index = (long) random.nextInt(reportIdList.size());
        }

        ReportDto responseEntity1 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + index,
                        ReportDto.class);
        ReportDto responseEntity2 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + index,
                        ReportDto.class);

        Long finalIndex = index;
        assertThatExceptionOfType(ReportNotFoundException.class).isThrownBy(() ->
                reportService.readReport(finalIndex, "DOG"));
        assertThatExceptionOfType(ReportNotFoundException.class).isThrownBy(() ->
                reportService.readReport(finalIndex, "CAT"));
    }

    @Test
    void updateReport() {
        Report reportDog =
                reportRepository.findAll().stream().
                        filter(report1 -> report1.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        Report reportCat =
                reportRepository.findAll().stream().
                        filter(report1 -> report1.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);

        ReportDto reportDtoDog = dtoMapperService.toDto(reportDog);
        ReportDto reportDtoCat = dtoMapperService.toDto(reportCat);

        reportDtoDog.setBehavior("fgfrergth");
        reportDtoCat.setBehavior("fgfrergth");

        final int countReport = reportRepository.findAll().size();
        testRestTemplate.
                put("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING,
                        reportDtoDog);
        testRestTemplate.
                put("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING,
                        reportDtoCat);
        assertEquals(countReport, reportRepository.findAll().size());

        Report reportActual1 = reportRepository.findById(reportDtoDog.getId()).orElse(null);
        Report reportActual2 = reportRepository.findById(reportDtoCat.getId()).orElse(null);

        assertThat(reportActual1).isNotNull();
        assertThat(reportActual1.getBehavior()).isEqualTo("fgfrergth");
        assertThat(reportActual1.getReportDate().toString()).isEqualTo(reportDog.getReportDate().toString());
        assertThat(reportActual1.getId().toString()).isEqualTo(reportDog.getId().toString());
        assertThat(reportActual1.getDiet()).isEqualTo(reportDog.getDiet());
        assertThat(reportActual1.getFeeling()).isEqualTo(reportDog.getFeeling());
        assertThat(reportActual1.getAnimalOwnership().getId()).isEqualTo(reportDog.getAnimalOwnership().getId());
        assertThat(reportActual1.getPhoto().getId()).isEqualTo(reportDog.getPhoto().getId());

        assertThat(reportActual2).isNotNull();
        assertThat(reportActual2.getBehavior()).isEqualTo("fgfrergth");
        assertThat(reportActual2.getReportDate().toString()).isEqualTo(reportCat.getReportDate().toString());
        assertThat(reportActual2.getId().toString()).isEqualTo(reportCat.getId().toString());
        assertThat(reportActual2.getDiet()).isEqualTo(reportCat.getDiet());
        assertThat(reportActual2.getFeeling()).isEqualTo(reportCat.getFeeling());
        assertThat(reportActual2.getAnimalOwnership().getId()).isEqualTo(reportCat.getAnimalOwnership().getId());
        assertThat(reportActual2.getPhoto().getId()).isEqualTo(reportCat.getPhoto().getId());

    }

    @Test
    void deleteReport() {
        final int countReport = reportRepository.findAll().size();

        Report reportDog =
                reportRepository.findAll().stream().
                        filter(report1 -> report1.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        Report reportCat =
                reportRepository.findAll().stream().
                        filter(report1 -> report1.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);

        ResponseEntity<String> responseEntity1 = testRestTemplate
                .exchange("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + reportDog.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });

        ResponseEntity<String> responseEntity2 = testRestTemplate
                .exchange("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + reportCat.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });

        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity1.getBody())
                .contains(reportDog.getId().toString())
                .contains(reportDog.getBehavior().toString())
                .contains(reportDog.getReportDate().toString())
                .contains(reportDog.getId().toString())
                .contains(reportDog.getDiet().toString())
                .contains(reportDog.getFeeling().toString())
                .contains(reportDog.getAnimalOwnership().getId().toString())
                .contains(reportDog.getPhoto().getId().toString());

        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity2.getBody())
                .contains(reportCat.getId().toString())
                .contains(reportCat.getBehavior().toString())
                .contains(reportCat.getReportDate().toString())
                .contains(reportCat.getId().toString())
                .contains(reportCat.getDiet().toString())
                .contains(reportCat.getFeeling().toString())
                .contains(reportCat.getAnimalOwnership().getId().toString())
                .contains(reportCat.getPhoto().getId().toString());

        assertEquals(countReport - 2, reportRepository.findAll().size());
        assertThat(reportRepository.findById(reportDog.getId()).orElse(null))
                .isNull();
        assertThat(reportRepository.findById(reportCat.getId()).orElse(null))
                .isNull();
    }

    @Test
    public void getAllReportTest() {
        final long countReport = reportRepository.findAll().size();
        ReportDto[] reportDtos1 = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING,
                        ReportDto[].class);
        ReportDto[] reportDtos2 = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING,
                        ReportDto[].class);
        assertThat(reportDtos1.length + reportDtos1.length)
                .isEqualTo(countReport);
    }
}