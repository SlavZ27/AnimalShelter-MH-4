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
import pro.sky.animalshelter4.entityDto.ChatDto;
import pro.sky.animalshelter4.entityDto.ReportDto;
import pro.sky.animalshelter4.entityDto.ShelterDto;
import pro.sky.animalshelter4.exception.ChatNotFoundException;
import pro.sky.animalshelter4.exception.ReportNotFoundException;
import pro.sky.animalshelter4.exception.ShelterNotFoundException;
import pro.sky.animalshelter4.listener.TelegramBotUpdatesListener;
import pro.sky.animalshelter4.repository.*;
import pro.sky.animalshelter4.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShelterControllerTest {
    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "shelter";
    @InjectMocks
    @Autowired
    private ShelterController shelterController;
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
    void createShelter() {
        Shelter shelterDog =
                shelterRepository.findAll().stream().
                        filter(shelter -> shelter.getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);

        ShelterDto shelterDtoDog = dtoMapperService.toDto(shelterDog);

        reportRepository.deleteAll();
        photoRepository.deleteAll();
        animalOwnershipRepository.deleteAll();
        callRequestRepository.deleteAll();
        userRepository.deleteAll();
        animalRepository.deleteAll();
        unfinishedRequestTelegramRepository.deleteAll();
        chatRepository.deleteAll();
        shelterRepository.delete(shelterDog);

        final int countShelter = shelterRepository.findAll().size();

        ShelterDto responseEntity1 = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        shelterDtoDog,
                        ShelterDto.class);

        assertEquals(countShelter + 1, shelterRepository.findAll().size());

        assertThat(responseEntity1).isNotNull();
        assertThat(responseEntity1.getNameShelter()).isEqualTo(shelterDtoDog.getNameShelter());
        assertThat(responseEntity1.getAddress()).isEqualTo(shelterDtoDog.getAddress());
        assertThat(responseEntity1.getPhone()).isEqualTo(shelterDtoDog.getPhone());
        assertThat(responseEntity1.getshelterDesignation()).isEqualTo(shelterDtoDog.getshelterDesignation());
    }

    @Test
    void readShelter() {
        Shelter shelter =
                shelterRepository.findAll().stream().findAny().orElse(null);
        assertThat(shelter).isNotNull();

        ShelterDto shelterDto = dtoMapperService.toDto(shelter);
        assertThat(shelterRepository.findById(shelterDto.getId()).orElse(null))
                .isNotNull();

        final int countShelter = shelterRepository.findAll().size();

        ShelterDto responseEntity1 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + shelter.getId(),
                        ShelterDto.class);
        assertEquals(countShelter, shelterRepository.findAll().size());

        assertThat(responseEntity1).isNotNull();
        assertThat(responseEntity1.getNameShelter()).isEqualTo(shelterDto.getNameShelter());
        assertThat(responseEntity1.getAddress()).isEqualTo(shelterDto.getAddress());
        assertThat(responseEntity1.getPhone()).isEqualTo(shelterDto.getPhone());
        assertThat(responseEntity1.getshelterDesignation()).isEqualTo(shelterDto.getshelterDesignation());
    }

    @Test
    void readShelterNegative() {
        List<Long> shelterIdList = shelterRepository.findAll().stream().map(Shelter::getId).collect(Collectors.toList());
        Long index = (long) random.nextInt(shelterIdList.size());
        while (shelterIdList.contains(index)) {
            index = (long) random.nextInt(shelterIdList.size());
        }

        ShelterDto responseEntity = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + index,
                        ShelterDto.class);

        Long finalIndex = index;
        assertThatExceptionOfType(ShelterNotFoundException.class).isThrownBy(() ->
                shelterService.readShelter(finalIndex));
    }

    @Test
    void updateShelter() {
        Shelter shelter =
                shelterRepository.findAll().stream().findAny().orElse(null);
        assertThat(shelter).isNotNull();

        ShelterDto shelterDto = dtoMapperService.toDto(shelter);
        assertThat(shelterDto).isNotNull();
        assertThat(shelterRepository.findById(shelterDto.getId()).orElse(null))
                .isNotNull();
        shelterDto.setPhone("1234567890");

        final int countShelter = shelterRepository.findAll().size();
        testRestTemplate.
                put("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        shelterDto);
        assertEquals(countShelter, shelterRepository.findAll().size());

        Shelter shelterActual = shelterRepository.findById(shelterDto.getId()).orElse(null);

        assertThat(shelterActual).isNotNull();
        assertThat(shelterActual.getPhone()).isEqualTo("1234567890");
        assertThat(shelterActual.getNameShelter()).isEqualTo(shelter.getNameShelter());
        assertThat(shelterActual.getAddress()).isEqualTo(shelter.getAddress());
        assertThat(shelterActual.getshelterDesignation()).isEqualTo(shelter.getshelterDesignation());
    }


    @Test
    void deleteShelter() {
        final int countShelter = shelterRepository.findAll().size();
        Shelter shelter = shelterRepository.findAll().stream().findFirst().orElse(null);

        reportRepository.deleteAll();
        photoRepository.deleteAll();
        animalOwnershipRepository.deleteAll();
        callRequestRepository.deleteAll();
        userRepository.deleteAll();
        animalRepository.deleteAll();
        unfinishedRequestTelegramRepository.deleteAll();
        chatRepository.deleteAll();

        ResponseEntity<String> responseEntity = testRestTemplate
                .exchange("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + shelter.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .contains(shelter.getId().toString())
                .contains(shelter.getPhone().toString())
                .contains(shelter.getNameShelter().toString())
                .contains(shelter.getAddress().toString())
                .contains(shelter.getshelterDesignation().toString());
        assertEquals(countShelter - 1, shelterRepository.findAll().size());
        assertThat(shelterRepository.findById(shelter.getId()).orElse(null))
                .isNull();
    }

    @Test
    public void getAllShelterTest() {
        final long countShelter = shelterRepository.findAll().size();
        ShelterDto[] callRequestDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        ShelterDto[].class);
        assertThat(callRequestDtos.length)
                .isEqualTo(countShelter);
    }
}