package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.UpdateDPO;

import java.io.IOException;

@Service
public class TelegramBotUpdatesService {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesService.class);
    private final TelegramBotSenderService telegramBotSenderService;
    private final MapperService mapperService;
    private final TelegramBotContentSaver telegramBotContentSaver;
    private final CallRequestService callRequestService;

    public TelegramBotUpdatesService(TelegramBotSenderService telegramBotSenderService, MapperService mapperService, TelegramBotContentSaver telegramBotContentSaver, CallRequestService callRequestService) {
        this.telegramBotSenderService = telegramBotSenderService;
        this.mapperService = mapperService;
        this.telegramBotContentSaver = telegramBotContentSaver;
        this.callRequestService = callRequestService;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            logger.debug("Method processUpdate detected null update");
            return;
        }
        if (detectEmptyCommand(update)) {
            logger.debug("Method processUpdate detected empty command");
            return;
        }
        UpdateDPO updateDpo = mapperService.toDPO(update);
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
                telegramBotSenderService.sendSorryIKnowThis(updateDpo.getIdChat());
                return;
            case COMMAND:
                logger.info("ChatId={}; Method processUpdate start process command = {}",
                        updateDpo.getIdChat(), updateDpo.getCommand());
                if (updateDpo.getCommand() == null) {
                    telegramBotSenderService.sendUnknownProcess(updateDpo.getIdChat());
                    telegramBotSenderService.sendButtonsCommandForChat(updateDpo.getIdChat());
                } else switch (updateDpo.getCommand()) {
                    case START:
                        System.out.println("Detected enter : " +
                                updateDpo.getIdChat() + " / " + updateDpo.getUserName());
                        telegramBotSenderService.sendStartButtons(updateDpo.getIdChat(), updateDpo.getUserName());
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
                        telegramBotSenderService.sendButtonsCommandForChat(updateDpo.getIdChat());
                        break;
                    case EMPTY_CALLBACK_DATA_FOR_BUTTON:
                        return;
                }
        }
    }

    private boolean detectEmptyCommand(Update update) {
        return update.callbackQuery() != null &&
                update.callbackQuery().data() != null &&
                update.callbackQuery().data().equals(Command.EMPTY_CALLBACK_DATA_FOR_BUTTON.getTitle());
    }

}

