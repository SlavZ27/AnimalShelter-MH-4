package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.component.Command;

import java.io.IOException;

@Service
public class TelegramBotUpdatesService {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesService.class);
    private final TelegramBotSenderService telegramBotSenderService;
    private final ParserService parserService;
    private final TelegramBotContentSaver telegramBotContentSaver;

    public TelegramBotUpdatesService(TelegramBotSenderService telegramBotSenderService, ParserService parserService, TelegramBotContentSaver telegramBotContentSaver) {
        this.telegramBotSenderService = telegramBotSenderService;
        this.parserService = parserService;
        this.telegramBotContentSaver = telegramBotContentSaver;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            logger.debug("Method processUpdate detected nul update");
            return;
        }

        if (update.message() != null) {
            if (update.message().from() == null ||
                    update.message().from().id() == null) {
                logger.error("Method processUpdate detected null user in update.message()");
                return;
            }
            Long idChat = update.message().chat().id();
            //here begins the processing of the photo
            if (update.message().photo() != null) {
                logger.debug("ChatId={}; Method processUpdate detected photo in message()", idChat);
                try {
                    telegramBotContentSaver.savePhoto(update);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            //here begins the processing of the text
            if (update.message().text() != null) {
                logger.debug("ChatId={}; Method processUpdate detected text in message()", idChat);
                String message = update.message().text().trim();

                //here begins the processing of the command
                if (message.startsWith("/")) {
                    Command command = Command.fromString(parserService.parseWord(message, 0));
                    if (command == null) {
                        logger.debug("Method processUpdate don't detected command from = {}", message);
                        telegramBotSenderService.sendUnknownProcess(update);
                        return;
                    }
                    switch (command) {
                        case START:
                            logProcessUpdateDetectedValidMessageCommand(command.getTitle(), idChat);
                            System.out.println(
                                    "detected enter : " +
                                            idChat + " / " +
                                            update.message().from().username() + " / " +
                                            update.message().from().firstName() + " / " +
                                            update.message().from().lastName());
                            telegramBotSenderService.sendStartButtons(update);
                            break;
                        case INFO:
                            logProcessUpdateDetectedValidMessageCommand(command.getTitle(), idChat);
                            telegramBotSenderService.sendInfoAboutShelter(update);
                            telegramBotSenderService.sendButtonsCommandForChat(update);
                            break;
                        case HOW:
                            logProcessUpdateDetectedValidMessageCommand(command.getTitle(), idChat);
                            telegramBotSenderService.sendHowTakeDog(update);
                            telegramBotSenderService.sendButtonsCommandForChat(update);
                            break;
                        default:
                            logger.info("ChatId={}; Method processUpdate detected unknown command", idChat);
                            telegramBotSenderService.sendUnknownProcess(update);
                            telegramBotSenderService.sendButtonsCommandForChat(update);
                            break;
                    }
                }
            }
        }
        //here begins the processing of the buttons
        if (update.callbackQuery() != null) {
            if (update.callbackQuery().data() == null ||
                    update.callbackQuery().from() == null ||
                    update.callbackQuery().from().id() == null) {
                logger.debug("Method processUpdate detected null in callbackQuery()");
                return;
            }
            Long idChat = update.callbackQuery().from().id();
            //here begins the check of empty data of buttons
            if (update.callbackQuery().data().equals(
                    Command.EMPTY_CALLBACK_DATA_FOR_BUTTON.getTitle())) {
                logger.debug("ChatId={}; Method processUpdate detected VARIABLE_EMPTY_CALLBACK_DATA_FOR_BUTTON in callbackQuery", idChat);
                return;
            }
            String callbackQuery = update.callbackQuery().data();
            Command callbackQueryCommand = Command.fromString(parserService.parseWord(callbackQuery, 0));
            //here begins the check of command
            if (callbackQueryCommand == null) {
                logger.debug("ChatId={}; Method processUpdate don't detected command from = {}", idChat, callbackQuery);
                telegramBotSenderService.sendUnknownProcess(update);
                return;
            }
            switch (callbackQueryCommand) {
                case INFO:
                    logProcessUpdateDetectedValidCallbackQueryCommand(callbackQueryCommand.getTitle(), idChat);
                    telegramBotSenderService.sendInfoAboutShelter(update);
                    telegramBotSenderService.sendButtonsCommandForChat(update);
                    break;
                case HOW:
                    logProcessUpdateDetectedValidMessageCommand(callbackQueryCommand.getTitle(), idChat);
                    telegramBotSenderService.sendHowTakeDog(update);
                    telegramBotSenderService.sendButtonsCommandForChat(update);
                    break;
            }
        }
    }

    private void logProcessUpdateDetectedValidMessageCommand(String command, long idChat) {
        logger.info("ChatId={}; Method processUpdate detected command from message '" + command + "'", idChat);
    }

    private void logProcessUpdateDetectedValidCallbackQueryCommand(String command, long idChat) {
        logger.info("ChatId={}; Method processUpdate detected command from callbackQuery '" +
                command + "'", idChat);
    }

}

