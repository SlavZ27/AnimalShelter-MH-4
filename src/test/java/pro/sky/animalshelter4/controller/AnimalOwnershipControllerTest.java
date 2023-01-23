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
import pro.sky.animalshelter4.entityDto.AnimalDto;
import pro.sky.animalshelter4.entityDto.AnimalOwnershipDto;
import pro.sky.animalshelter4.exception.AnimalNotFoundException;
import pro.sky.animalshelter4.exception.AnimalOwnershipNotFoundException;
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
class AnimalOwnershipControllerTest {
    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "animal_ownership";
    @InjectMocks
    @Autowired
    private AnimalOwnershipController animalOwnershipController;
    @MockBean
    private TelegramBot telegramBot;
    @Autowired
    private AnimalOwnershipRepository animalOwnershipRepository;
    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private AnimalTypeRepository animalTypeRepository;
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
    private AnimalTypeService animalTypeService;
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
        animalTypeRepository.deleteAll();
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
            AnimalType animalType = new AnimalType();
            animalType.setTypeAnimal(generator.generateAnimalType());
            animalType = animalTypeRepository.save(animalType);
            for (int j = 0; j < animalInt; j++) {
                //generate and remember animal
                Animal animal = new Animal();
                animal.setNameAnimal(generator.generateNameIfEmpty(null));
                animal.setAnimalType(animalType);
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
        animalTypeRepository.deleteAll();
        unfinishedRequestTelegramRepository.deleteAll();
        chatRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertThat(telegramBot).isNotNull();
        assertThat(animalOwnershipRepository).isNotNull();
        assertThat(animalRepository).isNotNull();
        assertThat(animalTypeRepository).isNotNull();
        assertThat(callRequestRepository).isNotNull();
        assertThat(chatRepository).isNotNull();
        assertThat(photoRepository).isNotNull();
        assertThat(reportRepository).isNotNull();
        assertThat(unfinishedRequestTelegramRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(animalOwnershipService).isNotNull();
        assertThat(animalService).isNotNull();
        assertThat(animalTypeService).isNotNull();
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
        assertThat(animalOwnershipController).isNotNull();
    }


    @Test
    void createAnimalOwnership() {
        AnimalOwnership animalOwnership =
                animalOwnershipRepository.findAll().stream().findAny().orElse(null);
        assertThat(animalOwnership).isNotNull();
        AnimalOwnershipDto animalOwnershipDto = dtoMapperService.toDto(animalOwnership);
        reportRepository.findAll().stream().filter(report ->
                        report.getAnimalOwnership().getId().equals(animalOwnership.getId())).
                forEach(report -> reportRepository.delete(report));
        animalOwnershipRepository.delete(animalOwnership);
        assertThat(animalOwnershipRepository.findById(animalOwnership.getId()).orElse(null))
                .isNull();
        final int countAnimalOwnership = animalOwnershipRepository.findAll().size();

        AnimalOwnershipDto responseEntity = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        animalOwnershipDto,
                        AnimalOwnershipDto.class);
        assertEquals(countAnimalOwnership + 1, animalOwnershipRepository.findAll().size());

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getIdAnimal()).isEqualTo(animalOwnershipDto.getIdAnimal());
        assertThat(responseEntity.getIdOwner()).isEqualTo(animalOwnershipDto.getIdOwner());
        assertThat(responseEntity.getDateEndTrial()).isEqualTo(animalOwnershipDto.getDateEndTrial());
        assertThat(responseEntity.getDateStartOwn()).isEqualTo(animalOwnershipDto.getDateStartOwn());
        assertThat(responseEntity.getApprove()).isEqualTo(animalOwnershipDto.getApprove());
    }

    @Test
    void readAnimalOwnership() {
        AnimalOwnership animalOwnership =
                animalOwnershipRepository.findAll().stream().findAny().orElse(null);
        assertThat(animalOwnership).isNotNull();

        AnimalOwnershipDto animalOwnershipDto = dtoMapperService.toDto(animalOwnership);
        assertThat(animalOwnershipRepository.findById(animalOwnershipDto.getId()).orElse(null))
                .isNotNull();

        final int countAnimalOwnership = animalOwnershipRepository.findAll().size();

        AnimalOwnershipDto responseEntity = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + animalOwnership.getId(),
                        AnimalOwnershipDto.class);
        assertEquals(countAnimalOwnership, animalOwnershipRepository.findAll().size());

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getIdAnimal()).isEqualTo(animalOwnershipDto.getIdAnimal());
        assertThat(responseEntity.getIdOwner()).isEqualTo(animalOwnershipDto.getIdOwner());
        assertThat(responseEntity.getDateEndTrial()).isEqualTo(animalOwnershipDto.getDateEndTrial());
        assertThat(responseEntity.getDateStartOwn()).isEqualTo(animalOwnershipDto.getDateStartOwn());
        assertThat(responseEntity.getApprove()).isEqualTo(animalOwnershipDto.getApprove());
    }

    @Test
    void readAnimalOwnershipNegative() {
        List<Long> animalOwnershipIdList = animalOwnershipRepository.findAll().stream().
                map(AnimalOwnership::getId).collect(Collectors.toList());
        Long index = (long) random.nextInt(animalOwnershipIdList.size());
        while (animalOwnershipIdList.contains(index)) {
            index = (long) random.nextInt(animalOwnershipIdList.size());
        }

        AnimalOwnershipDto responseEntity = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + index,
                        AnimalOwnershipDto.class);

        Long finalIndex = index;
        assertThatExceptionOfType(AnimalOwnershipNotFoundException.class).isThrownBy(()
                -> animalOwnershipService.readAnimalOwnership(finalIndex));
    }

    @Test
    void updateAnimalOwnership() {
        AnimalOwnership animalOwnership =
                animalOwnershipRepository.findAll().stream().findAny().orElse(null);
        assertThat(animalOwnership).isNotNull();

        AnimalOwnershipDto animalOwnershipDto = dtoMapperService.toDto(animalOwnership);
        assertThat(animalOwnershipDto).isNotNull();
        assertThat(animalOwnershipRepository.findById(animalOwnershipDto.getId()).orElse(null))
                .isNotNull();
        LocalDate dateRemember = LocalDate.of(2000, 02, 22);
        animalOwnershipDto.setDateEndTrial(dateRemember);

        final int countAnimalOwnership = animalOwnershipRepository.findAll().size();
        testRestTemplate.
                put("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        animalOwnershipDto);
        assertEquals(countAnimalOwnership, animalOwnershipRepository.findAll().size());

        AnimalOwnership animalOwnershipActual = animalOwnershipRepository.findById(animalOwnershipDto.getId()).orElse(null);

        assertThat(animalOwnershipActual).isNotNull();
        assertThat(animalOwnershipActual.getDateEndTrial().toString()).isEqualTo(dateRemember.toString());
        assertThat(animalOwnershipActual.getDateStartOwn().toString()).isEqualTo(animalOwnership.getDateStartOwn().toString());
        assertThat(animalOwnershipActual.getAnimal().getId()).isEqualTo(animalOwnership.getAnimal().getId());
        assertThat(animalOwnershipActual.getOwner().getId()).isEqualTo(animalOwnership.getOwner().getId());
    }

    @Test
    void deleteAnimalOwnership() {
        final int countAnimalOwnership = animalOwnershipRepository.findAll().size();
        AnimalOwnership animalOwnership = animalOwnershipRepository.findAll().stream().findFirst().orElse(null);

        reportRepository.findAll().stream().filter(report ->
                        report.getAnimalOwnership().getId().equals(animalOwnership.getId())).
                forEach(report -> reportRepository.delete(report));

        ResponseEntity<String> responseEntity = testRestTemplate
                .exchange("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + animalOwnership.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .contains(animalOwnership.getId().toString())
                .contains(animalOwnership.getAnimal().getId().toString())
                .contains(animalOwnership.getOwner().getId().toString())
                .contains(animalOwnership.getDateEndTrial().toString())
                .contains(animalOwnership.getDateStartOwn().toString());
        assertEquals(countAnimalOwnership - 1, animalOwnershipRepository.findAll().size());
        assertThat(animalOwnershipRepository.findById(animalOwnership.getId()).orElse(null))
                .isNull();
    }

    @Test
    public void getAllAnimalOwnershipTest() {
        final long countAnimalOwnership = animalOwnershipRepository.findAll().size();
        AnimalOwnershipDto[] animalOwnershipDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        AnimalOwnershipDto[].class);
        assertThat(animalOwnershipDtos.length)
                .isEqualTo(countAnimalOwnership);
    }
}