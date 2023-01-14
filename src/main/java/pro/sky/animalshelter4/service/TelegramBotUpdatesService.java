package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.UnfinishedRequestTelegram;
import pro.sky.animalshelter4.listener.TelegramBotUpdatesListener;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.InteractionUnit;
import pro.sky.animalshelter4.model.UpdateDPO;

import java.io.IOException;
import java.util.List;

/**
 * The class deals with the choice of further actions depending on the incoming object that came from
 * {@link TelegramBotUpdatesListener#process(List)}.
 * The class must have a lot of dependencies, because other objects receive signals for action from this class.
 */
@Service
public class TelegramBotUpdatesService {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesService.class);
    private final TelegramUnfinishedRequestService telegramUnfinishedRequestService;
    private final TelegramBotSenderService telegramBotSenderService;
    private final TelegramMapperService telegramMapperService;
    private final TelegramBotContentSaver telegramBotContentSaver;
    private final CallRequestService callRequestService;
    private final CommandService commandService;
    private final ChatService chatService;
    private final UserService userService;

    public TelegramBotUpdatesService(TelegramBotSenderService telegramBotSenderService, TelegramMapperService telegramMapperService, TelegramBotContentSaver telegramBotContentSaver, CallRequestService callRequestService, CommandService commandService, UserService userService, TelegramUnfinishedRequestService telegramUnfinishedRequestService, ChatService chatService) {
        this.telegramUnfinishedRequestService = telegramUnfinishedRequestService;
        this.telegramBotSenderService = telegramBotSenderService;
        this.telegramMapperService = telegramMapperService;
        this.telegramBotContentSaver = telegramBotContentSaver;
        this.callRequestService = callRequestService;
        this.commandService = commandService;
        this.userService = userService;
        this.chatService = chatService;
    }


    /**
     * The method deals with the choice of actions depending
     * on the incoming object that came from {@link TelegramBotUpdatesListener#process(List)}
     * The definition and conversion of a hostile {@link Update}
     * to native {@link UpdateDPO} is handled by the {@link TelegramMapperService#toDPO(Update)}.
     * Then, depending on the type of interaction {@link pro.sky.animalshelter4.model.InteractionUnit}
     * and command {@link Command} and other parameters, the next action is selected.
     * The method terminates if it detects an {@link Command#EMPTY_CALLBACK_DATA_FOR_BUTTON} in {@link Update},
     * or when receiving an unexpected {@link Update}, when null comes from {@link TelegramMapperService#toDPO(Update)}.
     *
     * @param update
     */
    public void processUpdate(Update update) {
        if (update == null) {
            logger.debug("Method processUpdate detected null update");
            return;
        }
        if (detectEmptyCommand(update)) {
            logger.debug("Method processUpdate detected empty command");
            return;
        }

        UpdateDPO updateDpo = telegramMapperService.toDPO(update);
        if (updateDpo == null) {
            logger.debug("Method processUpdate detected null updateDpo");
            return;
        }

        switch (updateDpo.getInteractionUnit()) {
            case PHOTO:
                logger.debug("ChatId={}; Method processUpdate detected photo in message()", updateDpo.getIdChat());
                try {
                    telegramBotContentSaver.savePhoto(update);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            case MESSAGE:
                Command command = telegramUnfinishedRequestService.
                        findUnfinishedRequestForChat(chatService.getChatByIdOrNew(updateDpo));
                if (command == null) {
                    telegramBotSenderService.sendSorryIKnowThis(updateDpo.getIdChat());
                    return;
                } else {
                    updateDpo.setInteractionUnit(InteractionUnit.COMMAND);
                    updateDpo.setCommand(command);
                    processUpdateDpoWithCommand(updateDpo);
                }
                return;
            case COMMAND:
                logger.info("ChatId={}; Method processUpdate launch process command = {}",
                        updateDpo.getIdChat(), updateDpo.getCommand());
                processUpdateDpoWithCommand(updateDpo);
                return;
        }
    }

    public void processUpdateDpoWithCommand(UpdateDPO updateDpo) {
        if (updateDpo.getCommand() == null) {
            telegramBotSenderService.sendUnknownProcess(updateDpo.getIdChat());
            telegramBotSenderService.sendButtonsCommandForChat(updateDpo.getIdChat());
        } else {
            if (!commandService.approveLaunchCommand(updateDpo.getCommand(), updateDpo.getIdChat())) {
                logger.debug("ChatId={}; Method processUpdate detected no rights to execute command = {} ",
                        updateDpo.getIdChat(), updateDpo.getCommand());
                telegramBotSenderService.sendSorryIKnowThis(updateDpo.getIdChat());
                return;
            }
            switch (updateDpo.getCommand()) {
                case START:
                    System.out.println("Detected enter : " +
                            updateDpo.getIdChat() + " / " + updateDpo.getUserName());
                    telegramBotSenderService.sendHello(updateDpo.getIdChat(),
                            updateDpo.getFirstName() + " " + updateDpo.getLastName());
                    break;
                case INFO:
                    telegramBotSenderService.sendInfoAboutShelter(updateDpo.getIdChat());
                    telegramBotSenderService.sendButtonsCommandForChat(updateDpo.getIdChat());
                    break;
                case HOW:
                    telegramBotSenderService.sendHowTakeDog(updateDpo.getIdChat());
                    telegramBotSenderService.sendButtonsCommandForChat(updateDpo.getIdChat());
                    break;
                case CALL_REQUEST:
                    callRequestService.process(updateDpo);
                    break;
                case CALL_CLIENT:
                    callRequestService.sendNotificationAboutCallRequestsToTelegram(
                            userService.getUserWithTelegramUserId(updateDpo.getIdChat()), true);
                    telegramBotSenderService.sendButtonsCommandForChat(updateDpo.getIdChat());
                    break;
                case CLOSE_CALL_REQUEST:
                    callRequestService.closeCallRequest(
                            userService.getUserWithTelegramUserId(updateDpo.getIdChat()),
                            telegramMapperService.mapStringToLong(updateDpo.getMessage()));
                    telegramBotSenderService.sendButtonsCommandForChat(updateDpo.getIdChat());
                    break;
                case CHANGE_PHONE:
                    chatService.changePhoneUser(
                            chatService.getChatByIdOrNew(updateDpo.getIdChat()),
                            updateDpo.getMessage());
                    break;
                case CLOSE_UNFINISHED_REQUEST:
                    telegramUnfinishedRequestService.delUnfinishedRequestForChat(
                            chatService.getChatByIdOrNew(updateDpo));
                    telegramBotSenderService.sendButtonsCommandForChat(updateDpo.getIdChat());
                case EMPTY_CALLBACK_DATA_FOR_BUTTON:
                    return;
            }
        }

    }

    /**
     * The method is needed for detection of the command {@link Command#EMPTY_CALLBACK_DATA_FOR_BUTTON}
     * for which the bot will do nothing.
     * The text with this command is located in {@link Update} here <b>update.callbackQuery().data()</b>
     * The method should work before laborious parsing of the entire incoming object {@link Update}
     *
     * @param update
     * @return true - if emptyCommand was detected, else false
     */
    private boolean detectEmptyCommand(Update update) {
        return update.callbackQuery() != null &&
                update.callbackQuery().data() != null &&
                update.callbackQuery().data().equals(Command.EMPTY_CALLBACK_DATA_FOR_BUTTON.getTextCommand());
    }

}

