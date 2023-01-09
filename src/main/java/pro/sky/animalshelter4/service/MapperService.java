package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.InteractionUnit;
import pro.sky.animalshelter4.model.Update_DPO;

@Service
public class MapperService {

    private final Logger logger = LoggerFactory.getLogger(MapperService.class);
    private final ParserService parserService;

    public MapperService(ParserService parserService) {
        this.parserService = parserService;
    }

    public Update_DPO toDPO(Update update) {
//update
        if (update == null) {
            logger.debug("Method toDPO detected null update");
            return null;
        }
        Update_DPO updateDpo = new Update_DPO();
//message!=null
        if (update.message() != null) {
            logger.debug("Method toDPO detected message into update");
//message from
            if (update.message().from() != null &&
                    update.message().from().id() != null) {
                updateDpo.setIdChat(update.message().from().id());
                String name = "";
                if (isNotNullOrEmpty(update.message().from().firstName())) {
                    name = update.message().from().firstName();
                } else if (isNotNullOrEmpty(update.message().from().lastName())) {
                    name = update.message().from().lastName();
                } else if (isNotNullOrEmpty(update.message().from().username())) {
                    name = update.message().from().username();
                }
                updateDpo.setUserName(name);
                logger.debug("ChatId={}; Method toDPO detected idChat", updateDpo.getIdChat());
            } else {
                logger.error("Method toDPO detected null user in update.message()");
                return null;
            }
//message photo
            if (update.message().photo() != null) {
                logger.debug("ChatId={}; Method toDPO detected photo in message()", updateDpo.getIdChat());
                updateDpo.setInteractionUnit(InteractionUnit.PHOTO);
                int maxPhotoIndex = update.message().photo().length - 1;
                if (update.message().photo()[maxPhotoIndex].fileId() != null) {
                    updateDpo.setIdMedia(update.message().photo()[maxPhotoIndex].fileId());
                } else {
                    logger.debug("ChatId={}; Method toDPO detected null fileId in photo", updateDpo.getIdChat());
                }
            }
//message text
            if (update.message().text() != null) {
                logger.debug("ChatId={}; Method toDPO detected text in message()", updateDpo.getIdChat());
                updateDpo.setInteractionUnit(InteractionUnit.MESSAGE);
                updateDpo.setMessage(update.message().text().trim());
//callbackQuery!=null
            }
        } else if (update.callbackQuery() != null) {
            logger.debug("Method toDPO detected callbackQuery into update");
            updateDpo.setInteractionUnit(InteractionUnit.CALLBACK_QUERY);
//callbackQuery from
            if (update.callbackQuery().from() != null &&
                    update.callbackQuery().from().id() != null) {
                updateDpo.setIdChat(update.callbackQuery().from().id());
                String name = "";
                if (isNotNullOrEmpty(update.callbackQuery().from().firstName())) {
                    name = update.callbackQuery().from().firstName();
                } else if (isNotNullOrEmpty(update.callbackQuery().from().lastName())) {
                    name = update.callbackQuery().from().lastName();
                } else if (isNotNullOrEmpty(update.callbackQuery().from().username())) {
                    name = update.callbackQuery().from().username();
                }
                updateDpo.setUserName(name);
                logger.debug("ChatId={}; Method toDPO detected idChat", updateDpo.getIdChat());
            } else {
                logger.error("Method toDPO detected null user in update.callbackQuery()");
                return null;
            }
//callbackQuery data
            if (update.callbackQuery().data() != null) {
                logger.debug("ChatId={}; Method toDPO detected data in callbackQuery()", updateDpo.getIdChat());
                updateDpo.setInteractionUnit(InteractionUnit.MESSAGE);
                updateDpo.setMessage(update.callbackQuery().data().trim());
            }
        }
//updateDpo.Message -> Command
        if (updateDpo.getMessage() != null && updateDpo.getMessage().startsWith("/")) {
            updateDpo.setInteractionUnit(InteractionUnit.COMMAND);
            updateDpo.setCommand(Command.fromString(
                    parserService.parseWord(updateDpo.getMessage(), 0)));
            if (updateDpo.getCommand() != null) {
                logger.debug("ChatId={}; Method toDPO detected command = {}", updateDpo.getIdChat(), updateDpo.getCommand().getTitle());
                if (updateDpo.getCommand().getTitle().trim().length() > updateDpo.getCommand().getTitle().length()) {
                    updateDpo.setMessage(updateDpo.getMessage().
                            substring(
                                    updateDpo.getCommand().getTitle().length()).
                            trim());
                }
            }
        } else {
            logger.debug("ChatId={}; Method toDPO don't detected command in callbackQuery()", updateDpo.getIdChat());
            updateDpo.setCommand(null);
        }
        return updateDpo;
    }

    private boolean isNotNullOrEmpty(String s) {
        return s != null && s.length() > 0;
    }

}