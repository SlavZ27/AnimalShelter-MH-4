package pro.sky.animalshelter4.listener;


import com.github.javafaker.Faker;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import pro.sky.animalshelter4.component.Command;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.City;
import pro.sky.animalshelter4.info.InfoAboutShelter;
import pro.sky.animalshelter4.info.InfoTakeADog;
import pro.sky.animalshelter4.service.ParserService;
import pro.sky.animalshelter4.service.TelegramBotContentSaver;
import pro.sky.animalshelter4.service.TelegramBotSenderService;
import pro.sky.animalshelter4.service.TelegramBotUpdatesService;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;


@Profile("test")
@ExtendWith(MockitoExtension.class)
class TelegramBotUpdatesListenerTest {
    private final TelegramBot telegramBot = mock(TelegramBot.class);
    private final ParserService parserService = new ParserService();
    private final TelegramBotSenderService telegramBotSenderService = new TelegramBotSenderService(telegramBot);
    private final TelegramBotContentSaver telegramBotContentSaver = new TelegramBotContentSaver("./materials", telegramBotSenderService, telegramBot);
    private final TelegramBotUpdatesService telegramBotUpdatesService = new TelegramBotUpdatesService(telegramBotSenderService, parserService, telegramBotContentSaver);
    private TelegramBotUpdatesListener telegramBotUpdatesListener;
    private final Faker faker = new Faker();

    @BeforeEach
    public void init() {
        telegramBotUpdatesListener = new TelegramBotUpdatesListener(telegramBot, telegramBotUpdatesService);
    }

    @ParameterizedTest
    @MethodSource("paramForRequestResponseMessageTest")
    public void requestResponseMessageTest(Long id, String request, String response) {
        List<Update> updateList = Collections.singletonList(generateUpdateMessageWithReflection(
                "",
                "",
                "",
                id,
                request
        ));

        telegramBotUpdatesListener.process(updateList);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(2)).execute(argumentCaptor.capture());

        List<SendMessage> actualList = argumentCaptor.getAllValues();

        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(response);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo("Select action");
    }

    @ParameterizedTest
    @MethodSource("paramForRequestResponseCallbackQueryTestTest")
    public void requestResponseCallbackQueryTest(Long id, String request, String response) {
        List<Update> updateList = Collections.singletonList(generateUpdateCallbackQueryWithReflection(
                "",
                "",
                "",
                id,
                request
        ));

        telegramBotUpdatesListener.process(updateList);

        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(2)).execute(argumentCaptor.capture());

        List<SendMessage> actualList = argumentCaptor.getAllValues();

        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);

        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(response);

        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo("Select action");
    }

    public static Stream<Arguments> paramForRequestResponseMessageTest() {
        return Stream.of(
                Arguments.of(50L, Command.INFO.getTitle(), InfoAboutShelter.getInfoEn()),
                Arguments.of(50L, Command.HOW.getTitle(), InfoTakeADog.getInfoEn())
        );
    }

    public static Stream<Arguments> paramForRequestResponseCallbackQueryTestTest() {
        return Stream.of(
                Arguments.of(50L, Command.INFO.getTitle(), InfoAboutShelter.getInfoEn()),
                Arguments.of(50L, Command.HOW.getTitle(), InfoTakeADog.getInfoEn())
        );
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
            Field userId = user.getClass().getDeclaredField("id");
            userId.setAccessible(true);
            userNameField.set(user, username);
            firstNameField.set(user, firstName);
            lastNameField.set(user, lastName);
            userId.set(user, chatId);


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