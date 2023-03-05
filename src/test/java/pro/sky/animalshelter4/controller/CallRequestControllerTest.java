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
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import pro.sky.animalshelter4.Generator;
import pro.sky.animalshelter4.entity.*;
import pro.sky.animalshelter4.entityDto.AnimalDto;
import pro.sky.animalshelter4.entityDto.CallRequestDto;
import pro.sky.animalshelter4.exception.AnimalNotFoundException;
import pro.sky.animalshelter4.exception.CallRequestNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CallRequestControllerTest {

    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "call_request";
    private final static String SHELTER1 = "DOG";
    private final static String SHELTER2 = "CAT";
    @Autowired
    @InjectMocks
    private CallRequestController callRequestController;
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
    void createCallRequest() {
        CallRequest callRequestDog =
                callRequestRepository.findAll().stream().
                        filter(callRequest -> callRequest.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        CallRequest callRequestCat =
                callRequestRepository.findAll().stream().
                        filter(callRequest -> callRequest.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);

        CallRequestDto callRequestDtoDog = dtoMapperService.toDto(callRequestDog);
        CallRequestDto callRequestDtoCat = dtoMapperService.toDto(callRequestCat);
        callRequestRepository.delete(callRequestDog);
        callRequestRepository.delete(callRequestCat);

        final int countCallRequest = callRequestRepository.findAll().size();

        CallRequestDto responseEntity1 = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING,
                        callRequestDtoDog,
                        CallRequestDto.class);
        CallRequestDto responseEntity2 = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING,
                        callRequestDtoCat,
                        CallRequestDto.class);
        assertEquals(countCallRequest + 2, callRequestRepository.findAll().size());

        assertThat(responseEntity1).isNotNull();
        assertThat(responseEntity1.getIdClient()).isEqualTo(callRequestDtoDog.getIdClient());
        assertThat(responseEntity1.getIdVolunteer()).isEqualTo(callRequestDtoDog.getIdVolunteer());
        assertThat(responseEntity1.getLocalDateTimeOpen()).isEqualTo(callRequestDtoDog.getLocalDateTimeOpen());
        assertThat(responseEntity1.getLocalDateTimeClose()).isEqualTo(callRequestDtoDog.getLocalDateTimeClose());
        assertThat(responseEntity1.isOpen()).isEqualTo(callRequestDtoDog.isOpen());

        assertThat(responseEntity2).isNotNull();
        assertThat(responseEntity2.getIdClient()).isEqualTo(callRequestDtoCat.getIdClient());
        assertThat(responseEntity2.getIdVolunteer()).isEqualTo(callRequestDtoCat.getIdVolunteer());
        assertThat(responseEntity2.getLocalDateTimeOpen()).isEqualTo(callRequestDtoCat.getLocalDateTimeOpen());
        assertThat(responseEntity2.getLocalDateTimeClose()).isEqualTo(callRequestDtoCat.getLocalDateTimeClose());
        assertThat(responseEntity2.isOpen()).isEqualTo(callRequestDtoCat.isOpen());
    }

    @Test
    void readCallRequest() {
        CallRequest callRequestDog =
                callRequestRepository.findAll().stream().
                        filter(callRequest -> callRequest.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        CallRequest callRequestCat =
                callRequestRepository.findAll().stream().
                        filter(callRequest -> callRequest.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);

        CallRequestDto callRequestDtoDog = dtoMapperService.toDto(callRequestDog);
        CallRequestDto callRequestDtoCat = dtoMapperService.toDto(callRequestCat);

        final int countCallRequest = callRequestRepository.findAll().size();

        CallRequestDto responseEntity1 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + callRequestDog.getId(),
                        CallRequestDto.class);
        CallRequestDto responseEntity2 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + callRequestCat.getId(),
                        CallRequestDto.class);
        CallRequestDto responseEntity3 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + callRequestDog.getId(),
                        CallRequestDto.class);
        assertEquals(countCallRequest, callRequestRepository.findAll().size());

        assertThat(responseEntity1).isNotNull();
        assertThat(responseEntity1.getIdClient()).isEqualTo(callRequestDtoDog.getIdClient());
        assertThat(responseEntity1.getIdVolunteer()).isEqualTo(callRequestDtoDog.getIdVolunteer());
        assertThat(responseEntity1.getLocalDateTimeOpen()).isEqualTo(callRequestDtoDog.getLocalDateTimeOpen());
        assertThat(responseEntity1.getLocalDateTimeClose()).isEqualTo(callRequestDtoDog.getLocalDateTimeClose());
        assertThat(responseEntity1.isOpen()).isEqualTo(callRequestDtoDog.isOpen());

        assertThat(responseEntity2).isNotNull();
        assertThat(responseEntity2.getIdClient()).isEqualTo(callRequestDtoCat.getIdClient());
        assertThat(responseEntity2.getIdVolunteer()).isEqualTo(callRequestDtoCat.getIdVolunteer());
        assertThat(responseEntity2.getLocalDateTimeOpen()).isEqualTo(callRequestDtoCat.getLocalDateTimeOpen());
        assertThat(responseEntity2.getLocalDateTimeClose()).isEqualTo(callRequestDtoCat.getLocalDateTimeClose());
        assertThat(responseEntity2.isOpen()).isEqualTo(callRequestDtoCat.isOpen());

        if (responseEntity3.getId() != null) {
            assertThat(responseEntity3.getIdClient()).isNotEqualTo(responseEntity1.getIdClient());
            assertThat(responseEntity3.getIdVolunteer()).isNotEqualTo(responseEntity1.getIdVolunteer());
            assertThat(responseEntity3.getLocalDateTimeOpen()).isNotEqualTo(responseEntity1.getLocalDateTimeOpen());
            assertThat(responseEntity3.getLocalDateTimeClose()).isNotEqualTo(responseEntity1.getLocalDateTimeClose());
        }
    }

    @Test
    void readCallRequestNegative() {
        List<Long> callRequestIdList = callRequestRepository.findAll().stream().map(CallRequest::getId).collect(Collectors.toList());
        Long index = (long) random.nextInt(callRequestIdList.size());
        while (callRequestIdList.contains(index)) {
            index = (long) random.nextInt(callRequestIdList.size());
        }

        CallRequestDto responseEntity1 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + index,
                        CallRequestDto.class);
        CallRequestDto responseEntity2 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + index,
                        CallRequestDto.class);

        Long finalIndex = index;
        assertThatExceptionOfType(CallRequestNotFoundException.class).isThrownBy(() ->
                callRequestService.readCallRequest(finalIndex, "DOG"));
        assertThatExceptionOfType(CallRequestNotFoundException.class).isThrownBy(() ->
                callRequestService.readCallRequest(finalIndex, "CAT"));
    }

    @Test
    void updateCallRequest() {
        CallRequest callRequestDog =
                callRequestRepository.findAll().stream().
                        filter(callRequest -> callRequest.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        CallRequest callRequestCat =
                callRequestRepository.findAll().stream().
                        filter(callRequest -> callRequest.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);

        CallRequestDto callRequestDtoDog = dtoMapperService.toDto(callRequestDog);
        CallRequestDto callRequestDtoCat = dtoMapperService.toDto(callRequestCat);

        callRequestDtoDog.setOpen(!callRequestDtoDog.isOpen());
        callRequestDtoCat.setOpen(!callRequestDtoCat.isOpen());

        final int countCallRequest = callRequestRepository.findAll().size();
        testRestTemplate.
                put("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING,
                        callRequestDtoDog);
        testRestTemplate.
                put("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING,
                        callRequestDtoCat);

        assertEquals(countCallRequest, callRequestRepository.findAll().size());

        CallRequest callRequestActual1 = callRequestRepository.findById(callRequestDtoDog.getId()).orElse(null);
        CallRequest callRequestActual2 = callRequestRepository.findById(callRequestDtoCat.getId()).orElse(null);

        assertThat(callRequestDog).isNotNull();
        assertThat(callRequestDog.getClient().getId()).isEqualTo(callRequestActual1.getClient().getId());
        assertThat(callRequestDog.getVolunteer().getId()).isEqualTo(callRequestActual1.getVolunteer().getId());
        assertThat(callRequestDog.getLocalDateTimeOpen()).isEqualTo(callRequestActual1.getLocalDateTimeOpen());
        assertThat(callRequestDog.getLocalDateTimeClose()).isEqualTo(callRequestActual1.getLocalDateTimeClose());
        assertThat(callRequestDog.isOpen()).isEqualTo(!callRequestActual1.isOpen());

        assertThat(callRequestCat).isNotNull();
        assertThat(callRequestCat.getClient().getId()).isEqualTo(callRequestActual2.getClient().getId());
        assertThat(callRequestCat.getVolunteer().getId()).isEqualTo(callRequestActual2.getVolunteer().getId());
        assertThat(callRequestCat.getLocalDateTimeOpen()).isEqualTo(callRequestActual2.getLocalDateTimeOpen());
        assertThat(callRequestCat.getLocalDateTimeClose()).isEqualTo(callRequestActual2.getLocalDateTimeClose());
        assertThat(callRequestCat.isOpen()).isEqualTo(!callRequestActual2.isOpen());
    }


    @Test
    void deleteCallRequest() {
        final int countCallRequest = callRequestRepository.findAll().size();

        CallRequest callRequestDog =
                callRequestRepository.findAll().stream().
                        filter(callRequest -> callRequest.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        CallRequest callRequestCat =
                callRequestRepository.findAll().stream().
                        filter(callRequest -> callRequest.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);

        ResponseEntity<String> responseEntity1 = testRestTemplate
                .exchange("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + callRequestDog.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });
        ResponseEntity<String> responseEntity2 = testRestTemplate
                .exchange("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + callRequestCat.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });

        assertEquals(countCallRequest - 2, callRequestRepository.findAll().size());

        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity1.getBody())
                .contains(callRequestDog.getId().toString())
                .contains(callRequestDog.getClient().getId().toString())
                .contains(callRequestDog.getVolunteer().getId().toString())
                .contains(callRequestDog.getLocalDateTimeOpen().toString().substring(0, 17));
        assertThat(callRequestRepository.findById(callRequestDog.getId()).orElse(null))
                .isNull();

        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity2.getBody())
                .contains(callRequestCat.getId().toString())
                .contains(callRequestCat.getClient().getId().toString())
                .contains(callRequestCat.getVolunteer().getId().toString())
                .contains(callRequestCat.getLocalDateTimeOpen().toString().substring(0, 17));
        assertThat(callRequestRepository.findById(callRequestCat.getId()).orElse(null))
                .isNull();
    }

    @Test
    public void getAllCallRequestVolunteerTest() {
        User user = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user1 -> user1.getShelter().getshelterDesignation().equals("DOG")).
                findFirst().orElse(null);
        assertThat(user).isNotNull();

        List<CallRequest> callRequestListDog = callRequestRepository.findAll().stream().
                filter(CallRequest::isOpen).
                filter(callRequest -> callRequest.getVolunteer().getId().equals(user.getId())).
                collect(Collectors.toList());

        CallRequestDto[] callRequestDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/volunteer/" + user.getId(),
                        CallRequestDto[].class);
        assertThat(callRequestDtos.length)
                .isEqualTo(callRequestListDog.size());
    }

    @Test
    public void getAllCallRequestClientTest() {
        User user = userRepository.findAll().stream().
                filter(user1 -> !user1.isVolunteer()).
                filter(user1 -> user1.getShelter().getshelterDesignation().equals("DOG")).
                findFirst().orElse(null);
        assertThat(user).isNotNull();

        List<CallRequest> callRequestListDog = callRequestRepository.findAll().stream().
                filter(CallRequest::isOpen).
                filter(callRequest -> callRequest.getClient().getId().equals(user.getId())).
                collect(Collectors.toList());

        CallRequestDto[] callRequestDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/client/" + user.getId(),
                        CallRequestDto[].class);
        assertThat(callRequestDtos.length)
                .isEqualTo(callRequestListDog.size());
    }

    @Test
    public void getAllOpenCallRequestTest() {
        List<CallRequest> callRequestListDog = callRequestRepository.findAll().stream().
                filter(CallRequest::isOpen).
                filter(callRequest -> callRequest.getShelter().getshelterDesignation().equals("DOG")).
                collect(Collectors.toList());

        CallRequestDto[] callRequestDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/open/",
                        CallRequestDto[].class);
        assertThat(callRequestDtos.length)
                .isEqualTo(callRequestListDog.size());
    }

    @Test
    public void getAllCloseCallRequestTest() {
        List<CallRequest> callRequestListDog = callRequestRepository.findAll().stream().
                filter(callRequest -> !callRequest.isOpen()).
                filter(callRequest -> callRequest.getShelter().getshelterDesignation().equals("DOG")).
                collect(Collectors.toList());

        CallRequestDto[] callRequestDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/close/",
                        CallRequestDto[].class);
        assertThat(callRequestDtos.length)
                .isEqualTo(callRequestListDog.size());
    }
}