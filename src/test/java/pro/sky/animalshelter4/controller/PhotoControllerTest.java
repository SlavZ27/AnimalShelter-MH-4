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
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import pro.sky.animalshelter4.Generator;
import pro.sky.animalshelter4.entity.*;
import pro.sky.animalshelter4.listener.TelegramBotUpdatesListener;
import pro.sky.animalshelter4.repository.*;
import pro.sky.animalshelter4.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PhotoControllerTest {
    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "photo";

    @InjectMocks
    @Autowired
    private PhotoController photoController;
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
    private PhotoService photoService;
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
    private ReportService reportService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private TelegramBotSenderService telegramBotSenderService;
    @Autowired
    private TelegramMapperService telegramMapperService;
    @Autowired
    private TelegramPhotoService telegramPhotoService;
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
        shelterRepository.deleteAll();
        unfinishedRequestTelegramRepository.deleteAll();
        chatRepository.deleteAll();

        int userVolunteerInt = 5;
        int userClientInt = 100;
        int chatInt = userClientInt + userVolunteerInt;
        int callRequestInt = 40;                       // callRequest < chat
        int animalInt = 50;
        int animalOwnershipInt = 30;                   // animal > animalOwnership
        int animalTypeInt = 6;
        int reportInt = 30;
        int photoInt = reportInt;


        //generate and remember userVolunteer with chat
        List<User> userVolunteerList = new ArrayList<>();
        List<Chat> chatVolunteerList = new ArrayList<>();
        for (int i = 0; i < userVolunteerInt; i++) {
            //generate chat
            Chat chatV = generator.generateChat(-1L, "", "", "", null, true);
            chatV = chatRepository.save(chatV);
            //generate userVolunteer with chatV
            User userVolunteer = generator.generateUser(null, null, chatV, null, null, true, null, true);
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
            chatC = chatRepository.save(chatC);
            //generate userClient with chatC
            User userClient = generator.generateUser(null, null, chatC, null, null, false, null, true);
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
            callRequestRepository.save(callRequest);
        }
        //generate animalType
        List<Animal> animalList = new ArrayList<>();
        for (int i = 0; i < animalTypeInt; i++) {
            Shelter shelter = new Shelter();
            shelter = shelterRepository.save(shelter);
            for (int j = 0; j < animalInt; j++) {
                //generate and remember animal
                Animal animal = new Animal();
                animal.setNameAnimal(generator.generateNameIfEmpty(null));
                animal.setBorn(generator.generateDate(true, LocalDate.now()));
                animal = animalRepository.save(animal);
                animalList.add(animal);
            }
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
            animalOwnership = animalOwnershipRepository.save(animalOwnership);
            animalOwnershipList.add(animalOwnership);
        }
        //generate report and photo
        for (int i = 0; i < reportInt; i++) {
            Photo photo = new Photo();
            photo.setIdMedia(generator.generateMessageIfEmpty(null));
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
            reportRepository.save(report);
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
        shelterRepository.deleteAll();
        unfinishedRequestTelegramRepository.deleteAll();
        chatRepository.deleteAll();
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
        assertThat(telegramPhotoService).isNotNull();
        assertThat(telegramUnfinishedRequestService).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(telegramBotUpdatesListener).isNotNull();
        assertThat(testRestTemplate).isNotNull();
        assertThat(photoController).isNotNull();
    }

    @Test
    void deletePhoto() {
        final int countPhoto = photoRepository.findAll().size();
        Photo photo = photoRepository.findAll().stream().findFirst().orElse(null);
        reportRepository.findAll().stream().filter(report -> report.getPhoto().getId().equals(photo.getId())).
                forEach(report -> reportRepository.delete(report));
        ResponseEntity<String> responseEntity = testRestTemplate
                .exchange("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + photo.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).
                contains(photo.getId().toString());
        assertEquals(countPhoto - 1, photoRepository.findAll().size());
        assertThat(photoRepository.findById(photo.getId()).orElse(null)).isNull();
    }

    @Test
    public void getAllIdPhotoTest() {
        final long countPhoto = photoRepository.findAll().size();
        Long[] idPhotoMas = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        Long[].class);
        assertThat(idPhotoMas.length)
                .isEqualTo(countPhoto);
    }
}