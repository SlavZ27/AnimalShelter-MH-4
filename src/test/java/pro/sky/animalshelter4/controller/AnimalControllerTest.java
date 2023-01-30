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
import pro.sky.animalshelter4.exception.AnimalNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnimalControllerTest {
    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "animal";
    private final static String SHELTER1 = "DOG";
    private final static String SHELTER2 = "CAT";

    @InjectMocks
    @Autowired
    private AnimalController animalController;
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
        assertThat(telegramPhotoService).isNotNull();
        assertThat(telegramUnfinishedRequestService).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(telegramBotUpdatesListener).isNotNull();
    }


    @Test
    void createAnimal() {
        Animal animal =
                animalRepository.findAll().stream().findAny().orElse(null);
        assertThat(animal).isNotNull();
        AnimalDto animalDto = dtoMapperService.toDto(animal);
        animalOwnershipRepository.findAll().stream().filter(animalOwnership ->
                        animalOwnership.getAnimal().getId().equals(animal.getId())).
                forEach(animalOwnership -> {
                    reportRepository.findAll().stream().filter(report ->
                                    report.getAnimalOwnership().getId().equals(animalOwnership.getId())).
                            forEach(report -> reportRepository.delete(report));
                    animalOwnershipRepository.delete(animalOwnership);
                });
        animalRepository.delete(animal);
        assertThat(animalRepository.findById(animal.getId()).orElse(null))
                .isNull();
        final int countAnimal = animalRepository.findAll().size();

        AnimalDto responseEntity1 = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING,
                        animalDto,
                        AnimalDto.class);
        AnimalDto responseEntity2 = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING,
                        animalDto,
                        AnimalDto.class);
        assertEquals(countAnimal + 2, animalRepository.findAll().size());

        assertThat(responseEntity1).isNotNull();
        assertThat(responseEntity1.getNameAnimal()).isEqualTo(animalDto.getNameAnimal());
        assertThat(responseEntity1.getBorn()).isEqualTo(animalDto.getBorn());

        assertThat(responseEntity2).isNotNull();
        assertThat(responseEntity2.getNameAnimal()).isEqualTo(animalDto.getNameAnimal());
        assertThat(responseEntity2.getBorn()).isEqualTo(animalDto.getBorn());
    }

    @Test
    void readAnimal() {
        Animal animalDog =
                animalRepository.findAll().stream().
                        filter(animal -> animal.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        Animal animalCat =
                animalRepository.findAll().stream().
                        filter(animal -> animal.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);
        assertThat(animalDog).isNotNull();
        assertThat(animalCat).isNotNull();

        AnimalDto animalDogDto = dtoMapperService.toDto(animalDog);
        AnimalDto animalCatDto = dtoMapperService.toDto(animalCat);

        final int countAnimal = animalRepository.findAll().size();

        AnimalDto responseEntity1 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + animalDog.getId(),
                        AnimalDto.class);
        AnimalDto responseEntity2 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + animalCat.getId(),
                        AnimalDto.class);
        AnimalDto responseEntity3 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + animalDog.getId(),
                        AnimalDto.class);
        assertEquals(countAnimal, animalRepository.findAll().size());

        assertThat(responseEntity1).isNotNull();
        assertThat(responseEntity1.getNameAnimal()).isEqualTo(animalDogDto.getNameAnimal());
        assertThat(responseEntity1.getBorn()).isEqualTo(animalDogDto.getBorn());

        assertThat(responseEntity2).isNotNull();
        assertThat(responseEntity2.getNameAnimal()).isEqualTo(animalCatDto.getNameAnimal());
        assertThat(responseEntity2.getBorn()).isEqualTo(animalCatDto.getBorn());

        assertThat(responseEntity3).isNotNull();
        assertThat(responseEntity3.getNameAnimal()).isNotEqualTo(responseEntity1.getNameAnimal());
        assertThat(responseEntity3.getBorn()).isNotEqualTo(responseEntity1.getBorn());
    }

    @Test
    void readAnimalNegative() {
        List<Long> animalIdList = animalRepository.findAll().stream().map(Animal::getId).collect(Collectors.toList());
        Long index = (long) random.nextInt(animalIdList.size());
        while (animalIdList.contains(index)) {
            index = (long) random.nextInt(animalIdList.size());
        }

        AnimalDto responseEntity1 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + index,
                        AnimalDto.class);
        AnimalDto responseEntity2 = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + index,
                        AnimalDto.class);

        Long finalIndex = index;
        assertThatExceptionOfType(AnimalNotFoundException.class).isThrownBy(() ->
                animalService.readAnimalWithShelter(finalIndex, "DOG"));
        assertThatExceptionOfType(AnimalNotFoundException.class).isThrownBy(() ->
                animalService.readAnimalWithShelter(finalIndex, "CAT"));
    }

    @Test
    void updateAnimal() {
        Animal animalDog =
                animalRepository.findAll().stream().
                        filter(animal -> animal.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        Animal animalCat =
                animalRepository.findAll().stream().
                        filter(animal -> animal.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);
        assertThat(animalDog).isNotNull();
        assertThat(animalCat).isNotNull();

        AnimalDto animalDogDto = dtoMapperService.toDto(animalDog);
        AnimalDto animalCatDto = dtoMapperService.toDto(animalCat);
        animalDogDto.setNameAnimal("fgfrergth");
        animalCatDto.setNameAnimal("fgfrergth");

        final int countAnimal = animalRepository.findAll().size();
        testRestTemplate.
                put("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING,
                        animalDogDto);
        testRestTemplate.
                put("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING,
                        animalCatDto);
        assertEquals(countAnimal, animalRepository.findAll().size());

        Animal animalActual1 = animalRepository.findById(animalDogDto.getId()).orElse(null);
        Animal animalActual2 = animalRepository.findById(animalCatDto.getId()).orElse(null);

        assertThat(animalActual1).isNotNull();
        assertThat(animalActual1.getNameAnimal()).isEqualTo("fgfrergth");
        assertThat(animalActual1.getBorn()).isEqualTo(animalDog.getBorn());
        assertThat(animalActual2).isNotNull();
        assertThat(animalActual2.getNameAnimal()).isEqualTo("fgfrergth");
        assertThat(animalActual2.getBorn()).isEqualTo(animalCat.getBorn());
    }

    @Test
    void deleteAnimal() {
        final int countAnimal = animalRepository.findAll().size();
        Animal animalDog =
                animalRepository.findAll().stream().
                        filter(animal -> animal.getShelter().getshelterDesignation().equals("DOG")).
                        findAny().orElse(null);
        Animal animalCat =
                animalRepository.findAll().stream().
                        filter(animal -> animal.getShelter().getshelterDesignation().equals("CAT")).
                        findAny().orElse(null);
        assertThat(animalDog).isNotNull();
        assertThat(animalCat).isNotNull();

        animalOwnershipRepository.findAll().stream().filter(animalOwnership ->
                        animalOwnership.getAnimal().getId().equals(animalDog.getId()) ||
                                animalOwnership.getAnimal().getId().equals(animalCat.getId())).
                forEach(animalOwnership -> {
                    reportRepository.findAll().stream().filter(report ->
                                    report.getAnimalOwnership().getId().equals(animalOwnership.getId())).
                            forEach(report -> reportRepository.delete(report));
                    animalOwnershipRepository.delete(animalOwnership);
                });

        ResponseEntity<String> responseEntity1 = testRestTemplate
                .exchange("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING + "/" + animalDog.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });
        ResponseEntity<String> responseEntity2 = testRestTemplate
                .exchange("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING + "/" + animalCat.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });

        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity1.getBody())
                .contains(animalDog.getId().toString())
                .contains(animalDog.getNameAnimal().toString())
                .contains(animalDog.getBorn().toString());
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity2.getBody())
                .contains(animalCat.getId().toString())
                .contains(animalCat.getNameAnimal().toString())
                .contains(animalCat.getBorn().toString());
        assertEquals(countAnimal - 2, animalRepository.findAll().size());
        assertThat(animalRepository.findById(animalDog.getId()).orElse(null))
                .isNull();
        assertThat(animalRepository.findById(animalCat.getId()).orElse(null))
                .isNull();
    }

    @Test
    public void getAllAnimalTest() {
        final long countAnimal = animalRepository.findAll().size();
        AnimalDto[] animalDtosDog = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + SHELTER1 + "/" + REQUEST_MAPPING_STRING,
                        AnimalDto[].class);
        AnimalDto[] animalDtosCat = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + SHELTER2 + "/" + REQUEST_MAPPING_STRING,
                        AnimalDto[].class);
        assertThat(animalDtosDog.length + animalDtosCat.length)
                .isEqualTo(countAnimal);
    }
}