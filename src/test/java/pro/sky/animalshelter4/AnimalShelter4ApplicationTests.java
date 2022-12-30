package pro.sky.animalshelter4;

import com.github.javafaker.Faker;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pro.sky.animalshelter4.component.Command;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.City;
import pro.sky.animalshelter4.listener.TelegramBotUpdatesListener;
import pro.sky.animalshelter4.service.ParserService;
import pro.sky.animalshelter4.service.TelegramBotSenderService;
import pro.sky.animalshelter4.service.TelegramBotUpdatesService;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Profile("test")
@ExtendWith(SpringExtension.class)
class AnimalShelter4ApplicationTests {

    @MockBean
    @Autowired
    private TelegramBot telegramBot;
    @SpyBean
    @Autowired
    private TelegramBotUpdatesService telegramBotUpdatesService;
    @MockBean
    @Autowired
    private TelegramBotSenderService telegramBotSenderService;
    @SpyBean
    @Autowired
    private ParserService parserService;
    @InjectMocks
    @Autowired
    private TelegramBotUpdatesListener telegramBotUpdatesListener;
    private final Faker faker = new Faker();

    @AfterEach
    public void afterEach() {
    }

    @BeforeEach
    public void beforeAll() {

    }

    @Test
    void contextLoads() {
        assertThat(telegramBot).isNotNull();
        assertThat(telegramBotUpdatesListener).isNotNull();
        assertThat(telegramBotUpdatesService).isNotNull();
        assertThat(telegramBotSenderService).isNotNull();
        assertThat(parserService).isNotNull();
    }

    @Test
    public void receivingMessageCommandINFO() {
        List<Update> updateListWithCommandINFO = List.of(generateUpdateCallbackQueryWithReflection(
                "",
                "",
                "",
                50L,
                Command.INFO.getTitle()
        ));

        telegramBotUpdatesListener.process(updateListWithCommandINFO);

        verify(telegramBotSenderService, times(1)).sendInfoAboutShelter(updateListWithCommandINFO.get(0));
        verify(telegramBotSenderService, times(1)).sendButtonsCommandForChat(updateListWithCommandINFO.get(0));
    }

    @Test
    public void receivingCallbackQueryCommandINFO() {
        List<Update> updateListWithCommandINFO = List.of(generateUpdateMessageWithReflection(
                "",
                "",
                "",
                50L,
                Command.INFO.getTitle()
        ));

        telegramBotUpdatesListener.process(updateListWithCommandINFO);

        verify(telegramBotSenderService, times(1)).sendInfoAboutShelter(updateListWithCommandINFO.get(0));
        verify(telegramBotSenderService, times(1)).sendButtonsCommandForChat(updateListWithCommandINFO.get(0));
    }
    @Test
    public void receivingMessageCommandHOW() {
        List<Update> updateListWithCommandHOW = List.of(generateUpdateCallbackQueryWithReflection(
                "",
                "",
                "",
                50L,
                Command.HOW.getTitle()
        ));

        telegramBotUpdatesListener.process(updateListWithCommandHOW);

        verify(telegramBotSenderService, times(1)).sendHowTakeDog(updateListWithCommandHOW.get(0));
        verify(telegramBotSenderService, times(1)).sendButtonsCommandForChat(updateListWithCommandHOW.get(0));
    }

    @Test
    public void receivingCallbackQueryCommandHOW() {
        List<Update> updateListWithCommandHOW = List.of(generateUpdateMessageWithReflection(
                "",
                "",
                "",
                50L,
                Command.HOW.getTitle()
        ));

        telegramBotUpdatesListener.process(updateListWithCommandHOW);

        verify(telegramBotSenderService, times(1)).sendHowTakeDog(updateListWithCommandHOW.get(0));
        verify(telegramBotSenderService, times(1)).sendButtonsCommandForChat(updateListWithCommandHOW.get(0));
    }


    private Chat mapUpdateToChat(Update update) {
        Chat chat = new Chat();
        if (update.message() != null) {
            chat.setId(update.message().chat().id());
            chat.setName(generateNameIfEmpty(update.message().chat().firstName()));
        } else if (update.callbackQuery() != null) {
            chat.setId(update.callbackQuery().from().id());
            chat.setName(generateNameIfEmpty(update.callbackQuery().from().firstName()));
        }
        chat.setVolunteer(false);
        chat.setPhone(generatePhoneIfEmpty(""));
        City city = new City();
        city.setCityName(generateCityIfEmpty(""));
        city.setApproved(true);
        city.setTimeZone(generateTimeZoneIfNull(null));
        city.setId(generateIdIfEmpty(null));
        chat.setCity(city);
        return chat;
    }

