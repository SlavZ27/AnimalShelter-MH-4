package pro.sky.animalshelter4.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
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
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.info.InfoAboutShelter;
import pro.sky.animalshelter4.info.InfoTakeADog;
import pro.sky.animalshelter4.repository.*;
import pro.sky.animalshelter4.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
        int userClientInt = 70;
        int chatInt = userClientInt + userVolunteerInt;
        int callRequestInt = 20;                       // callRequest < chat
        int animalInt = 30;
        int animalOwnershipInt = 10;                   // animal > animalOwnership
        int animalTypeInt = 6;
        int reportInt = 10;
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
        Long id = 50L;
        Chat chatClient = generator.generateChat(id, "userName", "firstName", "lastName", null, false);
        chatClient = chatRepository.save(chatClient);
        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection("userName", "firstName", "lastName", id, Command.CALL_REQUEST.getTextCommand(), false),
                generator.generateUpdateCallbackQueryWithReflection("userName", "firstName", "lastName", id, Command.CALL_REQUEST.getTextCommand(), false)));
        List<String> listIdChatVolunteerToString = userRepository.findAll().stream().
                filter(User::isVolunteer).
                map(user -> user.getChatTelegram().getId().toString()).
                collect(Collectors.toList());
        int countCallRequestRepository = callRequestRepository.findAll().size();
        //Volunteers is present
        telegramBotUpdatesListener.process(updateList);
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

        //delete all the volunteers
        userRepository.findAll().stream().
                filter(User::isVolunteer).forEach(user -> {
                    user.setVolunteer(false);
                    userRepository.save(user);
                });

        countCallRequestRepository = callRequestRepository.findAll().size();
        //Volunteer is absent
        telegramBotUpdatesListener.process(updateList);
        assertThat(callRequestRepository.findAll().size()).isEqualTo(countCallRequestRepository);

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
    }

}