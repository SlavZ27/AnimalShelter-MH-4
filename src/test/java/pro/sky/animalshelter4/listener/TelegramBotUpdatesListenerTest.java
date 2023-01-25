package pro.sky.animalshelter4.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
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
    }

    /**
     * testing an incoming message with {@link Command#START}
     */
    @Test
    public void STARTTest() {
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
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    /**
     * testing an incoming message and press button with {@link Command#INFO}
     */
    @Test
    public void INFOTest() {
        Long id = 50L;
        String command = Command.INFO.getTextCommand();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection("", "", "", id, command, true),
                generator.generateUpdateMessageWithReflection("", "", "", id, command + " 1", true),
                generator.generateUpdateCallbackQueryWithReflection("", "", "", id, command, true)));
        telegramBotUpdatesListener.process(updateList);
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
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoAboutShelter.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(InfoAboutShelter.getInfoEn());
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(InfoAboutShelter.getInfoEn());
        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    /**
     * testing an incoming message and press button with {@link Command#HOW}
     */
    @Test
    public void HOWTest() {
        Long id = 50L;
        String command = Command.HOW.getTextCommand();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateMessageWithReflection("", "", "", id, command, true),
                generator.generateUpdateCallbackQueryWithReflection("", "", "", id, command, true)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoTakeADog.getInfoEn());
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(InfoTakeADog.getInfoEn());
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    /**
     * testing an incoming message with unknown Command
     */
    @Test
    public void UNKNOWNCommandTest() {
        Long id = 50L;
        String command = "/fegfdhesfhdgmghrfdgg";
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
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SORRY_I_DONT_KNOW_COMMAND);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    /**
     * testing an incoming message with {@link Command#START}
     */
    @Test
    public void UnknownTextTest() {
        Long id = 50L;
        String command = "fegfdhesfhdgmghrfdgg";
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
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SORRY_I_KNOW_THIS);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
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
        //create chatClient
        Long id = 50L;
        Chat chatClient = generator.generateChat(id, "userName", "firstName", "lastName", null, false);
        chatClient = chatRepository.save(chatClient);
        //remember list IDs of Volunteers
        List<String> listIdChatVolunteerToString = userRepository.findAll().stream().
                filter(User::isVolunteer).
                map(user -> user.getChatTelegram().getId().toString()).
                collect(Collectors.toList());
        //Update with chatClient
        //same Update to check that a new call request will not be created
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection("userName", "firstName", "lastName", id, Command.CALL_REQUEST.getTextCommand(), false),
                generator.generateUpdateCallbackQueryWithReflection("userName", "firstName", "lastName", id, Command.CALL_REQUEST.getTextCommand(), false)));
        //remember count callRequests
        int countCallRequestRepository = callRequestRepository.findAll().size();
        //Volunteers is present
        telegramBotUpdatesListener.process(updateList);
        //check that only 1 has been added
        assertThat(callRequestRepository.findAll().size()).isEqualTo(countCallRequestRepository + 1);

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

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_SUCCESSFUL_CREATION);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SORRY_I_DONT_KNOW_YOUR_PHONE);

        assertTrue(listIdChatVolunteerToString.contains(actual2.getParameters().get("chat_id").toString()));
        Assertions.assertThat(actual2.getParameters().get("text").toString()
                        .substring(0, CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST.length()))
                .isEqualTo(CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST);

        assertTrue(listIdChatVolunteerToString.contains(actual3.getParameters().get("chat_id").toString()));
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_YOU_CAN_CLOSE_CALL_REQUEST);

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_SUCCESSFUL_CREATION);

        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SORRY_I_DONT_KNOW_YOUR_PHONE);

        assertTrue(listIdChatVolunteerToString.contains(actual6.getParameters().get("chat_id").toString()));
        Assertions.assertThat(actual6.getParameters().get("text").toString()
                        .substring(0, CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST.length()))
                .isEqualTo(CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST);

        assertTrue(listIdChatVolunteerToString.contains(actual7.getParameters().get("chat_id").toString()));
        Assertions.assertThat(actual7.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_YOU_CAN_CLOSE_CALL_REQUEST);

        //find new callRequest
        CallRequest callRequest = callRequestRepository.findAll().stream().filter(callRequest1 -> callRequest1.getClient().getChatTelegram().getId().equals(id)).findFirst().orElse(null);
        assertThat(callRequest).isNotNull();
        assertThat(callRequest.isOpen()).isTrue();
        //delete all the volunteers
        userRepository.findAll().stream().
                filter(User::isVolunteer).forEach(user -> {
                    user.setVolunteer(false);
                    userRepository.save(user);
                });

        //Volunteer is absent
        telegramBotUpdatesListener.process(updateList);
        //check that only 1 has been added
        assertThat(callRequestRepository.findAll().size()).isEqualTo(countCallRequestRepository + 1);

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(12)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(12);
        SendMessage actual8 = actualList.get(8);
        SendMessage actual9 = actualList.get(9);
        SendMessage actual10 = actualList.get(10);
        SendMessage actual11 = actualList.get(11);
        Assertions.assertThat(actual8.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual8.getParameters().get("text")).isEqualTo(UserService.MESSAGE_VOLUNTEERS_IS_ABSENT);
        Assertions.assertThat(actual9.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual9.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual10.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual10.getParameters().get("text")).isEqualTo(UserService.MESSAGE_VOLUNTEERS_IS_ABSENT);
        Assertions.assertThat(actual11.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual11.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    /**
     * A test that verifies the execution of a {@link Command#CALL_REQUEST} that is not intended for the volunteer
     */
    @Test
    public void permissionTest() {
        Long id1 = 50L;
        String firstName1 = generator.generateNameIfEmpty("");
        String lastName1 = generator.generateNameIfEmpty("");
        String userName1 = generator.generateNameIfEmpty("");
        String phone1 = generator.generatePhoneIfEmpty("3456787654");
        boolean isVolunteer1 = true;

        Long id2 = 51L;
        String userName2 = generator.generateNameIfEmpty("");
        String firstName2 = generator.generateNameIfEmpty("");
        String lastName2 = generator.generateNameIfEmpty("");
        String phone2 = generator.generatePhoneIfEmpty("4846732029");
        boolean isVolunteer2 = false;

        //Command.CALL_REQUEST is not available to the volunteer
        String command = Command.CALL_REQUEST.getTextCommand();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(userName1, firstName1, lastName1, id1, command, false),
                generator.generateUpdateCallbackQueryWithReflection(userName2, firstName2, lastName2, id2, command, false)));
        Chat chatVolunteer = generator.generateChat(id1, userName1, firstName1, lastName1, null, false);
        chatVolunteer = chatRepository.save(chatVolunteer);
        User userVolunteer = generator.generateUser(null, firstName1 + " " + lastName1, chatVolunteer, null, null, isVolunteer1, null, true);
        userVolunteer = userRepository.save(userVolunteer);
        Chat chatClient = generator.generateChat(id2, userName2, firstName2, lastName2, null, false);
        chatClient = chatRepository.save(chatClient);
        User userClient = generator.generateUser(null, firstName2 + " " + lastName2, chatClient, phone2, null, isVolunteer2, null, true);
        userClient = userRepository.save(userClient);

        telegramBotUpdatesListener.process(updateList);
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
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(id1);
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SORRY_I_KNOW_THIS);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(id1);
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(id2);
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_SUCCESSFUL_CREATION);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(id2);
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);


        Assertions.assertThat(actual4.getParameters().get("text").toString()
                        .substring(0, CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST.length()))
                .isEqualTo(CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST);

        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_YOU_CAN_CLOSE_CALL_REQUEST);
    }

    @Test
    public void CALL_CLIENTTest() {
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findFirst().orElse(null);
        assert userVolunteer != null;
        Chat chatVolunteer = userVolunteer.getChatTelegram();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.CALL_CLIENT.getTextCommand(), false)));

        telegramBotUpdatesListener.process(updateList);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(3)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(3);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(userVolunteer.getChatTelegram().getId());
        Assertions.assertThat(actual0.getParameters().get("text").toString()
                        .substring(0, CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST.length()))
                .isEqualTo(CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(userVolunteer.getChatTelegram().getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_YOU_CAN_CLOSE_CALL_REQUEST);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(userVolunteer.getChatTelegram().getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        //close all callRequest
        callRequestRepository.findAll().stream().
                filter(CallRequest::isOpen).forEach(callRequest -> {
                    callRequest.setOpen(false);
                    callRequestRepository.save(callRequest);
                });
        telegramBotUpdatesListener.process(updateList);

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(5)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(5);
        SendMessage actual3 = actualList.get(3);
        SendMessage actual4 = actualList.get(4);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(userVolunteer.getChatTelegram().getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_YOU_DONT_HAVE_CALL_REQUEST);
        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(userVolunteer.getChatTelegram().getId());
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void CLOSE_CALL_REQUESTTest() {
        //find open callRequest
        CallRequest callRequestOpen = callRequestRepository.findAll().stream().
                filter(CallRequest::isOpen).findFirst().orElse(null);
        assert callRequestOpen != null;
        assert callRequestOpen.getVolunteer() != null;
        assert callRequestOpen.getVolunteer().getChatTelegram() != null;
        //remember actual chat of userVolunteer of callRequest
        Chat chatVolunteerActual = callRequestOpen.getVolunteer().getChatTelegram();
        //find any chat of volunteer
        List<User> listUserVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).collect(Collectors.toList());
        User userAny = listUserVolunteer.get(random.nextInt(listUserVolunteer.size()));
        while (userAny.getChatTelegram().getId().equals(chatVolunteerActual.getId())) {
            userAny = listUserVolunteer.get(random.nextInt(listUserVolunteer.size()));
        }
        Chat chatAny = userAny.getChatTelegram();
        //find non-existent index callRequest
        Long nonExistIndex = (long) random.nextInt(callRequestRepository.findAll().size());
        while (callRequestRepository.findById(nonExistIndex).orElse(null) != null) {
            nonExistIndex = (long) random.nextInt(callRequestRepository.findAll().size());
        }
        //Updates include different chat id by volunteers and non-existent index callRequest
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatAny.getUserNameTelegram(),
                        chatAny.getFirstNameUser(),
                        chatAny.getLastNameUser(),
                        chatAny.getId(),
                        Command.CLOSE_CALL_REQUEST.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                callRequestOpen.getId(), false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerActual.getUserNameTelegram(),
                        chatVolunteerActual.getFirstNameUser(),
                        chatVolunteerActual.getLastNameUser(),
                        chatVolunteerActual.getId(),
                        Command.CLOSE_CALL_REQUEST.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                callRequestOpen.getId(), false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteerActual.getUserNameTelegram(),
                        chatVolunteerActual.getFirstNameUser(),
                        chatVolunteerActual.getLastNameUser(),
                        chatVolunteerActual.getId(),
                        Command.CLOSE_CALL_REQUEST.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                nonExistIndex, false)
        ));

        telegramBotUpdatesListener.process(updateList);

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

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatAny.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_YOU_CANT_CLOSE_CALL_REQUEST);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatAny.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteerActual.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_CALL_REQUEST_IS_CLOSE);
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteerActual.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatVolunteerActual.getId());
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_CALL_REQUEST_NOT_FOUND);
        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(chatVolunteerActual.getId());
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        CallRequest callRequest = callRequestRepository.findById(callRequestOpen.getId()).orElse(null);
        assertThat(callRequest).isNotNull();
        assertThat(callRequest.isOpen()).isFalse();
    }

    @Test
    public void CHANGE_PHONETest() {
        //find any userClient
        User userClient = userRepository.findAll().stream().filter(user ->
                !user.isVolunteer()).findFirst().orElse(null);
        //get chatClient
        Chat chatClient = userClient.getChatTelegram();
        //set unfinishedRequestTelegram Command.CHANGE_PHONE for chatClient
        UnfinishedRequestTelegram unfinishedRequestTelegram = new UnfinishedRequestTelegram();
        unfinishedRequestTelegram.setChat(chatClient);
        unfinishedRequestTelegram.setCommand(Command.CHANGE_PHONE.getTextCommand());
        unfinishedRequestTelegramRepository.save(unfinishedRequestTelegram);
        //Update include Command.CHANGE_PHONE + too short phone <5 symbols
        //Update include Command.CHANGE_PHONE + too large phone >15 symbols
        //Update include Command.CHANGE_PHONE + good phone
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
                                "89998887766", false)
        ));

        telegramBotUpdatesListener.process(updateList);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(UserService.MESSAGE_BAD_PHONE);
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(UserService.MESSAGE_BAD_PHONE);
        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(UserService.MESSAGE_PHONE_IS_OK);
        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void CREATE_OWNERSHIPTest() {
        //find userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findFirst().orElse(null);
        assertThat(userVolunteer).isNotNull();
        //find userClient
        User userClient = userRepository.findAll().stream().filter(user ->
                !user.isVolunteer()).findFirst().orElse(null);
        assertThat(userVolunteer).isNotNull();
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        //get chatClient
        Chat chatClient = userClient.getChatTelegram();
        //find free animal
        List<Long> animalOwnershipIdList = animalOwnershipRepository.findAll().stream().map(animalOwnership -> animalOwnership.getAnimal().getId()).collect(Collectors.toList());
        List<Animal> animalList = animalRepository.findAll();
        Animal animal = animalList.get(random.nextInt(animalList.size()));
        while (animalOwnershipIdList.contains(animal.getId())) {
            animal = animalList.get(random.nextInt(animalList.size()));
        }
        //Update include only Command.CREATE_OWNERSHIP
        //Update include Command.CREATE_OWNERSHIP + OwnershipUnit.USER + id of userOwner
        //Update include Command.CREATE_OWNERSHIP + OwnershipUnit.USER + id of userOwner + OwnershipUnit.ANIMAL + id of animal
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
                                animal.getId(), false)
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
        Mockito.verify(telegramBot, times(7)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(7);
        SendMessage actual5 = actualList.get(5);
        SendMessage actual6 = actualList.get(6);

        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(AnimalService.MESSAGE_ANIMALS_IS_ABSENT);

        Assertions.assertThat(actual6.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual6.getParameters().get("text")).isEqualTo(UserService.MESSAGE_CLIENTS_IS_ABSENT);
    }

    @Test
    public void ADD_ANIMALTest() {
        String nameAnimal = "Tuzik";
        //remember count of animal
        int countOfAnimal = animalRepository.findAll().size();
        //find userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findFirst().orElse(null);
        assertThat(userVolunteer).isNotNull();
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        //Update include only Command.ADD_ANIMAL
        //Update include Command.ADD_ANIMAL + name of animal
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.ADD_ANIMAL.getTextCommand(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.ADD_ANIMAL.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + nameAnimal,
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);
        assertThat(animalRepository.findAll().size()).isEqualTo(countOfAnimal + 1);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(AnimalService.CAPTION_WRITE_NAME_OF_ANIMAL);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(AnimalService.MESSAGE_ANIMAL_CREATED + " " + nameAnimal);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(AnimalService.CAPTION_SELECT_TYPE_OF_ANIMAL + " '" + nameAnimal + "'");

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }


    @Test
    public void COMPLEMENT_ANIMALTest() {
        //remember type of animal
        AnimalType animalType = animalTypeRepository.findAll().stream().findFirst().orElse(null);
        assertThat(animalType).isNotNull();
        //find animal with different animalType
        List<Animal> animalList = animalRepository.findAll();
        Animal animal = animalList.get(random.nextInt(animalList.size()));
        while (animal.getAnimalType().getId().equals(animalType.getId())) {
            animal = animalList.get(random.nextInt(animalList.size()));
        }
        assertThat(animal.getAnimalType().getId().equals(animalType.getId())).isFalse();
        //find userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findFirst().orElse(null);
        assertThat(userVolunteer).isNotNull();
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        //Update include only Command.COMPLEMENT_ANIMAL + AnimalUnit.ID + animal.getId() + AnimalUnit.ANIMAL_TYPE + animalType.getId()
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.COMPLEMENT_ANIMAL.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                animal.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                animalType.getId(),
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);

        animal = animalRepository.findById(animal.getId()).orElse(null);
        assertThat(animal).isNotNull();
        assertThat(animal.getAnimalType().getId().equals(animalType.getId())).isTrue();

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(3)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(3);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual0.getParameters().get("text").toString().contains(AnimalService.MESSAGE_ANIMAL_UPDATED)).isTrue();

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(AnimalService.MESSAGE_ALL_ANIMAL_COMPLEMENT);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }


    @Test
    public void REPORTTest() {
        //remember animalOwnership
        AnimalOwnership animalOwnership = animalOwnershipRepository.findAll().stream().findFirst().orElse(null);
        animalOwnership.setOpen(true);
        animalOwnership.setDateStartOwn(LocalDate.now().minusDays(15));
        animalOwnership.setDateEndTrial(LocalDate.now().plusDays(15));
        animalOwnership = animalOwnershipRepository.save(animalOwnership);
        assertThat(animalOwnership).isNotNull();
        Long idAnimalOwnership = animalOwnership.getId();
        //delete all report of animalOwnership
        reportRepository.findAll().stream().filter(report -> report.getAnimalOwnership().getId().equals(idAnimalOwnership))
                .forEach(report -> reportRepository.delete(report));
        //get userClient
        User userClient = animalOwnership.getOwner();
        //get chatClient
        Chat chatClient = userClient.getChatTelegram();
        //Update include only Command.REPORT
        //Update include only diet
        //Update include only feeling
        //Update include only photo
        //Update include only behavior
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.REPORT.getTextCommand(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        generator.generateMessageIfEmpty(null),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        generator.generateMessageIfEmpty(null),
                        false),
                generator.generateUpdateMessagePhotoWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        "",
                        "12345678543456787654",
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        generator.generateMessageIfEmpty(null),
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);

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

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(
                ReportService.MESSAGE_WRITE_DIET + " or " + ReportService.MESSAGE_SEND_PHOTO);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(
                ReportService.MESSAGE_WRITE_FEELING + " or " + ReportService.MESSAGE_SEND_PHOTO);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(
                ReportService.MESSAGE_WRITE_BEHAVIOR + " or " + ReportService.MESSAGE_SEND_PHOTO);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_WRITE_BEHAVIOR + " ");

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual4.getParameters().get("text").toString().contains(ReportService.MESSAGE_REPORT_CREATE)).isTrue();

        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }


    @Test
    public void VIEW_REPORTTest() {
        //get userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findFirst().orElse(null);
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        //Update include only Command.VIEW_REPORT
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.VIEW_REPORT.getTextCommand(),
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
        String validateStr = "Report\n" +
                "AnimalOwnership: AnimalOwnership\n" +
                "Owner: User\n" +
                "Named:";
        assertTrue(actual0.getParameters().get("text").toString().contains(validateStr));

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_APPROVE_OR_NOT);

        //close all report
        reportRepository.findAll().stream().filter(report -> report.isApprove() == null).forEach(report -> {
            report.setApprove(true);
            reportRepository.save(report);
        });
        telegramBotUpdatesListener.process(updateList);

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_ALL_REPORT_ARE_APPROVE);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }


    @Test
    public void APPROVE_REPORTTest() {
        //get userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findFirst().orElse(null);
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        //get open report (approve == null)
        List<Report> reportList = reportRepository.findAll().stream().filter(report1 -> report1.isApprove() == null).collect(Collectors.toList());
        int index = random.nextInt(reportList.size());
        Report openReportGood = reportList.get(index);
        reportList.remove(index);
        index = random.nextInt(reportList.size());
        Report openReportBad = reportList.get(index);
        reportList.remove(index);
        index = random.nextInt(reportList.size());
        Report deletedReport = reportList.get(index);
        reportRepository.delete(deletedReport);


        //Update include Command.APPROVE_REPORT + report.getId() + true
        //Update include Command.APPROVE_REPORT + report.getId() + false
        //Update include Command.APPROVE_REPORT + non-exist report.getId() + true
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.APPROVE_REPORT.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + openReportGood.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.APPROVE_REPORT.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + openReportBad.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + false,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.APPROVE_REPORT.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + deletedReport.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true,
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);

        openReportGood = reportRepository.findById(openReportGood.getId()).orElse(null);
        openReportBad = reportRepository.findById(openReportBad.getId()).orElse(null);
        assertThat(openReportGood).isNotNull();
        assertThat(openReportBad).isNotNull();
        assertThat(openReportGood.isApprove()).isTrue();
        assertThat(openReportBad.isApprove()).isFalse();

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(4)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(4);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);
        SendMessage actual3 = actualList.get(3);


        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_REPORT_IS_PLACED_GOOD);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).
                isEqualTo(openReportGood.getAnimalOwnership().getOwner().getChatTelegram().getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_REPORT_IS_PLACED_GOOD);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_REPORT_IS_PLACED_BAD);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).
                isEqualTo(openReportBad.getAnimalOwnership().getOwner().getChatTelegram().getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_REPORT_IS_PLACED_BAD_OWNER);
    }

    @Test
    public void VIEW_OWNERSHIPTest() {
        //remember actual animalOwnership
        AnimalOwnership animalOwnership = animalOwnershipRepository.findAll().stream().
                filter(animalOwnership1 -> animalOwnership1.isApprove() == null).findFirst().orElse(null);
        animalOwnership.setOpen(true);
        animalOwnership.setDateStartOwn(LocalDate.now().minusDays(31));
        animalOwnership.setDateEndTrial(LocalDate.now().minusDays(1));
        animalOwnership = animalOwnershipRepository.save(animalOwnership);
        assertThat(animalOwnership).isNotNull();
        //get userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findFirst().orElse(null);
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        //Update include Command.VIEW_OWNERSHIP
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.VIEW_OWNERSHIP.getTextCommand(),
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
        String validateStr = "AnimalOwnership\n" +
                "Owner: User\n" +
                "Named:";
        assertTrue(actual0.getParameters().get("text").toString().contains(validateStr));

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(ReportService.MESSAGE_APPROVE_OR_NOT);
    }

    @Test
    public void APPROVE_OWNERSHIPTest() {
        //remember list animalOwnership with isApprove() == null
        List<AnimalOwnership> animalOwnershipList = animalOwnershipRepository.findAll().stream().
                filter(animalOwnership1 -> animalOwnership1.isApprove() == null).collect(Collectors.toList());
        assertThat(animalOwnershipList.size() > 3).isTrue();
        //remember not-exist animalOwnership
        int index = random.nextInt(animalOwnershipList.size());
        AnimalOwnership animalOwnershipDeleted = animalOwnershipList.get(index);
        animalOwnershipList.remove(index);
        reportRepository.findAll().stream().filter(report -> report.getAnimalOwnership().getId().equals(animalOwnershipDeleted.getId())).
                forEach(report -> reportRepository.delete(report));
        animalOwnershipRepository.delete(animalOwnershipDeleted);
        //remember actual animalOwnershipGood
        index = random.nextInt(animalOwnershipList.size());
        AnimalOwnership animalOwnershipGood = animalOwnershipList.get(index);
        animalOwnershipList.remove(index);
        assertThat(animalOwnershipGood).isNotNull();
        animalOwnershipGood.setOpen(true);
        animalOwnershipGood.setDateStartOwn(LocalDate.now().minusDays(31));
        animalOwnershipGood.setDateEndTrial(LocalDate.now().minusDays(1));
        animalOwnershipGood = animalOwnershipRepository.save(animalOwnershipGood);
        //remember actual animalOwnershipBad
        index = random.nextInt(animalOwnershipList.size());
        AnimalOwnership animalOwnershipBad = animalOwnershipList.get(index);
        animalOwnershipList.remove(index);
        assertThat(animalOwnershipBad).isNotNull();
        animalOwnershipBad.setOpen(true);
        animalOwnershipBad.setDateStartOwn(LocalDate.now().minusDays(31));
        animalOwnershipBad.setDateEndTrial(LocalDate.now().minusDays(1));
        animalOwnershipBad = animalOwnershipRepository.save(animalOwnershipBad);
        //get userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findFirst().orElse(null);
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        //Update include Command.VIEW_OWNERSHIP + animalOwnershipGood.getId()
        //Update include the same thing
        //Update include Command.VIEW_OWNERSHIP + animalOwnershipBad.getId()
        //Update include Command.VIEW_OWNERSHIP + not-exist animalOwnership.getId()
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.APPROVE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipGood.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.APPROVE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipGood.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.APPROVE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipBad.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + false,
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.APPROVE_OWNERSHIP.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipDeleted.getId() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + false,
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);

        animalOwnershipGood = animalOwnershipRepository.findById(animalOwnershipGood.getId()).orElse(null);
        animalOwnershipBad = animalOwnershipRepository.findById(animalOwnershipBad.getId()).orElse(null);
        assertThat(animalOwnershipGood).isNotNull();
        assertThat(animalOwnershipBad).isNotNull();
        assertThat(animalOwnershipGood.isApprove()).isTrue();
        assertThat(animalOwnershipBad.isApprove()).isFalse();
        assertThat(animalOwnershipGood.isOpen()).isFalse();
        assertThat(animalOwnershipBad.isOpen()).isFalse();

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

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_GOOD);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(animalOwnershipGood.getOwner().getChatTelegram().getId());
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_GOOD);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(AnimalOwnershipService.MESSAGE_ALREADY_CLOSE);

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_BAD);

        Assertions.assertThat(actual6.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual6.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual7.getParameters().get("chat_id")).isEqualTo(animalOwnershipBad.getOwner().getChatTelegram().getId());
        Assertions.assertThat(actual7.getParameters().get("text")).isEqualTo(AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_BAD_OWNER);
    }


    @Test
    public void EXTEND_TRIALTest() {
        //remember list animalOwnership with isApprove() == null
        List<AnimalOwnership> animalOwnershipList = animalOwnershipRepository.findAll().stream().
                filter(animalOwnership1 -> animalOwnership1.isApprove() == null).collect(Collectors.toList());
        assertThat(animalOwnershipList.size() > 2).isTrue();
        //remember not-exist animalOwnership
        int index = random.nextInt(animalOwnershipList.size());
        AnimalOwnership animalOwnershipDeleted = animalOwnershipList.get(index);
        animalOwnershipList.remove(index);
        reportRepository.findAll().stream().filter(report -> report.getAnimalOwnership().getId().equals(animalOwnershipDeleted.getId())).
                forEach(report -> reportRepository.delete(report));
        animalOwnershipRepository.delete(animalOwnershipDeleted);
        //remember actual animalOwnership
        index = random.nextInt(animalOwnershipList.size());
        AnimalOwnership animalOwnership = animalOwnershipList.get(index);
        animalOwnershipList.remove(index);
        assertThat(animalOwnership).isNotNull();
        animalOwnership.setOpen(true);
        animalOwnership.setDateStartOwn(LocalDate.now().minusDays(31));
        LocalDate dateRemember = LocalDate.now().minusDays(1);
        animalOwnership.setDateEndTrial(dateRemember);
        animalOwnership = animalOwnershipRepository.save(animalOwnership);
        //get userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findFirst().orElse(null);
        //get chatVolunteer
        Chat chatVolunteer = userVolunteer.getChatTelegram();
        //Update include Command.VIEW_OWNERSHIP + animalOwnership.getId()
        //Update include the same thing
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.EXTEND_TRIAL.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnership.getId(),
                        false),
                generator.generateUpdateCallbackQueryWithReflection(
                        chatVolunteer.getUserNameTelegram(),
                        chatVolunteer.getFirstNameUser(),
                        chatVolunteer.getLastNameUser(),
                        chatVolunteer.getId(),
                        Command.EXTEND_TRIAL.getTextCommand() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animalOwnershipDeleted.getId(),
                        false)
        ));

        telegramBotUpdatesListener.process(updateList);

        animalOwnership = animalOwnershipRepository.findById(animalOwnership.getId()).orElse(null);
        assertThat(animalOwnership).isNotNull();
        assertThat(animalOwnership.isApprove()).isNull();
        assertThat(animalOwnership.isOpen()).isTrue();
        assertThat(animalOwnership.getDateEndTrial().minusDays(AnimalOwnershipService.count_extended_days).
                equals(dateRemember)).isTrue();

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(3)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(3);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        SendMessage actual2 = actualList.get(2);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        String validateStr = "AnimalOwnership\n" +
                "Owner: User\n" +
                "Named:";
        assertTrue(actual0.getParameters().get("text").toString().contains(validateStr));

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(chatVolunteer.getId());
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(animalOwnership.getOwner().getChatTelegram().getId());
        assertTrue(actual2.getParameters().get("text").toString().contains(validateStr));
    }

    @Test
    public void CLOSE_UNFINISHED_REQUESTTest() {
        //get userVolunteer
        User userVolunteer = userRepository.findAll().stream().
                filter(User::isVolunteer).findFirst().orElse(null);
        //get userClient
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findFirst().orElse(null);
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
    public void INFO_DOGS_DISABILITIESTest() {
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findFirst().orElse(null);
        Chat chatClient = userClient.getChatTelegram();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.INFO_DISABILITIES.getTextCommand(),
                        false)
        ));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(1)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(1);
        SendMessage actual0 = actualList.get(0);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoDogsWithDisabilities.getInfoEn());
    }

    @Test
    public void INFO_LIST_DOCUMENTSTest() {
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findFirst().orElse(null);
        Chat chatClient = userClient.getChatTelegram();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.INFO_LIST_DOCUMENTS.getTextCommand(),
                        false)
        ));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(1)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(1);
        SendMessage actual0 = actualList.get(0);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoListOfDocuments.getInfoEn());
    }

    @Test
    public void INFO_RECOMMEND_HOME_DOGTest() {
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findFirst().orElse(null);
        Chat chatClient = userClient.getChatTelegram();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.INFO_RECOMMEND_HOME_ANIMAL.getTextCommand(),
                        false)
        ));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(1)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(1);
        SendMessage actual0 = actualList.get(0);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoRecommendationsHomeDog.getInfoEn());
    }

    @Test
    public void INFO_RECOMMEND_HOME_DOG_SMALLTest() {
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findFirst().orElse(null);
        Chat chatClient = userClient.getChatTelegram();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.INFO_RECOMMEND_HOME_ANIMAL_SMALL.getTextCommand(),
                        false)
        ));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(1)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(1);
        SendMessage actual0 = actualList.get(0);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoRecommendationsHomeSmallDog.getInfoEn());
    }

    @Test
    public void INFO_REFUSETest() {
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findFirst().orElse(null);
        Chat chatClient = userClient.getChatTelegram();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.INFO_REFUSE.getTextCommand(),
                        false)
        ));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(1)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(1);
        SendMessage actual0 = actualList.get(0);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoRefuseDogFromShelter.getInfoEn());
    }

    @Test
    public void INFO_TIPSTest() {
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findFirst().orElse(null);
        Chat chatClient = userClient.getChatTelegram();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.INFO_TIPS.getTextCommand(),
                        false)
        ));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(1)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(1);
        SendMessage actual0 = actualList.get(0);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoTipsFromDogHandler.getInfoEn());
    }

    @Test
    public void INFO_TRANSPORTATIONTest() {
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findFirst().orElse(null);
        Chat chatClient = userClient.getChatTelegram();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.INFO_TRANSPORTATION.getTextCommand(),
                        false)
        ));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(1)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(1);
        SendMessage actual0 = actualList.get(0);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoTransportationAnimals.getInfoEn());
    }

    @Test
    public void INFO_NEED_HANDLERTest() {
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findFirst().orElse(null);
        Chat chatClient = userClient.getChatTelegram();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.INFO_NEED_HANDLER.getTextCommand(),
                        false)
        ));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(1)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(1);
        SendMessage actual0 = actualList.get(0);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoWhyDoYouNeedDogHandler.getInfoEn());
    }

    @Test
    public void INFO_GET_DOGTest() {
        User userClient = userRepository.findAll().stream().
                filter(user -> user.isVolunteer() == false).findFirst().orElse(null);
        Chat chatClient = userClient.getChatTelegram();
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(
                        chatClient.getUserNameTelegram(),
                        chatClient.getFirstNameUser(),
                        chatClient.getLastNameUser(),
                        chatClient.getId(),
                        Command.INFO_GET_ANIMAL.getTextCommand(),
                        false)
        ));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(1)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(1);
        SendMessage actual0 = actualList.get(0);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(chatClient.getId());
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(InfoGettingKnowDog.getInfoEn());
    }
}