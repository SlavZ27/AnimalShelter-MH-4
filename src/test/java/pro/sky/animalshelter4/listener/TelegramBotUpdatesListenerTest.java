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
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.info.InfoAboutShelter;
import pro.sky.animalshelter4.info.InfoTakeADog;
import pro.sky.animalshelter4.repository.CallRequestRepository;
import pro.sky.animalshelter4.repository.ChatRepository;
import pro.sky.animalshelter4.repository.UserRepository;
import pro.sky.animalshelter4.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    private ChatRepository chatRepository;
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CallRequestRepository callRequestRepository;
    @Autowired
    private CallRequestService callRequestService;
    @Autowired
    private TelegramMapperService telegramMapperService;
    @Autowired
    private TelegramBotSenderService telegramBotSenderService;
    private TelegramBotContentSaverService telegramBotContentSaverService = new TelegramBotContentSaverService("./materials", telegramBotSenderService, telegramBot);
    @Autowired
    private TelegramBotUpdatesService telegramBotUpdatesService;
    @Autowired
    @InjectMocks
    private TelegramBotUpdatesListener telegramBotUpdatesListener;
    private final Generator generator = new Generator();


    /**
     * 1 volunteer and 10 clients are generated and a call request is created for them
     */
    @BeforeEach
    public void generateData() {
        callRequestRepository.deleteAll();
        userRepository.deleteAll();
        chatRepository.deleteAll();

        for (int i = 0; i < 2; i++) {
            Chat chatVolunteer = generator.generateChat(-1L, "", "", "", null, true);
            chatVolunteer = chatRepository.save(chatVolunteer);
            User userVolunteer = generator.generateUser(null, null, chatVolunteer, null, null, true, true);
            userVolunteer = userRepository.save(userVolunteer);
            for (int j = 0; j < 10; j++) {
                Chat chatClient = generator.generateChat(-1L, "", "", "", null, true);
                chatClient = chatRepository.save(chatClient);
                User userClient = generator.generateUser(null, null, chatClient, null, null, false, true);
                userClient = userRepository.save(userClient);
                CallRequest callRequest = new CallRequest();
                callRequest.setVolunteer(userVolunteer);
                callRequest.setClient(userClient);
                callRequest.setOpen(generator.generateBool());
                callRequest.setLocalDateTimeClose(generator.generateDateTime(true, LocalDateTime.now()));
                callRequest.setLocalDateTimeOpen(generator.generateDateTime(true, callRequest.getLocalDateTimeClose()));
                callRequestRepository.save(callRequest);
            }
        }
    }

    @AfterEach
    public void clearData() {
        callRequestRepository.deleteAll();
        userRepository.deleteAll();
        chatRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertThat(telegramBot).isNotNull();
        assertThat(chatRepository).isNotNull();
        assertThat(chatService).isNotNull();
        assertThat(callRequestRepository).isNotNull();
        assertThat(callRequestService).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(telegramMapperService).isNotNull();
        assertThat(telegramBotSenderService).isNotNull();
        assertThat(telegramBotContentSaverService).isNotNull();
        assertThat(telegramBotUpdatesService).isNotNull();
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
        Long id1 = 50L;
        String firstName1 = generator.generateNameIfEmpty("");
        String lastName1 = generator.generateNameIfEmpty("");
        String userName1 = generator.generateNameIfEmpty("");
        String phone1 = generator.generatePhoneIfEmpty("3456787654");
        boolean isVolunteer1 = false;

        Long id2 = 51L;
        String userName2 = generator.generateNameIfEmpty("");
        String firstName2 = generator.generateNameIfEmpty("");
        String lastName2 = generator.generateNameIfEmpty("");
        String phone2 = generator.generatePhoneIfEmpty("484.673.2029");
        boolean isVolunteer2 = false;

        String command = Command.CALL_REQUEST.getTextCommand();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(userName1, firstName1, lastName1, id1, command, false),
                generator.generateUpdateCallbackQueryWithReflection(userName2, firstName2, lastName2, id2, command, false)));

        Chat chatExist = generator.generateChat(id1, userName1, firstName1, lastName1, null, false);
        chatExist = chatRepository.save(chatExist);
        User userExist = generator.generateUser(null, firstName1 + " " + lastName1, chatExist, phone1, null, isVolunteer1, true);
        userExist = userRepository.save(userExist);

        Chat chatVolunteer = userService.getRandomVolunteer().getChatTelegram();

        int countCallRequestRepository = callRequestRepository.findAll().size();
        //Volunteer is present
        telegramBotUpdatesListener.process(updateList);
        assertThat(callRequestRepository.findAll().size()).isEqualTo(countCallRequestRepository + 2);

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
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_OK_VOLUNTEERS_FOUND);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(id1);
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual3.getParameters().get("chat_id")).isEqualTo(id2);
        Assertions.assertThat(actual3.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_OK_VOLUNTEERS_FOUND);

        Assertions.assertThat(actual5.getParameters().get("chat_id")).isEqualTo(id2);
        Assertions.assertThat(actual5.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(userName2, firstName2, lastName2, id2, command, false)));

        callRequestRepository.deleteAll();
        userRepository.deleteAll();
        chatRepository.deleteAll();

        countCallRequestRepository = callRequestRepository.findAll().size();
        //Volunteer is absent
        telegramBotUpdatesListener.process(updateList);
        assertThat(callRequestRepository.findAll().size()).isEqualTo(countCallRequestRepository);

        argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(8)).execute(argumentCaptor.capture());
        actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(8);
        actual0 = actualList.get(6);
        actual1 = actualList.get(7);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(id2);
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_VOLUNTEERS_IS_ABSENT);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(id2);
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
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
        String phone2 = generator.generatePhoneIfEmpty("484.673.2029");
        boolean isVolunteer2 = false;

        //Command.CALL_REQUEST is not available to the volunteer
        String command = Command.CALL_REQUEST.getTextCommand();

        List<Update> updateList = new ArrayList<>(List.of(
                generator.generateUpdateCallbackQueryWithReflection(userName1, firstName1, lastName1, id1, command, false),
                generator.generateUpdateCallbackQueryWithReflection(userName2, firstName2, lastName2, id2, command, false)));
        Chat chatVolunteer = generator.generateChat(id1, userName1, firstName1, lastName1, null, false);
        chatVolunteer = chatRepository.save(chatVolunteer);
        User userVolunteer = generator.generateUser(null, firstName1 + " " + lastName1, chatVolunteer, null, null, isVolunteer1, true);
        userVolunteer = userRepository.save(userVolunteer);
        Chat chatClient = generator.generateChat(id2, userName2, firstName2, lastName2, null, false);
        chatClient = chatRepository.save(chatClient);
        User userClient = generator.generateUser(null, firstName2 + " " + lastName2, chatClient, phone2, null, isVolunteer2, true);
        userClient = userRepository.save(userClient);

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
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(id1);
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SORRY_I_KNOW_THIS);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(id1);
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);

        Assertions.assertThat(actual2.getParameters().get("chat_id")).isEqualTo(id2);
        Assertions.assertThat(actual2.getParameters().get("text")).isEqualTo(CallRequestService.MESSAGE_OK_VOLUNTEERS_FOUND);

        Assertions.assertThat(actual4.getParameters().get("chat_id")).isEqualTo(id2);
        Assertions.assertThat(actual4.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

}