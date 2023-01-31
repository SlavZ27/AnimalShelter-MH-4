package pro.sky.animalshelter4.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pro.sky.animalshelter4.Generator;
import pro.sky.animalshelter4.entity.*;
import pro.sky.animalshelter4.info.*;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.repository.*;
import pro.sky.animalshelter4.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


/**
 * The class contains methods for testing all the chains of interaction between the telegram bot and the program.
 * Interaction starts {@link TelegramBotUpdatesListener#process}, ends {@link TelegramBot#execute(BaseRequest)}
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class TelegramBotUpdatesListenerTest {
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
    @Autowired
    @InjectMocks
    private TelegramBotUpdatesListener telegramBotUpdatesListener;
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

    /**
     * testing an incoming message with {@link Command#START}
     */
    @Test
    public void STARTTest() {
        List<Shelter> shelterList = shelterRepository.findAll();
        Long id = 50L;
        String command = Command.START.getTextCommand();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection("", "", "", id, command, true)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(2)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(2);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_HELLO +
                updateList.get(0).message().from().firstName() + " " + updateList.get(0).message().from().lastName() + ".\n");
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_SHELTER);
        for (int i = 0; i < shelterList.size(); i++) {
            assertTrue(actual1.getParameters().get("reply_markup").toString().contains(
                    Command.SET_SHELTER +
                            TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                            shelterList.get(i).getshelterDesignation()));
        }
    }

    /**
     * testing an incoming message and press button with {@link Command#INFO}
     */
    @Test
    public void INFOTest() {
        String command = Command.INFO.getTextCommand();
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoAboutShelterDog.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(InfoAboutShelterCat.getInfoEn());
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    /**
     * testing an incoming message and press button with {@link Command#HOW}
     */
    @Test
    public void HOWTest() {
        String command = Command.HOW.getTextCommand();
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoTakeADog.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo("");
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void INFO_DISABILITIESTest() {
        String command = Command.INFO_DISABILITIES.getTextCommand();
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoDogsWithDisabilities.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(InfoCatWithDisabilities.getInfoEn());
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void INFO_LIST_DOCUMENTSTest() {
        String command = Command.INFO_LIST_DOCUMENTS.getTextCommand();
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoListOfDocuments.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(InfoListOfDocumentsCat.getInfoEn());
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void INFO_RECOMMEND_HOME_ANIMALTest() {
        String command = Command.INFO_RECOMMEND_HOME_ANIMAL.getTextCommand();
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoRecommendationsHomeDog.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(InfoRecommendationsHomeCat.getInfoEn());
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void INFO_RECOMMEND_HOME_ANIMAL_SMALLTest() {
        String command = Command.INFO_RECOMMEND_HOME_ANIMAL_SMALL.getTextCommand();
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoRecommendationsHomeSmallDog.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(InfoRecommendationsHomeSmallCat.getInfoEn());
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void INFO_REFUSETest() {
        String command = Command.INFO_REFUSE.getTextCommand();
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoRefuseDogFromShelter.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo("");
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void INFO_TIPSTest() {
        String command = Command.INFO_TIPS.getTextCommand();
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoTipsFromDogHandler.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo("");
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void INFO_TRANSPORTATIONTest() {
        String command = Command.INFO_TRANSPORTATION.getTextCommand();
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoTransportationAnimals.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo("");
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void INFO_NEED_HANDLERTest() {
        String command = Command.INFO_NEED_HANDLER.getTextCommand();
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoWhyDoYouNeedDogHandler.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo("");
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void INFO_GET_ANIMALTest() {
        String command = Command.INFO_GET_ANIMAL.getTextCommand();
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoGettingKnowDog.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(InfoGettingKnowCat.getInfoEn());
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    /**
     * testing an incoming message with unknown Command
     */
    @Test
    public void UNKNOWNCommandTest() {
        String command = "/fegfdhesfhdgmghrfdgg";
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SORRY_I_DONT_KNOW_COMMAND);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SORRY_I_DONT_KNOW_COMMAND);
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    /**
     * testing an incoming message with {@link Command#START}
     */
    @Test
    public void UnknownTextTest() {
        String command = "fegfdhesfhdgmghrfdgg";
        Chat chatClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null).getChatTelegram();
        Chat chatVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null).getChatTelegram();
        assertThat(chatClientDog).isNotNull();
        assertThat(chatVolunteerCat).isNotNull();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command + " 1",
                        false)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SORRY_I_KNOW_THIS);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SORRY_I_KNOW_THIS);
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    /**
     * testing an incoming press button with {@link Command#EMPTY_CALLBACK_DATA_FOR_BUTTON}
     */
    @Test
    public void EmptyCommandTest() {
        Long id = 50L;
        String command = Command.EMPTY_CALLBACK_DATA_FOR_BUTTON.getTextCommand();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection("", "", "", id, command, true)));
        telegramBotUpdatesListener.process(updateList);
        Mockito.verify(telegramBot, times(0)).execute(any());
    }

    /**
     * testing an incoming message and press button with {@link Command#CALL_REQUEST}
     * With and without Volunteers in {@link ChatRepository}
     */
    @Test
    public void CALL_REQUESTTest() {
        String command = Command.CALL_REQUEST.getTextCommand();
        //get userClientDogWithoutPhone
        User userClientDogWithoutPhone = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        assert userClientDogWithoutPhone != null;
        userClientDogWithoutPhone.setPhone(null);
        userClientDogWithoutPhone = userRepository.save(userClientDogWithoutPhone);
        //get chatClientDogWithoutPhone
        Chat chatClientDogWithoutPhone = userClientDogWithoutPhone.getChatTelegram();
        //get userClientDog
        User finalUserClientDogWithoutPhone = userClientDogWithoutPhone;
        User userClientDog = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> !user.getId().equals(finalUserClientDogWithoutPhone.getId())).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        //get chatClientDog
        assert userClientDog != null;
        Chat chatClientDog = userClientDog.getChatTelegram();
        //remember list IDs of Volunteers Dog
        List<String> listIdChatVolunteerDOGToString = userRepository.findAll().stream().
                filter(user -> user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                map(user -> user.getChatTelegram().getId().toString()).
                collect(Collectors.toList());
        //get userClientCat
        User userClientCat = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null);
        //get chatClientCat
        assert userClientCat != null;
        Chat chatClientCat = userClientCat.getChatTelegram();
        //remember list IDs of Volunteers Cat
        List<String> listIdChatVolunteerCATToString = userRepository.findAll().stream().
                filter(user -> user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                map(user -> user.getChatTelegram().getId().toString()).
                collect(Collectors.toList());
        assertThat(chatClientDog).isNotNull();
        //delete callRequests of users
        callRequestRepository.findAll().stream().filter(callRequest ->
                        callRequest.getClient().getId().equals(userClientCat.getId()) ||
                                callRequest.getClient().getId().equals(userClientDog.getId()) ||
                                callRequest.getClient().getId().equals(finalUserClientDogWithoutPhone.getId())).
                forEach(callRequest -> callRequestRepository.delete(callRequest));
        //close all callRequests
        callRequestRepository.findAll().stream().filter(callRequest ->
                        callRequest.isOpen()).
                forEach(callRequest -> {
                    callRequest.setOpen(false);
                    callRequestRepository.save(callRequest);
                });
        //Update ClientDog
        //Same update to check that a new call request will not be created
        //Update ClientCat
        //Update ChatClientDogWithoutPhone
        //Should be three new CallRequests
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatClientCat.getUserNameTelegram(),
                        chatClientCat.getFirstNameUser(),
                        chatClientCat.getLastNameUser(),
                        chatClientCat.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatClientDogWithoutPhone.getUserNameTelegram(),
                        chatClientDogWithoutPhone.getFirstNameUser(),
                        chatClientDogWithoutPhone.getLastNameUser(),
                        chatClientDogWithoutPhone.getId(),
                        command,
                        false)));
        //remember count callRequests
        int countCallRequestRepository = callRequestRepository.findAll().size();
        //Volunteers is present
        telegramBotUpdatesListener.process(updateList);
        //check that only 3 has been added
        assertThat(callRequestRepository.findAll().size()).isEqualTo(countCallRequestRepository + 3);

        //find new callRequest
        CallRequest callRequestUserCat = callRequestRepository.findAll().stream().filter(
                callRequest1 -> callRequest1.getClient().getId().equals(userClientCat.getId())).findAny().orElse(null);
        CallRequest callRequestUserDog = callRequestRepository.findAll().stream().filter(
                callRequest1 -> callRequest1.getClient().getId().equals(userClientDog.getId())).findAny().orElse(null);
        CallRequest callRequestUserDogWithoutPhone = callRequestRepository.findAll().stream().filter(
                callRequest1 -> callRequest1.getClient().getId().equals(finalUserClientDogWithoutPhone.getId())).findAny().orElse(null);

        assertThat(callRequestUserCat).isNotNull();
        assertThat(callRequestUserDog).isNotNull();
        assertThat(callRequestUserDogWithoutPhone).isNotNull();

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(12)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(12);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);
        SendMessage actual4 = actualList.get(4);
        SendMessage actual5 = actualList.get(5);
        SendMessage actual6 = actualList.get(6);
        SendMessage actual7 = actualList.get(7);
        SendMessage actual8 = actualList.get(8);
        SendMessage actual9 = actualList.get(9);
        SendMessage actual10 = actualList.get(10);
        SendMessage actual11 = actualList.get(11);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        assertTrue(actual0.getParameters().get("text").toString().
                contains(CallRequestService.MESSAGE_SUCCESSFUL_CREATION));

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        assertTrue(actual1.getParameters().get("text").toString().
                contains(TelegramBotSenderService.MESSAGE_SELECT_COMMAND));

        assertTrue(listIdChatVolunteerDOGToString.contains(actual2.getParameters().get("chat_id").toString()));
        assertTrue(actual2.getParameters().get("text").toString().
                contains(CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST));
        assertTrue(actual2.getParameters().get("text").toString().
                contains(callRequestUserDog.getId().toString()));
        assertTrue(actual2.getParameters().get("reply_markup").toString().contains(
                Command.CLOSE_CALL_REQUEST +
                        TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                        callRequestUserDog.getId().toString()));

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        assertTrue(actual3.getParameters().get("text").toString().
                contains(CallRequestService.MESSAGE_SUCCESSFUL_CREATION));

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        assertTrue(actual4.getParameters().get("text").toString().
                contains(TelegramBotSenderService.MESSAGE_SELECT_COMMAND));

        assertTrue(listIdChatVolunteerDOGToString.contains(actual5.getParameters().get("chat_id").toString()));
        assertTrue(actual5.getParameters().get("text").toString().
                contains(CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST));
        assertTrue(actual5.getParameters().get("text").toString().
                contains(callRequestUserDog.getId().toString()));
        assertTrue(actual5.getParameters().get("reply_markup").toString().contains(
                Command.CLOSE_CALL_REQUEST +
                        TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                        callRequestUserDog.getId().toString()));

        Assertions.assertThat(actual6.getParameters().get("chat_id")).isEqualTo(chatClientCat.getId());
        assertTrue(actual6.getParameters().get("text").toString().
                contains(CallRequestService.MESSAGE_SUCCESSFUL_CREATION));

        Assertions.assertThat(actual7.getParameters().get("chat_id")).isEqualTo(chatClientCat.getId());
        assertTrue(actual7.getParameters().get("text").toString().
                contains(TelegramBotSenderService.MESSAGE_SELECT_COMMAND));

        assertTrue(listIdChatVolunteerCATToString.contains(actual8.getParameters().get("chat_id").toString()));
        assertTrue(actual8.getParameters().get("text").toString().
                contains(CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST));
        assertTrue(actual8.getParameters().get("text").toString().
                contains(callRequestUserCat.getId().toString()));
        assertTrue(actual8.getParameters().get("reply_markup").toString().contains(
                Command.CLOSE_CALL_REQUEST +
                        TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                        callRequestUserCat.getId().toString()));

        Assertions.assertThat(actual9.getParameters().get("chat_id")).isEqualTo(chatClientDogWithoutPhone.getId());
        assertTrue(actual9.getParameters().get("text").toString().
                contains(CallRequestService.MESSAGE_SUCCESSFUL_CREATION));

        Assertions.assertThat(actual10.getParameters().get("chat_id")).isEqualTo(chatClientDogWithoutPhone.getId());
        assertTrue(actual10.getParameters().get("text").toString().
                contains(TelegramBotSenderService.MESSAGE_SORRY_I_DONT_KNOW_YOUR_PHONE));
        assertTrue(actual10.getParameters().get("reply_markup").toString().contains(
                TelegramBotSenderService.NAME_BUTTON_FOR_CANCEL));

        assertTrue(listIdChatVolunteerDOGToString.contains(actual11.getParameters().get("chat_id").toString()));
        assertTrue(actual11.getParameters().get("text").toString().
                contains(CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST));
        assertTrue(actual11.getParameters().get("text").toString().
                contains(callRequestUserDogWithoutPhone.getId().toString()) ||
                actual11.getParameters().get("text").toString().
                        contains(callRequestUserDog.getId().toString()));
        assertTrue(
                actual11.getParameters().get("reply_markup").toString().contains(
                        Command.CLOSE_CALL_REQUEST +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                callRequestUserDog.getId().toString()) ||
                        actual11.getParameters().get("reply_markup").toString().contains(
                                Command.CLOSE_CALL_REQUEST +
                                        TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                        callRequestUserDogWithoutPhone.getId().toString())
        );
        //Check adding UnfinishedRequestTelegram for change phone if phone empty
        UnfinishedRequestTelegram unfinishedRequestTelegram = unfinishedRequestTelegramRepository.findAll().stream().
                filter(unfinishedRequestTelegram1 -> unfinishedRequestTelegram1.getChat().getId().
                        equals(chatClientDogWithoutPhone.getId())).
                findFirst().orElse(null);
        assertThat(unfinishedRequestTelegram).isNotNull();
        assertThat(unfinishedRequestTelegram.getCommand()).isEqualTo(Command.CHANGE_PHONE.getTextCommand());

        //delete all the volunteers
        userRepository.findAll().stream().
                filter(User::isVolunteer).forEach(user -> {
                    user.setVolunteer(false);
                    userRepository.save(user);
                });
        //delete callRequests of users
        callRequestRepository.findAll().stream().filter(callRequest ->
                        callRequest.getClient().getId().equals(userClientDog.getId())).
                forEach(callRequest -> callRequestRepository.delete(callRequest));
        //Update ClientDog
        updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatClientDog.getUserNameTelegram(),
                        chatClientDog.getFirstNameUser(),
                        chatClientDog.getLastNameUser(),
                        chatClientDog.getId(),
                        command,
                        false)));
        //Volunteer is absent
        countCallRequestRepository = callRequestRepository.findAll().size();
        telegramBotUpdatesListener.process(updateList);
        //checking that none has been added
        assertThat(callRequestRepository.findAll().size()).isEqualTo(countCallRequestRepository);

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(14)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(14);
        SendMessage actual12 = actualList.get(12);
        SendMessage actual13 = actualList.get(13);

        Assertions.assertThat(actual12.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        assertTrue(actual12.getParameters().get("text").toString().
                contains(UserService.MESSAGE_VOLUNTEERS_IS_ABSENT));

        Assertions.assertThat(actual13.getParameters().get("chat_id")).isEqualTo(chatClientDog.getId());
        assertTrue(actual13.getParameters().get("text").toString().
                contains(TelegramBotSenderService.MESSAGE_SELECT_COMMAND));
    }

    /**
     * A test that verifies the execution of a {@link Command#CALL_REQUEST} that is not intended for the volunteer
     */
    @Test
    public void permissionTest() {
        //Command.CALL_REQUEST is not available to the volunteer
        String command = Command.CALL_REQUEST.getTextCommand();
        //get userVolunteerDog
        User userVolunteerDog = userRepository.findAll().stream().
                filter(user -> user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        //get chatVolunteerDog
        assert userVolunteerDog != null;
        Chat chatVolunteerDog = userVolunteerDog.getChatTelegram();
        //get userVolunteerCat
        User userClientCat = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null);
        //get chatVolunteerCat
        assert userClientCat != null;
        Chat chatClientCat = userClientCat.getChatTelegram();
        //Update VolunteerDog
        //Update ClientCat
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatClientCat.getUserNameTelegram(),
                        chatClientCat.getFirstNameUser(),
                        chatClientCat.getLastNameUser(),
                        chatClientCat.getId(),
                        command,
                        false)));
        //Command.CALL_REQUEST is not available to the volunteer
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(5)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(5);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);
        SendMessage actual4 = actualList.get(4);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SORRY_I_KNOW_THIS);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatClientCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_SUCCESSFUL_CREATION);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatClientCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual4.getParameters().get("text").toString().
                contains(CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST)).isTrue();

    }

    @Test
    public void CALL_CLIENTTest() {
        String command = Command.CALL_CLIENT.getTextCommand();
        //get userVolunteerDog
        User userVolunteerDog = userRepository.findAll().stream().
                filter(user -> user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        //get chatVolunteerDog
        assert userVolunteerDog != null;
        Chat chatVolunteerDog = userVolunteerDog.getChatTelegram();
        //get userVolunteerCat
        User userVolunteerCat = userRepository.findAll().stream().
                filter(user -> user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null);
        //get chatVolunteerCat
        assert userVolunteerCat != null;
        Chat chatVolunteerCat = userVolunteerCat.getChatTelegram();
        //Update volunteerDog
        //Update volunteerCat
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        command,
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command,
                        false)));
        //get list callRequests for userVolunteerDog
        List<CallRequest> callRequestListDog = callRequestRepository.findAll().stream().
                filter(callRequest -> callRequest.getVolunteer().getId().equals(userVolunteerDog)).
                filter(callRequest -> callRequest.getShelter().getshelterDesignation().equals("DOG")).
                collect(Collectors.toList());
        //get list callRequests for userVolunteerCat
        List<CallRequest> callRequestListCat = callRequestRepository.findAll().stream().
                filter(callRequest -> callRequest.getVolunteer().getId().equals(userVolunteerCat)).
                filter(callRequest -> callRequest.getShelter().getshelterDesignation().equals("CAT")).
                collect(Collectors.toList());
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text").toString().contains(CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST)).isTrue();

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual2.getParameters().get("text").toString().contains(CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST)).isTrue();

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        //close all callRequest
        callRequestRepository.findAll().stream().
                filter(CallRequest::isOpen).forEach(callRequest -> {
                    callRequest.setOpen(false);
                    callRequestRepository.save(callRequest);
                });
        telegramBotUpdatesListener.process(updateList);

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(8)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(8);
        SendMessage actual4 = actualList.get(4);
        SendMessage actual5 = actualList.get(5);
        SendMessage actual6 = actualList.get(6);
        SendMessage actual7 = actualList.get(7);

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual4.getParameters().get("text").toString().contains(CallRequestService.MESSAGE_YOU_DONT_HAVE_CALL_REQUEST)).isTrue();

        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual6.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual6.getParameters().get("text").toString().contains(CallRequestService.MESSAGE_YOU_DONT_HAVE_CALL_REQUEST)).isTrue();

        Assertions.assertThat(actual7.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual7.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void CLOSE_CALL_REQUESTTest() {
        String command = Command.CLOSE_CALL_REQUEST.getTextCommand();
        //get userVolunteerDog
        User userVolunteerDog = userRepository.findAll().stream().
                filter(user -> user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        //get chatVolunteerDog
        assert userVolunteerDog != null;
        Chat chatVolunteerDog = userVolunteerDog.getChatTelegram();
        //get userVolunteerCat
        User userVolunteerCat = userRepository.findAll().stream().
                filter(user -> user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null);
        //get chatVolunteerCat
        assert userVolunteerCat != null;
        Chat chatVolunteerCat = userVolunteerCat.getChatTelegram();
        //get list open callRequests for userVolunteerDog
        CallRequest callRequestDog = callRequestRepository.findAll().stream().
                filter(callRequest ->
                        callRequest.getVolunteer().getId().equals(userVolunteerDog.getId()) &&
                                callRequest.getShelter().getshelterDesignation().equals("DOG")).
                findFirst().orElse(null);
        assertThat(callRequestDog).isNotNull();
        callRequestDog.setOpen(true);
        callRequestDog.setLocalDateTimeClose(null);
        callRequestDog = callRequestRepository.save(callRequestDog);
        //get list open callRequests for userVolunteerCat
        CallRequest callRequestCat = callRequestRepository.findAll().stream().
                filter(callRequest ->
                        callRequest.getVolunteer().getId().equals(userVolunteerCat.getId()) &&
                                callRequest.getShelter().getshelterDesignation().equals("CAT")).
                findFirst().orElse(null);
        assertThat(callRequestCat).isNotNull();
        callRequestCat.setOpen(true);
        callRequestCat.setLocalDateTimeClose(null);
        callRequestCat = callRequestRepository.save(callRequestCat);
        //Update volunteerDog
        //Update volunteerDog with CallRequest with id of volunteerCat
        //Update volunteerCat
        //Update volunteerCat again
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        command +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                callRequestDog.getId(),
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        command +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                callRequestCat.getId(),
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                callRequestCat.getId(),
                        false),
                generator.generateUpdateMessageWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        command +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                callRequestCat.getId(),
                        false)));
        telegramBotUpdatesListener.process(updateList);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(8)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(8);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);
        SendMessage actual4 = actualList.get(4);
        SendMessage actual5 = actualList.get(5);
        SendMessage actual6 = actualList.get(6);
        SendMessage actual7 = actualList.get(7);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_CALL_REQUEST_IS_CLOSE);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_CALL_REQUEST_NOT_FOUND);
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_CALL_REQUEST_IS_CLOSE);
        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual6.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual6.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_CALL_REQUEST_IS_CLOSE);
        Assertions.assertThat(actual7.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual7.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        callRequestDog = callRequestRepository.findById(callRequestDog.getId()).orElse(null);
        callRequestCat = callRequestRepository.findById(callRequestCat.getId()).orElse(null);
        assertThat(callRequestDog).isNotNull();
        assertThat(callRequestDog.isOpen()).isFalse();
        assertThat(callRequestCat).isNotNull();
        assertThat(callRequestCat.isOpen()).isFalse();
    }

    @Test
    public void CHANGE_PHONETest() {
        //find any userClient
        User userClient = userRepository.findAll().stream().filter(user ->
                !user.isVolunteer()).findAny().orElse(null);
        //get chatClient
        Chat chatClient = userClient.getChatTelegram();
        //set unfinishedRequestTelegram Command.CHANGE_PHONE for chatClient
        UnfinishedRequestTelegram unfinishedRequestTelegram = new UnfinishedRequestTelegram();
        unfinishedRequestTelegram.setChat(chatClient);
        unfinishedRequestTelegram.setCommand(Command.CHANGE_PHONE.getTextCommand());
        unfinishedRequestTelegramRepository.save(unfinishedRequestTelegram);
        String phoneGood = "89998887766";
        //Update include Command.CHANGE_PHONE + too short phone <5 symbols
        //Update include Command.CHANGE_PHONE + too large phone >15 symbols
        //Update include Command.CHANGE_PHONE + good phone
        //Update include Command.CHANGE_PHONE
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.CHANGE_PHONE.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                "8888", false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.CHANGE_PHONE.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                "8888888888888888", false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.CHANGE_PHONE.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                phoneGood, false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.CHANGE_PHONE.getTextCommand()
                        , false)
        ));
        telegramBotUpdatesListener.process(updateList);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(5)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(5);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);
        SendMessage actual4 = actualList.get(4);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(UserService.MESSAGE_BAD_PHONE);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(UserService.MESSAGE_BAD_PHONE);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(UserService.MESSAGE_PHONE_IS_OK + " " +
                phoneGood);
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SORRY_I_DONT_KNOW_YOUR_PHONE);
    }

    @Test
    public void CREATE_OWNERSHIPTest() {
        Long idIncorrectUser = random.nextLong();
        List<Long> userIds = userRepository.findAll().stream().map(User::getId).collect(Collectors.toList());
        while (userIds.contains(idIncorrectUser)) {
            idIncorrectUser = random.nextLong();
        }
        Long idIncorrectAnimal = random.nextLong();
        List<Long> animalIds = animalRepository.findAll().stream().map(Animal::getId).collect(Collectors.toList());
        while (animalIds.contains(idIncorrectAnimal)) {
            idIncorrectAnimal = random.nextLong();
        }
        //find userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        assertThat(userVolunteer).isNotNull();
        //find userClient
        User userClient = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        assertThat(userVolunteer).isNotNull();
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        //get chatClient
        Chat chatClient = userClient.getChatTelegram();
        //find free animal
        Animal animal = animalRepository.findAll().stream().
                filter(animal1 -> animal1.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        assertThat(animal).isNotNull();
        animalOwnershipRepository.findAll().stream().
                filter(animalOwnership -> animalOwnership.getAnimal().getId().equals(animal.getId())).
                forEach(animalOwnership -> {
                    reportRepository.findAll().stream().
                            filter(report -> report.getAnimalOwnership().getId().equals(animalOwnership.getId())).
                            forEach(report -> reportRepository.delete(report));
                    animalOwnershipRepository.delete(animalOwnership);
                });
        //Update include only Command.CREATE_OWNERSHIP
        //Update include Command.CREATE_OWNERSHIP + id of userOwner
        //Update include Command.CREATE_OWNERSHIP + id of userOwner + id of animal
        //Update include Command.CREATE_OWNERSHIP + incorrect id of userOwner + id of animal
        //Update include Command.CREATE_OWNERSHIP + id of userOwner + incorrect id of animal
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.CREATE_OWNERSHIP.getTextCommand(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.CREATE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                userClient.getId(), false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.CREATE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                userClient.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                animal.getId(), false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.CREATE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                idIncorrectUser, false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.CREATE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                userClient.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                idIncorrectAnimal, false)
        ));

        int countAnimalOwnership = animalOwnershipRepository.findAll().size();
        telegramBotUpdatesListener.process(updateList);
        assertEquals(animalOwnershipRepository.findAll().size(), countAnimalOwnership + 1);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(6)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(6);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);
        SendMessage actual4 = actualList.get(4);
        SendMessage actual5 = actualList.get(5);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(UserService.CAPTION_SELECT_USER);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(AnimalService.CAPTION_SELECT_ANIMAL);

        String validateString = "is now the owner of";
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual2.getParameters().get("text").toString().contains(validateString)).isTrue();

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        validateString = "OK.You is now the owner of ";
        Assertions.assertThat(actual4.getParameters().get("text").toString().substring(0, validateString.length())).
                isEqualTo(validateString);
        //delete all animals
        reportRepository.deleteAll();
        animalOwnershipRepository.deleteAll();
        animalRepository.deleteAll();
        updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.CREATE_OWNERSHIP.getTextCommand(),
                        false)
        ));
        telegramBotUpdatesListener.process(updateList);
        //delete all users
        callRequestRepository.deleteAll();
        userRepository.findAll().stream().filter(user -> !user.isVolunteer()).forEach(user -> userRepository.delete(user));
        telegramBotUpdatesListener.process(updateList);

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(8)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(8);
        SendMessage actual6 = actualList.get(6);
        SendMessage actual7 = actualList.get(7);

        Assertions.assertThat(actual6.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual6.getParameters().get("text")).isEqualTo(AnimalService.MESSAGE_ANIMALS_IS_ABSENT);

        Assertions.assertThat(actual7.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual7.getParameters().get("text")).isEqualTo(UserService.MESSAGE_CLIENTS_IS_ABSENT);
    }

    @Test
    public void ADD_ANIMALTest() {
        String nameAnimal = "Tuzik";
        //remember count of animal
        int countOfAnimal = animalRepository.findAll().size();
        //find userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        assertThat(userVolunteer).isNotNull();
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        //Update include Command.ADD_ANIMAL + name of animal
        //Update include only Command.ADD_ANIMAL
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.ADD_ANIMAL.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + nameAnimal,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.ADD_ANIMAL.getTextCommand(),
                        false)
        ));

        unfinishedRequestTelegramRepository.deleteAll();

        telegramBotUpdatesListener.process(updateList);
        assertThat(animalRepository.findAll().size()).isEqualTo(countOfAnimal + 1);

        //Check adding UnfinishedRequestTelegram for add animal
        UnfinishedRequestTelegram unfinishedRequestTelegram = unfinishedRequestTelegramRepository.findAll().stream().
                filter(unfinishedRequestTelegram1 -> unfinishedRequestTelegram1.getChat().getId().
                        equals(chatVolunteer.getId())).
                findFirst().orElse(null);
        assertThat(unfinishedRequestTelegram).isNotNull();
        assertThat(unfinishedRequestTelegram.getCommand()).isEqualTo(Command.ADD_ANIMAL.getTextCommand());

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(3)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(3);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(AnimalService.MESSAGE_ANIMAL_CREATED + " " + nameAnimal);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(AnimalService.CAPTION_WRITE_NAME_OF_ANIMAL);

        //Update include only name of animal
        updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        nameAnimal + nameAnimal,
                        false)));

        telegramBotUpdatesListener.process(updateList);
        assertThat(animalRepository.findAll().size()).isEqualTo(countOfAnimal + 2);

        //Check close UnfinishedRequestTelegram for add animal
        unfinishedRequestTelegram = unfinishedRequestTelegramRepository.findAll().stream().
                filter(unfinishedRequestTelegram1 -> unfinishedRequestTelegram1.getChat().getId().
                        equals(chatVolunteer.getId())).
                findFirst().orElse(null);
        assertThat(unfinishedRequestTelegram).isNull();

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(5)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(5);
        SendMessage actual3 = actualList.get(3);
        SendMessage actual4 = actualList.get(4);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(AnimalService.MESSAGE_ANIMAL_CREATED +
                " " + nameAnimal + nameAnimal);

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Animal animalDog1 = animalRepository.findAll().stream().
                filter(animal -> animal.getNameAnimal().equals(nameAnimal)).
                filter(animal -> animal.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        Animal animalDog2 = animalRepository.findAll().stream().
                filter(animal -> animal.getNameAnimal().equals(nameAnimal + nameAnimal)).
                filter(animal -> animal.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        Animal animalCat1 = animalRepository.findAll().stream().
                filter(animal -> animal.getNameAnimal().equals(nameAnimal)).
                filter(animal -> animal.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null);
        Animal animalCat2 = animalRepository.findAll().stream().
                filter(animal -> animal.getNameAnimal().equals(nameAnimal + nameAnimal)).
                filter(animal -> animal.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null);
        assertThat(animalDog1).isNotNull();
        assertThat(animalDog2).isNotNull();
        assertThat(animalCat1).isNull();
        assertThat(animalCat2).isNull();
    }


    @Test
    public void REPORTTest() {
        //remember animalOwnership
        AnimalOwnership animalOwnership = animalOwnershipRepository.findAll().stream().findAny().orElse(null);
        assert animalOwnership != null;
        animalOwnership.setOpen(true);
        animalOwnership.setDateStartOwn(LocalDate.now().minusDays(15));
        animalOwnership.setDateEndTrial(LocalDate.now().plusDays(15));
        animalOwnership = animalOwnershipRepository.save(animalOwnership);
        Long idAnimalOwnership = animalOwnership.getId();
        //delete all report of animalOwnership
        reportRepository.findAll().stream().filter(report -> report.getAnimalOwnership().getId().equals(idAnimalOwnership))
                .forEach(report -> reportRepository.delete(report));
        //get userClient
        User userClient = animalOwnership.getOwner();
        //get chatClient
        Chat chatClient = userClient.getChatTelegram();
        //write parameters
        String diet = generator.generateMessageIfEmpty(null);
        String feeling = generator.generateMessageIfEmpty(null);
        String behavior = generator.generateMessageIfEmpty(null);
        String idPhoto = "12345678543456787654";
        //Update include only Command.REPORT
        //Update include only diet
        //Update include only feeling
        //Update include only photo
        //Update include only behavior
        Update updateWithReport =
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.REPORT.getTextCommand(),
                        false);
        Update updateWithDiet =
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        diet,
                        false);
        Update updateWithFeeling =
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        feeling,
                        false);
        Update updateWithPhoto =
                generator.generateUpdateMessagePhotoWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        "",
                        idPhoto,
                        false);
        Update updateWithBehavior =
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        behavior,
                        false);

        int reportCount = reportRepository.findAll().size();
        telegramBotUpdatesListener.process(Collections.singletonList(updateWithReport));

        assertThat(reportRepository.findAll().size()).isEqualTo(reportCount + 1);

        //Check presence UnfinishedRequestTelegram for add report
        assertThat(unfinishedRequestTelegramRepository.findAll().stream().
                filter(unfinishedRequestTelegram1 -> unfinishedRequestTelegram1.getChat().getId().
                        equals(chatClient.getId()) && unfinishedRequestTelegram1.getCommand().
                        equals(Command.REPORT.getTextCommand())).
                findFirst().orElse(null)).isNotNull();

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(1)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(1);
        SendMessage actual0 = actualList.get(0);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(
                ReportService.MESSAGE_WRITE_DIET + " or " + ReportService.MESSAGE_SEND_PHOTO);

        telegramBotUpdatesListener.process(Collections.singletonList(updateWithDiet));

        assertThat(reportRepository.findAll().size()).isEqualTo(reportCount + 1);

        //Check presence UnfinishedRequestTelegram for add report
        assertThat(unfinishedRequestTelegramRepository.findAll().stream().
                filter(unfinishedRequestTelegram1 -> unfinishedRequestTelegram1.getChat().getId().
                        equals(chatClient.getId()) && unfinishedRequestTelegram1.getCommand().
                        equals(Command.REPORT.getTextCommand())).
                findFirst().orElse(null)).isNotNull();

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(2)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(2);

        SendMessage actual1 = actualList.get(1);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(
                ReportService.MESSAGE_WRITE_FEELING + " or " + ReportService.MESSAGE_SEND_PHOTO);

        telegramBotUpdatesListener.process(Collections.singletonList(updateWithFeeling));

        assertThat(reportRepository.findAll().size()).isEqualTo(reportCount + 1);

        //Check presence UnfinishedRequestTelegram for add report
        assertThat(unfinishedRequestTelegramRepository.findAll().stream().
                filter(unfinishedRequestTelegram1 -> unfinishedRequestTelegram1.getChat().getId().
                        equals(chatClient.getId()) && unfinishedRequestTelegram1.getCommand().
                        equals(Command.REPORT.getTextCommand())).
                findFirst().orElse(null)).isNotNull();

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(3)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(3);

        SendMessage actual2 = actualList.get(2);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(
                ReportService.MESSAGE_WRITE_BEHAVIOR + " or " + ReportService.MESSAGE_SEND_PHOTO);

        telegramBotUpdatesListener.process(Collections.singletonList(updateWithPhoto));

        assertThat(reportRepository.findAll().size()).isEqualTo(reportCount + 1);

        //Check presence UnfinishedRequestTelegram for add report
        assertThat(unfinishedRequestTelegramRepository.findAll().stream().
                filter(unfinishedRequestTelegram1 -> unfinishedRequestTelegram1.getChat().getId().
                        equals(chatClient.getId()) && unfinishedRequestTelegram1.getCommand().
                        equals(Command.REPORT.getTextCommand())).
                findFirst().orElse(null)).isNotNull();

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);

        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(
                ReportService.MESSAGE_WRITE_BEHAVIOR + " ");

        telegramBotUpdatesListener.process(Collections.singletonList(updateWithBehavior));

        assertThat(reportRepository.findAll().size()).isEqualTo(reportCount + 1);

        //Check presence UnfinishedRequestTelegram for add report
        assertThat(unfinishedRequestTelegramRepository.findAll().stream().
                filter(unfinishedRequestTelegram1 -> unfinishedRequestTelegram1.getChat().getId().
                        equals(chatClient.getId()) && unfinishedRequestTelegram1.getCommand().
                        equals(Command.REPORT.getTextCommand())).
                findFirst().orElse(null)).isNull();

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(6)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(6);

        SendMessage actual4 = actualList.get(4);
        SendMessage actual5 = actualList.get(5);

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual4.getParameters().get("text").toString().contains(ReportService.MESSAGE_REPORT_CREATE)).isTrue();

        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Report report = reportRepository.findAll().stream().
                filter(report1 -> report1.getAnimalOwnership().getOwner().getId().equals(userClient.getId())).
                findAny().orElse(null);
        assertThat(report).isNotNull();
        assertThat(report.getDiet()).isEqualTo(diet);
        assertThat(report.getFeeling()).isEqualTo(feeling);
        assertThat(report.getBehavior()).isEqualTo(behavior);
        assertThat(report.getShelter().getId()).isEqualTo(userClient.getShelter().getId());
        assertThat(report.getPhoto().getIdMedia()).isEqualTo(idPhoto);
        assertThat(report.getPhoto().getShelter().getId()).isEqualTo(userClient.getShelter().getId());
    }


    @Test
    public void VIEW_REPORTTest() {
        //get userVolunteerDog
        User userVolunteerDog = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        //get chatVolunteerDog
        assert userVolunteerDog != null;
        Chat chatVolunteerDog = userVolunteerDog.getChatTelegram();
        //get userVolunteerCat
        User userVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null);
        //get chatVolunteerCat
        assert userVolunteerCat != null;
        Chat chatVolunteerCat = userVolunteerCat.getChatTelegram();
        //Update include only Command.VIEW_REPORT for userVolunteerDog
        //Update include only Command.VIEW_REPORT for userVolunteerCat
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.VIEW_REPORT.getTextCommand(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        Command.VIEW_REPORT.getTextCommand(),
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);

        ArgumentCaptor<SendMessage> argumentCaptorMessage = ArgumentCaptor.forClass(SendMessage.class);
        ArgumentCaptor<SendPhoto> argumentCaptorPhoto = ArgumentCaptor.forClass(SendPhoto.class);
        verify(telegramBot, times(6)).execute(argumentCaptorMessage.capture());
        verify(telegramBot, times(6)).execute(argumentCaptorPhoto.capture());
        List<SendMessage> actualListMessage = argumentCaptorMessage.getAllValues();
        List<SendPhoto> actualListPhoto = argumentCaptorPhoto.getAllValues();
        Assertions.assertThat(actualListMessage.size()).isEqualTo(6);
        Assertions.assertThat(actualListPhoto.size()).isEqualTo(6);
        SendMessage actual0 = actualListMessage.get(0);
        SendPhoto actual1 = actualListPhoto.get(1);
        SendMessage actual2 = actualListMessage.get(2);
        SendMessage actual3 = actualListMessage.get(3);
        SendPhoto actual4 = actualListPhoto.get(4);
        SendMessage actual5 = actualListMessage.get(5);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        String validateStr = "Report\n" +
                "AnimalOwnership: AnimalOwnership\n" +
                "Owner: Client of shelter";
        assertTrue(actual0.getParameters().get("text").toString().contains(validateStr));

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual1.getParameters().get("photo")).isNotNull();

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_APPROVE_OR_NOT);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        assertTrue(actual3.getParameters().get("text").toString().contains(validateStr));

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual4.getParameters().get("photo")).isNotNull();

        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_APPROVE_OR_NOT);

        //close all report for userVolunteerDog
        reportRepository.findAll().stream().
                filter(report -> report.isApprove() == null).
                filter(report -> report.getShelter().getshelterDesignation().equals("DOG")).
                forEach(report -> {
                    report.setApprove(true);
                    reportRepository.save(report);
                });
        telegramBotUpdatesListener.process(updateList);

        argumentCaptorMessage = ArgumentCaptor.forClass(SendMessage.class);
        argumentCaptorPhoto = ArgumentCaptor.forClass(SendPhoto.class);
        verify(telegramBot, times(11)).execute(argumentCaptorMessage.capture());
        verify(telegramBot, times(11)).execute(argumentCaptorPhoto.capture());
        actualListMessage = argumentCaptorMessage.getAllValues();
        actualListPhoto = argumentCaptorPhoto.getAllValues();
        Assertions.assertThat(actualListMessage.size()).isEqualTo(11);
        Assertions.assertThat(actualListPhoto.size()).isEqualTo(11);
        SendMessage actual6 = actualListMessage.get(6);
        SendMessage actual7 = actualListMessage.get(7);
        SendMessage actual8 = actualListMessage.get(8);
        SendPhoto actual9 = actualListPhoto.get(9);
        SendMessage actual10 = actualListMessage.get(10);

        Assertions.assertThat(actual6.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual6.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_ALL_REPORT_ARE_APPROVE);

        Assertions.assertThat(actual7.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual7.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual8.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        assertTrue(actual8.getParameters().get("text").toString().contains(validateStr));

        Assertions.assertThat(actual9.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual9.getParameters().get("photo")).isNotNull();

        Assertions.assertThat(actual10.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual10.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_APPROVE_OR_NOT);
    }


    @Test
    public void APPROVE_REPORTTest() {
        //get userVolunteerDog
        User userVolunteerDog = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        //get chatVolunteerDog
        assert userVolunteerDog != null;
        Chat chatVolunteerDog = userVolunteerDog.getChatTelegram();
        //get userVolunteerCat
        User userVolunteerCat = userRepository.findAll().stream().
                filter(User::isVolunteer).
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                findAny().orElse(null);
        //get chatVolunteerCat
        assert userVolunteerCat != null;
        Chat chatVolunteerCat = userVolunteerCat.getChatTelegram();
        //get open reportDogForGood (approve == null)
        //get open reportDogForBad (approve == null)
        //get open reportDogDel (approve == null)
        Report reportDogForGood = reportRepository.findAll().stream().
                filter(report1 -> report1.getShelter().getshelterDesignation().equals("DOG") &&
                        report1.isApprove() == null).
                findAny().orElse(null);
        Report finalReportDogForGood = reportDogForGood;
        Report reportDogForBad = reportRepository.findAll().stream().
                filter(report1 -> report1.getShelter().getshelterDesignation().equals("DOG") &&
                        report1.isApprove() == null &&
                        !report1.getId().equals(finalReportDogForGood.getId())).
                findAny().orElse(null);
        Report finalReportDogForBad = reportDogForBad;
        Report reportDogForDel = reportRepository.findAll().stream().
                filter(report1 -> report1.getShelter().getshelterDesignation().equals("DOG") &&
                        report1.isApprove() == null &&
                        !report1.getId().equals(finalReportDogForGood.getId()) &&
                        !report1.getId().equals(finalReportDogForBad.getId())).
                findAny().orElse(null);
        reportRepository.delete(reportDogForDel);

        Chat chatOwnerDogGood = reportDogForGood.getAnimalOwnership().getOwner().getChatTelegram();
        Chat chatOwnerDogBad = reportDogForBad.getAnimalOwnership().getOwner().getChatTelegram();

        //Update include Command.APPROVE_REPORT + report.getId() + true for DOG from CAT
        //Update include Command.APPROVE_REPORT + report.getId() + true for DOG from DOG
        //Update include Command.APPROVE_REPORT + report.getId() + true for DOG from DOG
        //Update include Command.APPROVE_REPORT + report.getId() + false for DOG from DOG
        //Update include Command.APPROVE_REPORT + non-exist report.getId() + true for DOG from DOG
        List<Update> updateListCat = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        Command.APPROVE_REPORT.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + reportDogForGood.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true,
                        false)));
        List<Update> updateListDog = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.APPROVE_REPORT.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + reportDogForGood.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.APPROVE_REPORT.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + reportDogForGood.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.APPROVE_REPORT.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + reportDogForBad.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + false,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.APPROVE_REPORT.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + reportDogForDel.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true,
                        false)
        ));

        telegramBotUpdatesListener.process(updateListCat);

        //check report don't approve
        Report report = reportRepository.findById(reportDogForGood.getId()).orElse(null);
        assertThat(report).isNotNull();
        assertThat(report.getApprove()).isNull();


        telegramBotUpdatesListener.process(updateListDog);

        reportDogForGood = reportRepository.findById(reportDogForGood.getId()).orElse(null);
        reportDogForBad = reportRepository.findById(reportDogForBad.getId()).orElse(null);
        reportDogForDel = reportRepository.findById(reportDogForDel.getId()).orElse(null);
        assertThat(reportDogForDel).isNull();
        assert reportDogForGood != null;
        assert reportDogForGood.isApprove() != null;
        assertThat(reportDogForGood.isApprove()).isTrue();
        assert reportDogForBad != null;
        assert reportDogForBad.isApprove() != null;
        assertThat(reportDogForBad.isApprove()).isFalse();

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

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_REPORT_IS_PLACED_GOOD);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatOwnerDogGood.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_REPORT_IS_PLACED_GOOD);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_REPORT_IS_PLACED_GOOD);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatOwnerDogGood.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_REPORT_IS_PLACED_GOOD);

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_REPORT_IS_PLACED_BAD);

        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(chatOwnerDogBad.getId());
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_REPORT_IS_PLACED_BAD_OWNER);
    }

    @Test
    public void VIEW_OWNERSHIPTest() {
        //remember actual animalOwnershipDog
        AnimalOwnership animalOwnershipDog = animalOwnershipRepository.findAll().stream().
                filter(animalOwnership1 -> animalOwnership1.getShelter().getshelterDesignation().equals("DOG")).
                filter(animalOwnership1 -> animalOwnership1.isApprove() == null).
                findAny().orElse(null);
        animalOwnershipDog.setOpen(true);
        animalOwnershipDog.setDateStartOwn(LocalDate.now().minusDays(31));
        animalOwnershipDog.setDateEndTrial(LocalDate.now().minusDays(1));
        animalOwnershipDog = animalOwnershipRepository.save(animalOwnershipDog);
        assertThat(animalOwnershipDog).isNotNull();
        //close all other animalOwnership
        AnimalOwnership finalAnimalOwnershipDog = animalOwnershipDog;
        animalOwnershipRepository.findAll().stream().
                filter(animalOwnership1 -> animalOwnership1.isOpen() &&
                        !animalOwnership1.getId().equals(finalAnimalOwnershipDog.getId())).
                forEach(animalOwnership1 -> {
                    animalOwnership1.setOpen(false);
                    animalOwnershipRepository.save(animalOwnership1);
                });
        //get userVolunteerDog
        User userVolunteerDog = userRepository.findAll().stream().
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                filter(User::isVolunteer).findAny().orElse(null);
        //get chatVolunteerDog
        Chat chatVolunteerDog = userVolunteerDog.getChatTelegram();
        //get userVolunteerCat
        User userVolunteerCat = userRepository.findAll().stream().
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                filter(User::isVolunteer).findAny().orElse(null);
        //get chatVolunteerCat
        Chat chatVolunteerCat = userVolunteerCat.getChatTelegram();
        //Update include Command.VIEW_OWNERSHIP from Cat
        //Update include Command.VIEW_OWNERSHIP from Dog
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        Command.VIEW_OWNERSHIP.getTextCommand(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.VIEW_OWNERSHIP.getTextCommand(),
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(AnimalOwnershipService.MESSAGE_ALL_ANIMAL_OWNERSHIP_ARE_APPROVE);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteerCat.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        String validateStr = "AnimalOwnership\n" +
                "Owner: Client of shelter";
        assertTrue(actual2.getParameters().get("text").toString().contains(validateStr));

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_APPROVE_OR_NOT);
    }

    @Test
    public void APPROVE_OWNERSHIPTest() {
        //remember actual animalOwnershipGoodDog with isApprove() == null
        AnimalOwnership animalOwnershipGoodDog = animalOwnershipRepository.findAll().stream().
                filter(animalOwnership1 -> animalOwnership1.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        assert animalOwnershipGoodDog != null;
        animalOwnershipGoodDog.setOpen(true);
        animalOwnershipGoodDog.setApprove(null);
        animalOwnershipGoodDog.setDateStartOwn(LocalDate.now().minusDays(31));
        animalOwnershipGoodDog.setDateEndTrial(LocalDate.now().minusDays(1));
        animalOwnershipGoodDog = animalOwnershipRepository.save(animalOwnershipGoodDog);
        //remember actual animalOwnershipBadDog with isApprove() == null
        AnimalOwnership finalAnimalOwnershipGoodDog = animalOwnershipGoodDog;
        AnimalOwnership animalOwnershipBadDog = animalOwnershipRepository.findAll().stream().
                filter(animalOwnership1 -> animalOwnership1.getShelter().getshelterDesignation().equals("DOG") &&
                        !animalOwnership1.getId().equals(finalAnimalOwnershipGoodDog.getId())).
                findAny().orElse(null);
        assert animalOwnershipBadDog != null;
        animalOwnershipBadDog.setOpen(true);
        animalOwnershipBadDog.setApprove(null);
        animalOwnershipBadDog.setDateStartOwn(LocalDate.now().minusDays(31));
        animalOwnershipBadDog.setDateEndTrial(LocalDate.now().minusDays(1));
        animalOwnershipBadDog = animalOwnershipRepository.save(animalOwnershipBadDog);
        //remember not-exist animalOwnership
        AnimalOwnership finalAnimalOwnershipBadDog = animalOwnershipBadDog;
        AnimalOwnership animalOwnershipDelDog = animalOwnershipRepository.findAll().stream().
                filter(animalOwnership1 -> animalOwnership1.getShelter().getshelterDesignation().equals("DOG") &&
                        !animalOwnership1.getId().equals(finalAnimalOwnershipGoodDog.getId()) &&
                        !animalOwnership1.getId().equals(finalAnimalOwnershipBadDog.getId())).
                findAny().orElse(null);
        AnimalOwnership finalAnimalOwnershipDelDog = animalOwnershipDelDog;
        reportRepository.findAll().stream().
                filter(report -> report.getAnimalOwnership().getId().equals(finalAnimalOwnershipDelDog.getId())).
                forEach(report -> reportRepository.delete(report));
        animalOwnershipRepository.delete(animalOwnershipDelDog);
        //get userVolunteerDog
        User userVolunteerDog = userRepository.findAll().stream().
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                filter(User::isVolunteer).findAny().orElse(null);
        //get chatVolunteerDog
        Chat chatVolunteerDog = userVolunteerDog.getChatTelegram();
        //get userVolunteerCat
        User userVolunteerCat = userRepository.findAll().stream().
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                filter(User::isVolunteer).findAny().orElse(null);
        //get chatVolunteerCat
        Chat chatVolunteerCat = userVolunteerCat.getChatTelegram();
        //get chats of client
        Chat chatClientDogGood = animalOwnershipGoodDog.getOwner().getChatTelegram();
        Chat chatClientDogBad = animalOwnershipBadDog.getOwner().getChatTelegram();
        //Update include Command.VIEW_OWNERSHIP + animalOwnershipGood.getId() + true for DOG from CAT
        //Update include Command.VIEW_OWNERSHIP + animalOwnershipGood.getId() + true
        //Update include the same thing
        //Update include Command.VIEW_OWNERSHIP + animalOwnershipBad.getId() + false
        //Update include Command.VIEW_OWNERSHIP + not-exist animalOwnership.getId() + true
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        Command.APPROVE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipGoodDog.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.APPROVE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipGoodDog.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.APPROVE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipGoodDog.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.APPROVE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipBadDog.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + false,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.APPROVE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipDelDog.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true,
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);

        animalOwnershipGoodDog = animalOwnershipRepository.findById(animalOwnershipGoodDog.getId()).orElse(null);
        animalOwnershipBadDog = animalOwnershipRepository.findById(animalOwnershipBadDog.getId()).orElse(null);
        animalOwnershipDelDog = animalOwnershipRepository.findById(animalOwnershipDelDog.getId()).orElse(null);
        assertThat(animalOwnershipDelDog).isNull();
        assert animalOwnershipGoodDog != null;
        assertThat(animalOwnershipGoodDog.isApprove()).isTrue();
        assert animalOwnershipBadDog != null;
        assertThat(animalOwnershipBadDog.isApprove()).isFalse();
        assertThat(animalOwnershipGoodDog.isOpen()).isFalse();
        assertThat(animalOwnershipBadDog.isOpen()).isFalse();

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(8)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(8);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);
        SendMessage actual4 = actualList.get(4);
        SendMessage actual5 = actualList.get(5);
        SendMessage actual6 = actualList.get(6);
        SendMessage actual7 = actualList.get(7);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_GOOD);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatClientDogGood.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_GOOD);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(AnimalOwnershipService.MESSAGE_ALREADY_CLOSE);

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_BAD);

        Assertions.assertThat(actual6.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual6.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual7.getParameters().get("chat_id")).isEqualTo(chatClientDogBad.getId());
        Assertions.assertThat(actual7.getParameters().get("text")).isEqualTo(AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_BAD_OWNER);
    }


    @Test
    public void EXTEND_TRIALTest() {
        //remember actual animalOwnershipDog1 with isApprove() == null
        AnimalOwnership animalOwnershipDog1 = animalOwnershipRepository.findAll().stream().
                filter(animalOwnership1 -> animalOwnership1.getShelter().getshelterDesignation().equals("DOG")).
                findAny().orElse(null);
        assert animalOwnershipDog1 != null;
        animalOwnershipDog1.setOpen(true);
        animalOwnershipDog1.setApprove(null);
        animalOwnershipDog1.setDateStartOwn(LocalDate.now().minusDays(31));
        animalOwnershipDog1.setDateEndTrial(LocalDate.now().minusDays(1));
        animalOwnershipDog1 = animalOwnershipRepository.save(animalOwnershipDog1);
        //remember actual animalOwnershipDog2 with isApprove() == null
        AnimalOwnership finalAnimalOwnershipDog1 = animalOwnershipDog1;
        AnimalOwnership animalOwnershipDog2 = animalOwnershipRepository.findAll().stream().
                filter(animalOwnership1 -> animalOwnership1.getShelter().getshelterDesignation().equals("DOG") &&
                        !animalOwnership1.getId().equals(finalAnimalOwnershipDog1.getId())).
                findAny().orElse(null);
        assert animalOwnershipDog2 != null;
        animalOwnershipDog2.setOpen(true);
        animalOwnershipDog2.setApprove(null);
        animalOwnershipDog2.setDateStartOwn(LocalDate.now().minusDays(31));
        animalOwnershipDog2.setDateEndTrial(LocalDate.now().minusDays(1));
        animalOwnershipDog2 = animalOwnershipRepository.save(animalOwnershipDog2);
        //remember not-exist animalOwnership
        AnimalOwnership finalAnimalOwnershipDog2 = animalOwnershipDog2;
        AnimalOwnership animalOwnershipDelDog = animalOwnershipRepository.findAll().stream().
                filter(animalOwnership1 -> animalOwnership1.getShelter().getshelterDesignation().equals("DOG") &&
                        !animalOwnership1.getId().equals(finalAnimalOwnershipDog1.getId()) &&
                        !animalOwnership1.getId().equals(finalAnimalOwnershipDog2.getId())).
                findAny().orElse(null);
        AnimalOwnership finalAnimalOwnershipDelDog = animalOwnershipDelDog;
        reportRepository.findAll().stream().
                filter(report -> report.getAnimalOwnership().getId().equals(finalAnimalOwnershipDelDog.getId())).
                forEach(report -> reportRepository.delete(report));
        animalOwnershipRepository.delete(animalOwnershipDelDog);
        //get userVolunteerDog
        User userVolunteerDog = userRepository.findAll().stream().
                filter(user -> user.getShelter().getshelterDesignation().equals("DOG")).
                filter(User::isVolunteer).findAny().orElse(null);
        //get chatVolunteerDog
        Chat chatVolunteerDog = userVolunteerDog.getChatTelegram();
        //get userVolunteerCat
        User userVolunteerCat = userRepository.findAll().stream().
                filter(user -> user.getShelter().getshelterDesignation().equals("CAT")).
                filter(User::isVolunteer).findAny().orElse(null);
        //get chatVolunteerCat
        Chat chatVolunteerCat = userVolunteerCat.getChatTelegram();
        //get chats of client
        Chat chatClientDog1 = animalOwnershipDog1.getOwner().getChatTelegram();
        Chat chatClientDog2 = animalOwnershipDog2.getOwner().getChatTelegram();
        //Update include Command.EXTEND_TRIAL + animalOwnershipDog1.getId() + count_extended_days1 for DOG from CAT
        //Update include Command.EXTEND_TRIAL + animalOwnershipDog1.getId() + count_extended_days1
        //Update include Command.EXTEND_TRIAL + animalOwnershipDog2.getId() + count_extended_days2
        //Update include Command.EXTEND_TRIAL + finalAnimalOwnershipDelDog.getId() + count_extended_days2
        //Update include the same thing
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerCat.getUserNameTelegram(),
                        chatVolunteerCat.getFirstNameUser(),
                        chatVolunteerCat.getLastNameUser(),
                        chatVolunteerCat.getId(),
                        Command.EXTEND_TRIAL.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipDog1.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                AnimalOwnershipService.COUNT_EXTENDED_DAYS_1,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.EXTEND_TRIAL.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipDog1.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                AnimalOwnershipService.COUNT_EXTENDED_DAYS_1,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.EXTEND_TRIAL.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipDog2.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                AnimalOwnershipService.COUNT_EXTENDED_DAYS_2,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerDog.getUserNameTelegram(),
                        chatVolunteerDog.getFirstNameUser(),
                        chatVolunteerDog.getLastNameUser(),
                        chatVolunteerDog.getId(),
                        Command.EXTEND_TRIAL.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipDelDog.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                AnimalOwnershipService.COUNT_EXTENDED_DAYS_2,
                        false)
        ));

        LocalDate dateRemember1 = animalOwnershipDog1.getDateEndTrial();
        LocalDate dateRemember2 = animalOwnershipDog2.getDateEndTrial();

        telegramBotUpdatesListener.process(updateList);

        animalOwnershipDog1 = animalOwnershipRepository.findById(animalOwnershipDog1.getId()).orElse(null);
        animalOwnershipDog2 = animalOwnershipRepository.findById(animalOwnershipDog2.getId()).orElse(null);
        assertThat(animalOwnershipDog1.isApprove()).isNull();
        assertThat(animalOwnershipDog2.isApprove()).isNull();
        assertThat(animalOwnershipDog1.isOpen()).isTrue();
        assertThat(animalOwnershipDog2.isOpen()).isTrue();
        assertThat(animalOwnershipDog1.getDateEndTrial().minusDays(AnimalOwnershipService.COUNT_EXTENDED_DAYS_1).
                equals(dateRemember1)).isTrue();
        assertThat(animalOwnershipDog2.getDateEndTrial().minusDays(AnimalOwnershipService.COUNT_EXTENDED_DAYS_2).
                equals(dateRemember2)).isTrue();

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

        String validateStr = "AnimalOwnership\n" +
                "Owner: Client of shelter";
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        assertTrue(actual0.getParameters().get("text").toString().contains(validateStr));

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatClientDog1.getId());
        assertTrue(actual2.getParameters().get("text").toString().contains(validateStr));

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        assertTrue(actual3.getParameters().get("text").toString().contains(validateStr));

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatVolunteerDog.getId());
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(chatClientDog2.getId());
        assertTrue(actual5.getParameters().get("text").toString().contains(validateStr));
    }

    @Test
    public void CLOSE_UNFINISHED_REQUESTTest() {
        //get userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findAny().orElse(null);
        //get userClient
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findAny().orElse(null);
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        Chat chatClient = userClient.getChatTelegram();
        //set UNFINISHED_REQUEST
        UnfinishedRequestTelegram unfinishedRequestTelegramVolunteer = new UnfinishedRequestTelegram();
        unfinishedRequestTelegramVolunteer.setCommand(Command.CHANGE_PHONE.getTextCommand());
        unfinishedRequestTelegramVolunteer.setChat(chatVolunteer);
        unfinishedRequestTelegramRepository.save(unfinishedRequestTelegramVolunteer);
        UnfinishedRequestTelegram unfinishedRequestTelegramClient = new UnfinishedRequestTelegram();
        unfinishedRequestTelegramClient.setCommand(Command.CHANGE_PHONE.getTextCommand());
        unfinishedRequestTelegramClient.setChat(chatClient);
        unfinishedRequestTelegramRepository.save(unfinishedRequestTelegramClient);
        //Update include Command.CLOSE_UNFINISHED_REQUEST
        //Update include Command.CLOSE_UNFINISHED_REQUEST
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.CLOSE_UNFINISHED_REQUEST.getTextCommand(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatClient.getId(),
                        Command.CLOSE_UNFINISHED_REQUEST.getTextCommand(),
                        false)
        ));

        assertThat(unfinishedRequestTelegramRepository.findAll().size()).isEqualTo(2);
        telegramBotUpdatesListener.process(updateList);
        assertThat(unfinishedRequestTelegramRepository.findAll().size()).isEqualTo(0);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(2)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(2);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void INFOWithoutShelterTest() {
        //get userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findAny().orElse(null);
        //get userClient
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findAny().orElse(null);
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        chatVolunteer.setShelter(null);
        Chat chatClient = userClient.getChatTelegram();
        chatClient.setShelter(null);
        chatVolunteer = chatRepository.save(chatVolunteer);
        chatClient = chatRepository.save(chatClient);
        //Update include Command.INFO chatVolunteer
        //Update include Command.INFO chatClient
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.INFO.getTextCommand(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.INFO.getTextCommand(),
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(2)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(2);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_SHELTER);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_SHELTER);
    }

    @Test
    public void SET_SHELTERTest() {
        //get userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findAny().orElse(null);
        //get userClient
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findAny().orElse(null);
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        chatVolunteer.setShelter(null);
        Chat chatClient = userClient.getChatTelegram();
        chatClient.setShelter(null);
        chatVolunteer = chatRepository.save(chatVolunteer);
        chatClient = chatRepository.save(chatClient);
        //Update include Command.SET_SHELTER + DOG from chatVolunteer
        //Update include Command.SET_SHELTER + CAT from chatClient
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.SET_SHELTER.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                "DOG",
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.SET_SHELTER.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                "CAT",
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);

        chatVolunteer = chatRepository.findById(chatVolunteer.getId()).orElse(null);
        chatClient = chatRepository.findById(chatClient.getId()).orElse(null);
        assertThat(chatVolunteer.getShelter().getshelterDesignation().equals("DOG"));
        assertThat(chatClient.getShelter().getshelterDesignation().equals("CAT"));

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(2)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(2);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        //opposite
        updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.SET_SHELTER.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                "CAT",
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.SET_SHELTER.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                "DOG",
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);

        chatVolunteer = chatRepository.findById(chatVolunteer.getId()).orElse(null);
        chatClient = chatRepository.findById(chatClient.getId()).orElse(null);
        assertThat(chatVolunteer.getShelter().getshelterDesignation().equals("CAT"));
        assertThat(chatClient.getShelter().getshelterDesignation().equals("DOG"));

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void change_MENUTest() {
        List<Chat> chatClientList = userRepository.findAll().stream().
                filter(user -> !user.isVolunteer()).
                map(User::getChatTelegram).
                collect(Collectors.toList());
        int index = random.nextInt(chatClientList.size());
        Chat chat1 = chatClientList.get(index);
        chatClientList.remove(index);
        index = random.nextInt(chatClientList.size());
        Chat chat2 = chatClientList.get(index);
        chatClientList.remove(index);
        index = random.nextInt(chatClientList.size());
        Chat chat3 = chatClientList.get(index);
        chatClientList.remove(index);
        index = random.nextInt(chatClientList.size());
        Chat chat4 = chatClientList.get(index);
        chatClientList.remove(index);
        index = random.nextInt(chatClientList.size());
        Chat chat5 = chatClientList.get(index);
        chatClientList.remove(index);
        index = random.nextInt(chatClientList.size());
        Chat chat6 = chatClientList.get(index);
        //Update include Command.MENU_INFO chatVolunteer
        //Update include Command.MENU_INFO chatClient
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chat1.getUserNameTelegram(),
                        chat1.getFirstNameUser(),
                        chat1.getLastNameUser(),
                        chat1.getId(),
                        Command.MENU_INFO_SHELTER.getTextCommand(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chat2.getUserNameTelegram(),
                        chat2.getFirstNameUser(),
                        chat2.getLastNameUser(),
                        chat2.getId(),
                        Command.MENU_INFO_ANIMAL.getTextCommand(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chat3.getUserNameTelegram(),
                        chat3.getFirstNameUser(),
                        chat3.getLastNameUser(),
                        chat3.getId(),
                        Command.MENU_ACTION.getTextCommand(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chat4.getUserNameTelegram(),
                        chat4.getFirstNameUser(),
                        chat4.getLastNameUser(),
                        chat4.getId(),
                        Command.MENU_BACK1.getTextCommand(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chat5.getUserNameTelegram(),
                        chat5.getFirstNameUser(),
                        chat5.getLastNameUser(),
                        chat5.getId(),
                        Command.MENU_BACK2.getTextCommand(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chat6.getUserNameTelegram(),
                        chat6.getFirstNameUser(),
                        chat6.getLastNameUser(),
                        chat6.getId(),
                        Command.MENU_BACK3.getTextCommand(),
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);

        chat1 = chatRepository.findById(chat1.getId()).orElse(null);
        assertEquals(chat1.getIndexMenu(), 1);
        chat2 = chatRepository.findById(chat2.getId()).orElse(null);
        assertEquals(chat2.getIndexMenu(), 2);
        chat3 = chatRepository.findById(chat3.getId()).orElse(null);
        assertEquals(chat3.getIndexMenu(), 3);
        chat4 = chatRepository.findById(chat4.getId()).orElse(null);
        assertEquals(chat4.getIndexMenu(), 0);
        chat5 = chatRepository.findById(chat5.getId()).orElse(null);
        assertEquals(chat5.getIndexMenu(), 0);
        chat6 = chatRepository.findById(chat6.getId()).orElse(null);
        assertEquals(chat6.getIndexMenu(), 0);


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

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chat1.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chat2.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chat3.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chat4.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chat5.getId());
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(chat6.getId());
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }
}