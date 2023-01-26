package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.info.InfoAboutShelter;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.info.InfoTakeADog;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The class is needed for the simplicity of sending a message to telegram chats using {@link TelegramBot#execute(BaseRequest)}.
 * The class contains both a universal method {@link TelegramBotSenderService#sendMessage(Long, String)},
 * and ready-made methods for sending prepared messages, or buttons, or other
 */
@Service
public class TelegramBotSenderService {
    /**
     * Constant is used to name an empty button
     */
    public static final String EMPTY_SYMBOL_FOR_BUTTON = " ";
    /**
     * Constant is used to separate words in text queries
     */
    public static final String REQUEST_SPLIT_SYMBOL = " ";
    public static final String MESSAGE_SELECT_COMMAND = "Select action";
    public static final String MESSAGE_SELECT_SHELTER = "Select shelter";
    public static final String MESSAGE_SORRY_I_DONT_KNOW_COMMAND = "Sorry, I don't know this command";
    public static final String MESSAGE_SORRY_I_DONT_KNOW_YOUR_PHONE = "I do not know your phone number. Please write";
    public static final String MESSAGE_SORRY_I_KNOW_THIS = "Sorry.\nI know only this command:\n";
    public static final String MESSAGE_HELLO = "Hello ";
    public static final String NAME_BUTTON_FOR_CANCEL = "Cancel";

    private final Logger logger = LoggerFactory.getLogger(TelegramBotSenderService.class);
    private final TelegramBot telegramBot;
    private final CommandService commandService;
    private final ShelterService shelterService;

    public TelegramBotSenderService(TelegramBot telegramBot, CommandService commandService, ShelterService shelterService) {
        this.telegramBot = telegramBot;
        this.commandService = commandService;
        this.shelterService = shelterService;
    }

    /**
     * A universal method for sending messages to telegram chats
     * using {@link TelegramBot#execute(BaseRequest)}
     *
     * @param idChat      must be not null
     * @param textMessage must be not null
     */
    public void sendMessage(Long idChat, String textMessage) {
        logger.info("ChatId={}; Method sendMessage was started for send a message : {}", idChat, textMessage);
        SendMessage sendMessage = new SendMessage(idChat, textMessage);
        SendResponse response = telegramBot.execute(sendMessage);
        if (response != null) {
            if (response.isOk()) {
                logger.debug("ChatId={}; Method sendMessage has completed sending the message", idChat);
            } else {
                logger.debug("ChatId={}; Method sendMessage received an error : {}",
                        idChat, response.errorCode());
            }
        } else {
            logger.debug("ChatId={}; Method sendMessage don't received response",
                    idChat);
        }
    }


    /**
     * A method with a prepared message for sending a welcome message
     * using {@link TelegramBotSenderService#MESSAGE_HELLO} + name + ".\n"
     * using {@link TelegramBotSenderService#sendMessage(Long, String)}
     *
     * @param idChat must be not null
     * @param name   must be not null
     */
    public void sendHello(Long idChat, String name) {
        logger.info("ChatId={}; Method sendStartButtons was started for send a welcome message", idChat);
        sendMessage(idChat, MESSAGE_HELLO + name + ".\n");
        sendSelectShelter(idChat);
    }

    public void sendSelectShelter(Long idChat) {
        List<String> nameDataButtons = shelterService.getAllshelterDesignation();
        Pair<Integer, Integer> widthAndHeight = getTableSize(nameDataButtons.size());
        sendButtonsWithOneData(idChat,
                MESSAGE_SELECT_SHELTER,
                Command.SET_SHELTER.getTextCommand(),
                nameDataButtons,
                nameDataButtons,
                widthAndHeight.getFirst(), widthAndHeight.getSecond());
    }


    /**
     * A method for sending a set of buttons to a telegram chat with the same first value and different second values <br>
     * Messages will be signed {@param caption} <br>
     * Each button will be signed with a value from the {@param nameButtons}. <br>
     * Each button will contain a value of {@param command} + " " + value from the {@param dataButtons}. <br>
     * For example: <br>
     * /start 1 <br>
     * /start 2 <br>
     * /start 3 <br>
     * The size of the common rectangle of buttons is set by {@param width} and {@param height}
     * using {@link TelegramBot#execute(BaseRequest)}
     *
     * @param idChat      must be not null
     * @param caption     must be not null
     * @param command     must be not null
     * @param nameButtons must be not null
     * @param dataButtons must be not null
     * @param width       must be adequate
     * @param height      must be adequate
     */
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
                            .callbackData(Command.EMPTY_CALLBACK_DATA_FOR_BUTTON.getTextCommand());
                }
                countNameButtons++;
            }
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(tableButtons);
        SendMessage message = new SendMessage(idChat, caption).replyMarkup(inlineKeyboardMarkup);
        SendResponse response = telegramBot.execute(message);
        if (response != null) {
            if (response.isOk()) {
                logger.debug("ChatId={}; Method sendButtonsWithCommonData has completed sending the message", idChat);
            } else {
                logger.debug("ChatId={}; Method sendButtonsWithCommonData received an error : {}",
                        idChat, response.errorCode());
            }
        } else {
            logger.debug("ChatId={}; Method sendButtonsWithCommonData don't received response",
                    idChat);
        }
    }

    /**
     * A method for sending a set of buttons to a telegram chat with different values <br>
     * Messages will be signed {@param caption} <br>
     * Each button will be signed with a value from the {@param nameButtons}. <br>
     * Each button will contain a value from the {@param dataButtons}. <br>
     * The size of the common rectangle of buttons is set by {@param width} and {@param height}
     * using {@link TelegramBot#execute(BaseRequest)}
     *
     * @param idChat      must be not null
     * @param caption     must be not null
     * @param nameButtons must be not null
     * @param dataButtons must be not null
     * @param width       must be adequate
     * @param height      must be adequate
     */
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
                            .callbackData(Command.EMPTY_CALLBACK_DATA_FOR_BUTTON.getTextCommand());
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


    /**
     * The method sends the string value of all available commands to idChat as a list.
     * The provision of the list, using idChat, is handled by {@link CommandService#getAllTitlesAsListExcludeHide(Chat)}
     * using {@link TelegramBotSenderService#sendMessage(Long, String)}
     *
     * @param chat must be not null
     */
    public void sendListCommandForChat(Chat chat) {
        logger.info("ChatId={}; Method sendListCommandForChat was started for send list of command", chat.getId());
        sendMessage(chat.getId(), commandService.getAllTitlesAsListExcludeHide(chat));
    }


    /**
     * Method calculates the optimal width and height values depending on the number of objects
     *
     * @param countElements
     * @return Pair of width and height
     */
    public Pair<Integer, Integer> getTableSize(int countElements) {
        int width = 0;
        int height = 0;
        if (countElements == 1) {
            width = 1;
            height = 1;
        } else if (countElements > 4) {
            width = 4;
            if (countElements % 4 == 0) {
                height = countElements / 4;
            } else {
                height = countElements / 4 + 1;
            }
        } else if (countElements % 4 == 0) {
            width = 4;
            height = countElements / 4;
        } else if (countElements % 3 == 0) {
            width = 3;
            height = countElements / 3;
        } else if (countElements % 2 == 0) {
            width = 2;
            height = countElements / 2;
        }
        return Pair.of(width, height);
    }


    /**
     * The method receives a pair of lists of button names and button data from
     * {@link CommandService#getPairListsForButtonExcludeHide(Chat)}
     * receives a pair of values width and height from {@link TelegramBotSenderService#getTableSize(int)},
     * forms a request and starts sending buttons to the chat
     * using {@link TelegramBotSenderService#sendButtonsWithDifferentData(Long, String, List, List, int, int)}
     *
     * @param chat must be not null
     */
    public void sendButtonsCommandForChat(Chat chat) {
        logger.info("ChatId={}; Method sendListCommandForChat was started for send list of command", chat.getId());
        Pair<List<String>, List<String>> nameAndDataOfButtons = commandService.getPairListsForButtonExcludeHide(chat);

        List<String> nameList = nameAndDataOfButtons.getFirst();
        List<String> dataList = nameAndDataOfButtons.getSecond();
        int countButtons = nameList.size();

        if (countButtons == 0) {
            logger.debug("ChatId={}; Method sendButtonsCommandForChat detected count of command = 0", chat.getId());
            return;
        }
        Pair<Integer, Integer> widthAndHeight = getTableSize(countButtons);
        int width = widthAndHeight.getFirst();
        int height = widthAndHeight.getSecond();
        sendButtonsWithDifferentData(
                chat.getId(),
                MESSAGE_SELECT_COMMAND,
                nameList,
                dataList,
                width, height);
    }

    /**
     * Method sends a photo to telegram chat located in the file system.
     * Using {@link Paths#get(URI)}
     * Using {@link Files#readAllBytes(Path)}
     * Using {@link TelegramBot#execute(BaseRequest)}
     *
     * @param idChat   must be not null
     * @param pathFile must be not null
     * @throws IOException
     */
    public void sendPhoto(Long idChat, String pathFile) throws IOException {
        Path path = Paths.get(pathFile);
        byte[] file = Files.readAllBytes(path);
        SendPhoto sendPhoto = new SendPhoto(idChat, file);
        telegramBot.execute(sendPhoto).message();
    }

    /**
     * this method allows you to close an incomplete request using the cancel button
     *
     * @param idChat     is not null
     * @param message    is not null
     * @param nameButton is not null
     */

    public void sendMessageWithButtonCancel(Long idChat, String message, String nameButton) {
        sendButtonsWithDifferentData(
                idChat,
                message,
                Collections.singletonList(nameButton),
                Collections.singletonList(Command.CLOSE_UNFINISHED_REQUEST.getTextCommand()),
                1, 1
        );
    }


    public void sendMessageWithOneButton(Long idChat, String message, String nameButton, String dataButton) {
        sendButtonsWithDifferentData(
                idChat,
                message,
                Collections.singletonList(nameButton),
                Collections.singletonList(dataButton),
                1, 1
        );
    }

    /**
     * This method send message client I Don't Know Your Phone Write It
     *
     * @param idChat is not null
     */
    public void sendIDontKnowYourPhoneWriteIt(Long idChat) {
        logger.info("ChatId={}; Method sendIDontKnowYourPhone was started for send offer to enter a phone number", idChat);
        sendMessageWithButtonCancel(idChat, MESSAGE_SORRY_I_DONT_KNOW_YOUR_PHONE, NAME_BUTTON_FOR_CANCEL);
    }

}

