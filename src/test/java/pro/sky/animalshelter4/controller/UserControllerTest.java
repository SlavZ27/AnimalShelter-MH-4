package pro.sky.animalshelter4.controller;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
import pro.sky.animalshelter4.entityDto.ReportDto;
import pro.sky.animalshelter4.entityDto.UserDto;
import pro.sky.animalshelter4.exception.UserNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {


    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "user";
    private final static String SHELTER1 = "DOG";
    private final static String SHELTER2 = "CAT";
    @Autowired
    @InjectMocks
    private UserController userController;
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
    void createUser() {
        User userDog =
                userRepository.findAll().stream().
                        filter(user1 -> user1.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        User userCat =
                userRepository.findAll().stream().
                        filter(user1 -> user1.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);

        UserDto userDtoDog = dtoMapperService.toDto(userDog);
        UserDto userDtoCat = dtoMapperService.toDto(userCat);

        callRequestRepository.findAll().stream().
                filter(callRequest ->
                        callRequest.getClient().getId().equals(userDog.getId()) ||
                                callRequest.getClient().getId().equals(userCat.getId()) ||
                                callRequest.getVolunteer().getId().equals(userCat.getId()) ||
                                callRequest.getVolunteer().getId().equals(userDog.getId())).
                forEach(callRequest -> callRequestRepository.delete(callRequest));

        userRepository.delete(userDog);
        userRepository.delete(userCat);

        final int countUser = userRepository.findAll().size();

        UserDto responseEntity1 = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING,
                        userDtoDog,
                        UserDto.class);
        UserDto responseEntity2 = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING,
                        userDtoCat,
                        UserDto.class);

        assertEquals(countUser + 2, userRepository.findAll().size());

        assertThat(responseEntity1).isNotNull();
        assertThat(responseEntity1.getIdChat()).isEqualTo(userDtoDog.getIdChat());
        assertThat(responseEntity1.getAddress()).isEqualTo(userDtoDog.getAddress());
        assertThat(responseEntity1.getNameUser()).isEqualTo(userDtoDog.getNameUser());
        assertThat(responseEntity1.getPhone()).isEqualTo(userDtoDog.getPhone());

        assertThat(responseEntity2).isNotNull();
        assertThat(responseEntity2.getIdChat()).isEqualTo(userDtoCat.getIdChat());
        assertThat(responseEntity2.getAddress()).isEqualTo(userDtoCat.getAddress());
        assertThat(responseEntity2.getNameUser()).isEqualTo(userDtoCat.getNameUser());
        assertThat(responseEntity2.getPhone()).isEqualTo(userDtoCat.getPhone());
    }

    @Test
    void readUser() {
        User userDog =
                userRepository.findAll().stream().
                        filter(user1 -> user1.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        User userCat =
                userRepository.findAll().stream().
                        filter(user1 -> user1.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);

        UserDto userDtoDog = dtoMapperService.toDto(userDog);
        UserDto userDtoCat = dtoMapperService.toDto(userCat);

        final int countUser = userRepository.findAll().size();

        UserDto responseEntity1 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + userDtoDog.getId(),
                        UserDto.class);
        UserDto responseEntity2 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + userDtoCat.getId(),
                        UserDto.class);
        UserDto responseEntity3 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + userDtoDog.getId(),
                        UserDto.class);

        assertEquals(countUser, userRepository.findAll().size());

        assertThat(responseEntity1).isNotNull();
        assertThat(responseEntity1.getIdChat()).isEqualTo(userDtoDog.getIdChat());
        assertThat(responseEntity1.getAddress()).isEqualTo(userDtoDog.getAddress());
        assertThat(responseEntity1.getNameUser()).isEqualTo(userDtoDog.getNameUser());
        assertThat(responseEntity1.getPhone()).isEqualTo(userDtoDog.getPhone());

        assertThat(responseEntity2).isNotNull();
        assertThat(responseEntity2.getIdChat()).isEqualTo(userDtoCat.getIdChat());
        assertThat(responseEntity2.getAddress()).isEqualTo(userDtoCat.getAddress());
        assertThat(responseEntity2.getNameUser()).isEqualTo(userDtoCat.getNameUser());
        assertThat(responseEntity2.getPhone()).isEqualTo(userDtoCat.getPhone());

        if (responseEntity3.getId() != null) {
            assertThat(responseEntity3.getIdChat()).isNotEqualTo(responseEntity1.getIdChat());
            assertThat(responseEntity3.getAddress()).isNotEqualTo(responseEntity1.getAddress());
            assertThat(responseEntity3.getNameUser()).isNotEqualTo(responseEntity1.getNameUser());
            assertThat(responseEntity3.getPhone()).isNotEqualTo(responseEntity1.getPhone());
        }
    }

    @Test
    void readUserNegative() {
        List<Long> userIdList = userRepository.findAll().stream().map(User::getId).collect(Collectors.toList());
        Long index = (long) random.nextInt(userIdList.size());
        while (userIdList.contains(index)) {
            index = (long) random.nextInt(userIdList.size());
        }

        UserDto responseEntity1 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + index,
                        UserDto.class);
        UserDto responseEntity2 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + index,
                        UserDto.class);

        Long finalIndex = index;
        assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() ->
                userService.readUser(finalIndex, "DOG"));
        assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() ->
                userService.readUser(finalIndex, "CAT"));
    }

    @Test
    void updateUser() {
        User userDog =
                userRepository.findAll().stream().
                        filter(user1 -> user1.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        User userCat =
                userRepository.findAll().stream().
                        filter(user1 -> user1.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);

        UserDto userDtoDog = dtoMapperService.toDto(userDog);
        UserDto userDtoCat = dtoMapperService.toDto(userCat);

        userDtoDog.setAddress("1234567890");
        userDtoCat.setAddress("1234567890");

        final int countUser = userRepository.findAll().size();
        testRestTemplate.
                put("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING,
                        userDtoDog);
        testRestTemplate.
                put("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING,
                        userDtoCat);
        assertEquals(countUser, userRepository.findAll().size());

        User userActual1 = userRepository.findById(userDtoDog.getId()).orElse(null);
        User userActual2 = userRepository.findById(userDtoCat.getId()).orElse(null);

        assertThat(userActual1).isNotNull();
        assertThat(userActual1.getChatTelegram().getId()).isEqualTo(userDog.getChatTelegram().getId());
        assertThat(userActual1.getAddress()).isEqualTo("1234567890");
        assertThat(userActual1.getNameUser()).isEqualTo(userDog.getNameUser());
        assertThat(userActual1.getPhone()).isEqualTo(userDog.getPhone());

        assertThat(userActual2).isNotNull();
        assertThat(userActual2.getChatTelegram().getId()).isEqualTo(userCat.getChatTelegram().getId());
        assertThat(userActual2.getAddress()).isEqualTo("1234567890");
        assertThat(userActual2.getNameUser()).isEqualTo(userCat.getNameUser());
        assertThat(userActual2.getPhone()).isEqualTo(userCat.getPhone());
    }


    @Test
    void deleteUser() {
        final int countUser = userRepository.findAll().size();
        User userDog =
                userRepository.findAll().stream().
                        filter(user1 -> user1.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        User userCat =
                userRepository.findAll().stream().
                        filter(user1 -> user1.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);

        callRequestRepository.findAll().stream().
                filter(callRequest ->
                        callRequest.getVolunteer().getId().equals(userCat.getId()) ||
                                callRequest.getVolunteer().getId().equals(userDog.getId()) ||
                                callRequest.getClient().getId().equals(userCat.getId()) ||
                                callRequest.getClient().getId().equals(userDog.getId())).
                forEach(callRequest -> callRequestRepository.delete(callRequest));
        animalOwnershipRepository.findAll().stream().
                filter(animalOwnership ->
                        animalOwnership.getOwner().getId().equals(userCat.getId()) ||
                                animalOwnership.getOwner().getId().equals(userDog.getId())).
                forEach(animalOwnership -> {
                    reportRepository.findAll().stream().
                            filter(report -> report.getAnimalOwnership().getId().equals(animalOwnership.getId())).
                            forEach(report -> reportRepository.delete(report));
                    animalOwnershipRepository.delete(animalOwnership);
                });

        ResponseEntity<String> responseEntity1 = testRestTemplate
                .exchange("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + userDog.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });
        ResponseEntity<String> responseEntity2 = testRestTemplate
                .exchange("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + userCat.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });

        assertEquals(countUser - 2, userRepository.findAll().size());

        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity1.getBody())
                .contains(userDog.getId().toString())
                .contains(userDog.getChatTelegram().getId().toString())
                .contains(userDog.getAddress().toString())
                .contains(userDog.getNameUser().toString())
                .contains(userDog.getPhone().toString());
        assertThat(userRepository.findById(userDog.getId()).orElse(null))
                .isNull();

        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity2.getBody())
                .contains(userCat.getId().toString())
                .contains(userCat.getChatTelegram().getId().toString())
                .contains(userCat.getAddress().toString())
                .contains(userCat.getNameUser().toString())
                .contains(userCat.getPhone().toString());
        assertThat(userRepository.findById(userCat.getId()).orElse(null))
                .isNull();
    }

    @Test
    public void getAllVolunteersTest() {
        List<User> userVolunteerList = userRepository.findAll().stream().
                filter(User::isVolunteer).
                collect(Collectors.toList());

        UserDto[] userDtos1 = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/volunteers/",
                        UserDto[].class);
        UserDto[] userDtos2 = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/volunteers/",
                        UserDto[].class);
        assertThat(userDtos1.length + userDtos2.length)
                .isEqualTo(userVolunteerList.size());
    }

    @Test
    public void getAllClients() {
        List<User> userVolunteerList = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                collect(Collectors.toList());

        UserDto[] userDtos1 = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/clients/",
                        UserDto[].class);
        UserDto[] userDtos2 = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/clients/",
                        UserDto[].class);
        assertThat(userDtos1.length + userDtos2.length)
                .isEqualTo(userVolunteerList.size());
    }
}