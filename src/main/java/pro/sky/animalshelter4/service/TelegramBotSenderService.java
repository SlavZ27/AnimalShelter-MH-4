package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.info.InfoAboutShelter;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.info.InfoTakeADog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class TelegramBotSenderService {
    public static final String EMPTY_SYMBOL_FOR_BUTTON = " ";

    public static final String REQUEST_SPLIT_SYMBOL = " ";
    public static final String MESSAGE_SELECT_COMMAND = "Select action";
    public static final String MESSAGE_SORRY_I_DONT_KNOW_COMMAND = "Sorry, I don't know this command";
    public static final String MESSAGE_SORRY_I_KNOW_THIS = "Sorry.\nI know only this command:\n";
    public static final String MESSAGE_HELLO = "Hello ";


    private final Logger logger = LoggerFactory.getLogger(TelegramBotSenderService.class);
    private final TelegramBot telegramBot;
    private final ChatService chatService;

    public TelegramBotSenderService(TelegramBot telegramBot, ChatService chatService) {
        this.telegramBot = telegramBot;
        this.chatService = chatService;
    }

    public void sendMessage(Long idChat, String textMessage) {
        logger.info("ChatId={}; Method sendMessage was started for send a message : {}", idChat, textMessage);
        SendMessage sendMessage = new SendMessage(idChat, textMessage);
        SendResponse response = telegramBot.execute(sendMessage);
        if (response == null) {
            logger.debug("ChatId={}; Method sendMessage did not receive a response", idChat);
        } else if (response.isOk()) {
            logger.debug("ChatId={}; Method sendMessage has completed sending the message", idChat);
        } else {
            logger.debug("ChatId={}; Method sendMessage received an error : {}", idChat, response.errorCode());
        }
    }

    public void sendUnknownProcess(Long idChat) {
        logger.info("ChatId={}; Method sendUnknownProcess was started for send a message about unknown command",
                idChat);
        sendMessage(idChat, MESSAGE_SORRY_I_DONT_KNOW_COMMAND);
    }

    public void sendStart(Long idChat, String userName) {
        logger.info("ChatId={}; Method sendStart was started for send a welcome message", idChat);
        sendMessage(idChat, "Hello " + userName + ".\n" +
                "I know some command:\n" + Command.getAllTitlesAsListExcludeHide(chatService.isVolunteer(idChat)));
    }

    public void sendStartButtons(Long idChat, String userName) {
        logger.info("ChatId={}; Method sendStartButtons was started for send a welcome message", idChat);
        sendMessage(idChat, MESSAGE_HELLO + userName + ".\n");
        sendButtonsCommandForChat(idChat);
    }

    public void sendSorryIKnowThis(Long idChat) {
        logger.info("ChatId={}; Method processWhatICan was started for send ability", idChat);
        sendMessage(idChat, MESSAGE_SORRY_I_KNOW_THIS);
        sendButtonsCommandForChat(idChat);
    }

    public void sendInfoAboutShelter(Long idChat) {
        logger.info("ChatId={}; Method sendInfoAboutShelter was started for send info about shelter", idChat);
        sendMessage(idChat, InfoAboutShelter.getInfoEn());
    }

    public void sendHowTakeDog(Long idChat) {
        logger.info("ChatId={}; Method sendHowTakeDog was started for send how take a dog", idChat);
        sendMessage(idChat, InfoTakeADog.getInfoEn());
    }

    public void sendButtonsWithOneData(
            Long idChat,
            String caption,
            String command,
            List<String> nameButtons,
            List<String> dataButtons,
            int width, int height) {
        logger.info("ChatId={}; Method sendButtonsWithCommonData was started for send buttons", idChat);
        if (nameButtons.size() != dataButtons.size()) {
            logger.debug("ChatId={}; Method sendButtonsWithCommonData detect different size of Lists", idChat);
            return;
        }
        InlineKeyboardButton[][] tableButtons = new InlineKeyboardButton[height][width];
        int countNameButtons = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (countNameButtons < nameButtons.size()) {
                    tableButtons[i][j] = new InlineKeyboardButton(nameButtons.get(countNameButtons))
                            .callbackData(command + REQUEST_SPLIT_SYMBOL + dataButtons.get(countNameButtons));
                } else {
                    tableButtons[i][j] = new InlineKeyboardButton(EMPTY_SYMBOL_FOR_BUTTON)
                            .callbackData(Command.EMPTY_CALLBACK_DATA_FOR_BUTTON.getTitle());
                }
                countNameButtons++;
            }
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(tableButtons);
        SendMessage message = new SendMessage(idChat, caption).replyMarkup(inlineKeyboardMarkup);
        SendResponse response = telegramBot.execute(message);
        if (response.isOk()) {
            logger.debug("ChatId={}; Method sendButtonsWithCommonData has completed sending the message", idChat);
        } else {
            logger.debug("ChatId={}; Method sendButtonsWithCommonData received an error : {}",
                    idChat, response.errorCode());
        }
    }

    public void sendButtonsWithDifferentData(
            Long idChat,
            String caption,
            List<String> nameButtons,
            List<String> dataButtons,
            int width, int height) {
        logger.info("ChatId={}; Method sendButtonsWithDifferentData was started for send buttons", idChat);
        if (nameButtons.size() != dataButtons.size()) {
            logger.debug("ChatId={}; Method sendButtonsWithDifferentData detect different size of Lists", idChat);
            return;
        }
        InlineKeyboardButton[][] tableButtons = new InlineKeyboardButton[height][width];
        int indexLists = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (indexLists < nameButtons.size()) {
                    tableButtons[i][j] = new InlineKeyboardButton(nameButtons.get(indexLists))
                            .callbackData(dataButtons.get(indexLists));
                } else {
                    tableButtons[i][j] = new InlineKeyboardButton(EMPTY_SYMBOL_FOR_BUTTON)
                            .callbackData(Command.EMPTY_CALLBACK_DATA_FOR_BUTTON.getTitle());
                }
                indexLists++;
            }
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(tableButtons);
        SendMessage message = new SendMessage(idChat, caption).replyMarkup(inlineKeyboardMarkup);
        SendResponse response = telegramBot.execute(message);
        if (response == null) {
            logger.debug("ChatId={}; Method sendButtonsWithDifferentData did not receive a response", idChat);
            return;
        } else if (response.isOk()) {
            logger.debug("ChatId={}; Method sendButtonsWithDifferentData has completed sending the message", idChat);
        } else {
            logger.debug("ChatId={}; Method sendButtonsWithDifferentData received an error : {}",
                    idChat, response.errorCode());
        }
    }


    public void sendListCommandForChat(Long idChat) {
        logger.info("ChatId={}; Method sendListCommandForChat was started for send list of command", idChat);
        sendMessage(idChat, Command.getAllTitlesAsListExcludeHide(chatService.isVolunteer(idChat)));
    }

    public void sendButtonsCommandForChat(Long idChat) {
        logger.info("ChatId={}; Method sendListCommandForChat was started for send list of command", idChat);
        boolean isVolunteer = chatService.isVolunteer(idChat);
        List<String> nameList = Command.getPairListsForButtonExcludeHide(isVolunteer).getFirst();
        List<String> dataList = Command.getPairListsForButtonExcludeHide(isVolunteer).getSecond();
        int countButtons = nameList.size();
        int width = 0;
        int height = 0;

        if (countButtons == 0) {
            logger.debug("ChatId={}; Method sendButtonsCommandForChat detected count of command = 0", idChat);
            return;
        }
        if (countButtons == 1) {
            width = 1;
            height = 1;
        } else if (countButtons % 7 == 0) {
            width = 7;
            height = countButtons / 7;
        } else if (countButtons % 5 == 0) {
            width = 5;
            height = countButtons / 5;
        } else if (countButtons % 3 == 0) {
            width = 3;
            height = countButtons / 3;
        } else if (countButtons % 2 == 0) {
            width = 2;
            height = countButtons / 2;
        }
        sendButtonsWithDifferentData(
                idChat,
                MESSAGE_SELECT_COMMAND,
                nameList,
                dataList,
                width, height);
    }

    public void sendPhoto(Long idChat, String pathFile) throws IOException {
        Path path = Paths.get(pathFile);
        byte[] file = Files.readAllBytes(path);
        SendPhoto sendPhoto = new SendPhoto(idChat, file);
        telegramBot.execute(sendPhoto).message();
    }

}

