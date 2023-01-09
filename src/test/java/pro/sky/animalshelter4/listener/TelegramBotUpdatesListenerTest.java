package pro.sky.animalshelter4.listener;


import com.github.javafaker.Faker;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import pro.sky.animalshelter4.UpdateGenerator;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.City;
import pro.sky.animalshelter4.info.InfoAboutShelter;
import pro.sky.animalshelter4.info.InfoTakeADog;
import pro.sky.animalshelter4.repository.ChatRepository;
import pro.sky.animalshelter4.service.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;


@Profile("test")
@ExtendWith(MockitoExtension.class)
class TelegramBotUpdatesListenerTest {
    private final TelegramBot telegramBot = mock(TelegramBot.class);
    private final MapperService mapperService = new MapperService();
    private final TelegramBotSenderService telegramBotSenderService = new TelegramBotSenderService(telegramBot);
    private final TelegramBotContentSaver telegramBotContentSaver = new TelegramBotContentSaver("./materials", telegramBotSenderService, telegramBot);
    private final TelegramBotUpdatesService telegramBotUpdatesService = new TelegramBotUpdatesService(telegramBotSenderService, mapperService, telegramBotContentSaver);
    private final TelegramBotUpdatesListener telegramBotUpdatesListener = new TelegramBotUpdatesListener(telegramBot, telegramBotUpdatesService);

    private final UpdateGenerator updateGenerator = new UpdateGenerator();

    @BeforeEach
    public void init() {
    }

    @Test
    public void requestSTARTTest() {
        Long id = 50L;
        String command = Command.START.getTitle();
        List<Update> updateList = new ArrayList<>(List.of(
                updateGenerator.generateUpdateMessageWithReflection("", "", "", id, command)));
        telegramBotUpdatesListener.process(updateList);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(2)).execute(argumentCaptor.capture());
        List<SendMessage> actualList = argumentCaptor.getAllValues();
        Assertions.assertThat(actualList.size()).isEqualTo(2);
        SendMessage actual0 = actualList.get(0);
        SendMessage actual1 = actualList.get(1);
        Assertions.assertThat(actual0.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual0.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_HELLO +
                updateList.get(0).message().from().firstName() + ".\n");
        Assertions.assertThat(actual1.getParameters().get("chat_id")).isEqualTo(id);
        Assertions.assertThat(actual1.getParameters().get("text")).isEqualTo(TelegramBotSenderService.MESSAGE_SELECT_COMMAND);
    }

    @Test
    public void requestINFOTest() {
        Long id = 50L;
        String command = Command.INFO.getTitle();
        List<Update> updateList = new ArrayList<>(List.of(
                updateGenerator.generateUpdateMessageWithReflection("", "", "", id, command),
                updateGenerator.generateUpdateMessageWithReflection("", "", "", id, command + " 1"),
                updateGenerator.generateUpdateCallbackQueryWithReflection("", "", "", id, command)));
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

    @Test
    public void requestHOWTest() {
        Long id = 50L;
        String command = Command.HOW.getTitle();
        List<Update> updateList = new ArrayList<>(List.of(
                updateGenerator.generateUpdateMessageWithReflection("", "", "", id, command),
                updateGenerator.generateUpdateCallbackQueryWithReflection("", "", "", id, command)));
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

    @Test
    public void requestUNKNOWNCommandTest() {
        Long id = 50L;
        String command = "/fegfdhesfhdgmghrfdgg";
        List<Update> updateList = new ArrayList<>(List.of(
                updateGenerator.generateUpdateMessageWithReflection("", "", "", id, command)));
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

    @Test
    public void requestUnknownTextTest() {
        Long id = 50L;
        String command = "fegfdhesfhdgmghrfdgg";
        List<Update> updateList = new ArrayList<>(List.of(
                updateGenerator.generateUpdateMessageWithReflection("", "", "", id, command)));
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

    @Test
    public void requestEmptyCommandTest() {
        Long id = 50L;
        String command = Command.EMPTY_CALLBACK_DATA_FOR_BUTTON.getTitle();
        List<Update> updateList = new ArrayList<>(List.of(
                updateGenerator.generateUpdateCallbackQueryWithReflection("", "", "", id, command)));
        telegramBotUpdatesListener.process(updateList);
        Mockito.verify(telegramBot, times(0)).execute(any());
    }

}