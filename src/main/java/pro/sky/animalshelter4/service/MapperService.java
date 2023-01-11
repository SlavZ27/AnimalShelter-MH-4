package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.InteractionUnit;
import pro.sky.animalshelter4.model.UpdateDPO;

@Service
public class MapperService {

    private final Logger logger = LoggerFactory.getLogger(MapperService.class);

    public UpdateDPO toDPO(Update update) {
//update
        if (update == null) {
            logger.debug("Method toDPO detected null update");
            return null;
        }
        UpdateDPO updateDpo = new UpdateDPO();
//message!=null
        if (update.message() != null) {
            logger.debug("Method toDPO detected message into update");
//message from
            if (update.message().from() != null &&
                    update.message().from().id() != null &&
                    update.message().from().username() != null) {
                if (update.message().from().id() < 0) {
                    logger.error("Method toDPO detected userId < 0");
                    return null;
                }
                updateDpo.setIdChat(update.message().from().id());
                updateDpo.setName(toUserName(update.message().from()));
                updateDpo.setUserName(update.message().from().username());
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
                    update.callbackQuery().from().id() != null &&
                    update.callbackQuery().from().username() != null) {
                if (update.callbackQuery().from().id() < 0) {
                    logger.error("Method toDPO detected userId < 0");
                    return null;
                }
                updateDpo.setIdChat(update.callbackQuery().from().id());
                updateDpo.setName(toUserName(update.callbackQuery().from()));
                updateDpo.setUserName(update.callbackQuery().from().username());
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
            updateDpo.setCommand(Command.fromStringUpperCase(
                    toWord(updateDpo.getMessage(), 0)));
            if (updateDpo.getCommand() != null) {
                logger.debug("ChatId={}; Method toDPO detected command = {}",
                        updateDpo.getIdChat(), updateDpo.getCommand().getTextCommand());
                if (updateDpo.getCommand().getTextCommand().trim().length() >= updateDpo.getCommand().getTextCommand().length()) {
                    updateDpo.setMessage(updateDpo.getMessage().
                            substring(
                                    updateDpo.getCommand().getTextCommand().length()).
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

    public String toWord(String s, int indexWord) {
        logger.debug("Method toWord was start for parse from string = {} word # = {}", s, indexWord);

        if (!s.contains(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL)) {
            logger.debug("Method toWord don't found REQUEST_SPLIT_SYMBOL = {} and return",
                    TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
            return s;
        }
        String[] sMas = s.split(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);

        if (indexWord >= sMas.length) {
            logger.debug("Method toWord detect index of word bigger of sum words in string and return empty string");
            return "";
        }
        logger.debug("Method toWord return {}", sMas[indexWord]);
        return sMas[indexWord];
    }

    public Long toChatId(Update update) {
        if (update.message() != null &&
                update.message().from() != null &&
                update.message().from().id() != null) {
            return update.message().from().id();
        } else if (update.callbackQuery() != null &&
                update.callbackQuery().from() != null &&
                update.callbackQuery().from().id() != null) {
            return update.callbackQuery().from().id();
        }
        return null;
    }

    public String toUserName(User user) {
        StringBuilder name = new StringBuilder();
        if (isNotNullOrEmpty(user.firstName())) {
            name.append(user.firstName());
        }
        if (isNotNullOrEmpty(user.lastName())) {
            name.append(" ");
            name.append(user.lastName());
        }
        if (name.length() == 0) {
            return user.username();
        }
        return name.toString();
    }
}