    private Update generateUpdateCallbackQueryWithReflection(String username,
                                                             String firstName,
                                                             String lastName,
                                                             Long chatId,
                                                             String callbackQueryData) {
        username = generateNameIfEmpty(username);
        firstName = generateNameIfEmpty(firstName);
        lastName = generateNameIfEmpty(lastName);
        chatId = generateIdIfEmpty(chatId);
        callbackQueryData = generateMessageIfEmpty(callbackQueryData);

        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        User user = new User(0L);

        try {
            Field userNameField = user.getClass().getDeclaredField("username");
            userNameField.setAccessible(true);
            Field firstNameField = user.getClass().getDeclaredField("first_name");
            firstNameField.setAccessible(true);
            Field lastNameField = user.getClass().getDeclaredField("last_name");
            lastNameField.setAccessible(true);
            Field userId = user.getClass().getDeclaredField("id");
            userId.setAccessible(true);
            userNameField.set(user, username);
            firstNameField.set(user, firstName);
            lastNameField.set(user, lastName);
            userId.set(user, chatId);

            Field callbackUserField = callbackQuery.getClass().getDeclaredField("from");
            callbackUserField.setAccessible(true);
            Field callbackDataField = callbackQuery.getClass().getDeclaredField("data");
            callbackDataField.setAccessible(true);
            callbackUserField.set(callbackQuery, user);
            callbackDataField.set(callbackQuery, callbackQueryData);

            Field updateCallbackField = update.getClass().getDeclaredField("callback_query");
            updateCallbackField.setAccessible(true);
            updateCallbackField.set(update, callbackQuery);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return update;
    }

    private Update generateUpdateMessageWithReflection() {
        return generateUpdateMessageWithReflection("", "", "", -1L, "");
    }

    private Update generateUpdateMessageWithReflection(String username,
                                                       String firstName,
                                                       String lastName,
                                                       Long chatId,
                                                       String messageText) {
        username = generateNameIfEmpty(username);
        firstName = generateNameIfEmpty(firstName);
        lastName = generateNameIfEmpty(lastName);
        messageText = generateMessageIfEmpty(messageText);
        chatId = generateIdIfEmpty(chatId);

        Update update = new Update();
        Message message = new Message();
        com.pengrad.telegrambot.model.Chat chat = new com.pengrad.telegrambot.model.Chat();
        User user = new User(0L);

        try {
            Field userNameField = user.getClass().getDeclaredField("username");
            userNameField.setAccessible(true);
            Field firstNameField = user.getClass().getDeclaredField("first_name");
            firstNameField.setAccessible(true);
            Field lastNameField = user.getClass().getDeclaredField("last_name");
            lastNameField.setAccessible(true);
            userNameField.set(user, username);
            firstNameField.set(user, firstName);
            lastNameField.set(user, lastName);

            Field chatIdField = chat.getClass().getDeclaredField("id");
            chatIdField.setAccessible(true);
            chatIdField.set(chat, chatId);

            Field messageTextField = message.getClass().getDeclaredField("text");
            messageTextField.setAccessible(true);
            Field messageChatField = message.getClass().getDeclaredField("chat");
            messageChatField.setAccessible(true);
            Field messageUserField = message.getClass().getDeclaredField("from");
            messageUserField.setAccessible(true);
            messageTextField.set(message, messageText);
            messageChatField.set(message, chat);
            messageUserField.set(message, user);

            Field updateMessageField = update.getClass().getDeclaredField("message");
            updateMessageField.setAccessible(true);
            updateMessageField.set(update, message);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return update;
    }

    private int generateTimeZoneIfNull(Integer timeZone) {
        if (timeZone == null || timeZone < 0) {
            timeZone = faker.random().nextInt(-11, 12);
        }
        return timeZone;
    }

    private String generateAddressIfEmpty(String address) {
        if (address == null || address.length() == 0) {
            return faker.address().streetAddress();
        }
        return address;
    }

    private String generateCityIfEmpty(String city) {
        if (city == null || city.length() == 0) {
            return faker.address().city();
        }
        return city;
    }

    private String generatePhoneIfEmpty(String phone) {
        if (phone == null || phone.length() == 0) {
            return faker.phoneNumber().phoneNumber();
        }
        return phone;
    }

    private String generateNameIfEmpty(String name) {
        if (name == null || name.length() == 0) {
            return faker.name().username();
        }
        return name;
    }

    private Long generateIdIfEmpty(Long id) {
        if (id == null || id < 0) {
            long idTemp = -1L;
            //id with <100 I leave for my needs
            while (idTemp < 100) {
                idTemp = faker.random().nextLong();
            }
            return idTemp;
        }
        return id;
    }

    private String generateMessageIfEmpty(String message) {
        if (message == null || message.length() == 0) {
            return faker.lordOfTheRings().character();
        }
        return message;
    }

}
