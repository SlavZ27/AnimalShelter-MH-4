package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.Update_DPO;

import java.io.IOException;

@Service
public class TelegramBotUpdatesService {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesService.class);
    private final TelegramBotSenderService telegramBotSenderService;
    private final MapperService mapperService;
    private final TelegramBotContentSaver telegramBotContentSaver;

    public TelegramBotUpdatesService(TelegramBotSenderService telegramBotSenderService, MapperService mapperService, TelegramBotContentSaver telegramBotContentSaver) {
        this.telegramBotSenderService = telegramBotSenderService;
        this.mapperService = mapperService;
        this.telegramBotContentSaver = telegramBotContentSaver;
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

        Update_DPO updateDpo = mapperService.toDPO(update);
        if (updateDpo == null) {
            logger.debug("Method processUpdate detected null updateDpo");
            return;
        }

        Long idChat = updateDpo.getIdChat();

        switch (updateDpo.getInteractionUnit()) {
            case PHOTO:
                logger.debug("ChatId={}; Method processUpdate detected photo in message()", idChat);
                try {
                    telegramBotContentSaver.savePhoto(update);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            case MESSAGE:
                telegramBotSenderService.sendSorryWhatICan(idChat);
                return;
            case COMMAND:
                logger.info("ChatId={}; Method processUpdate start process command = {}", idChat, updateDpo.getCommand());
                if (updateDpo.getCommand() == null) {
                    telegramBotSenderService.sendUnknownProcess(idChat);
                    telegramBotSenderService.sendButtonsCommandForChat(idChat);
                } else switch (updateDpo.getCommand()) {
                    case START:
                        System.out.println("Detected enter : " + idChat + " / " + updateDpo.getUserName());
                        telegramBotSenderService.sendStartButtons(idChat, updateDpo.getUserName());
                        break;
                    case INFO:
                        telegramBotSenderService.sendInfoAboutShelter(idChat);
                        telegramBotSenderService.sendButtonsCommandForChat(idChat);
                        break;
                    case HOW:
                        telegramBotSenderService.sendHowTakeDog(idChat);
                        telegramBotSenderService.sendButtonsCommandForChat(idChat);
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

