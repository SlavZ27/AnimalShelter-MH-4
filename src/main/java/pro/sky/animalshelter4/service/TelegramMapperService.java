package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.model.Update;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.InteractionUnit;
import pro.sky.animalshelter4.model.UpdateDPO;


/**
 * The class is intended for mapping objects into other objects.
 * A very important class, greatly affects the fault tolerance of the application
 */
@Service
public class TelegramMapperService {

    private final Logger logger = LoggerFactory.getLogger(TelegramMapperService.class);


    /**
     * The method deals with mapping Update to Update and data validation.
     * For example, idChat and userName must not be null for the program to work.
     * The method determines which command to execute depending on {@link Update}.
     * For example, if update.message().photo()!=null,
     * then {@link UpdateDPO#setInteractionUnit(InteractionUnit)} changes to {@link InteractionUnit#PHOTO}
     * If update.message().text()!=null && update.message().text().startWith("/")
     * then {@link UpdateDPO#setInteractionUnit(InteractionUnit)} changes to {@link InteractionUnit#COMMAND}
     * @param update
     * @return {@link UpdateDPO}  <br>
     * where UpdateDPO.IdChat() must be not null <br>
     * where UpdateDPO.name() must be not null <br>
     * where UpdateDPO.userName() must be not null <br>
     * return null if update == null <br>
     * return null if update.message().from() == null <br>
     * return null if update.message().from().id() == null <br>
     * return null if update.message().from().id() < 0 <br>
     * return null if update.message().from().username() != null <br>
     * return null if update.callbackQuery().from() == null <br>
     * return null if update.callbackQuery().from().id() == null <br>
     * return null if update.callbackQuery().from().id() < 0 <br>
     * return null if update.callbackQuery().from().username() != null <br>
     */
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
                    update.message().from().id() != null) {
                if (update.message().from().id() < 0) {
                    logger.error("Method toDPO detected userId < 0");
                    return null;
                }
                updateDpo.setIdChat(update.message().from().id());
                updateDpo.setFirstName(update.message().from().firstName());
                updateDpo.setLastName(update.message().from().lastName());
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
                    if (update.message().caption() != null) {
                        updateDpo.setMessage(update.message().caption());
                    }
                } else {
                    logger.debug("ChatId={}; Method toDPO detected null fileId in photo", updateDpo.getIdChat());
                }
//message text
            } else {
                if (update.message().text() != null) {
                    logger.debug("ChatId={}; Method toDPO detected text in message()", updateDpo.getIdChat());
                    updateDpo.setInteractionUnit(InteractionUnit.MESSAGE);
                    updateDpo.setMessage(update.message().text().trim());
                }
            }
//callbackQuery!=null
        } else if (update.callbackQuery() != null) {
            logger.debug("Method toDPO detected callbackQuery into update");
            updateDpo.setInteractionUnit(InteractionUnit.CALLBACK_QUERY);
//callbackQuery from
            if (update.callbackQuery().from() != null &&
                    update.callbackQuery().from().id() != null) {
                if (update.callbackQuery().from().id() < 0) {
                    logger.error("Method toDPO detected userId < 0");
                    return null;
                }
                updateDpo.setIdChat(update.callbackQuery().from().id());
                updateDpo.setFirstName(update.callbackQuery().from().firstName());
                updateDpo.setLastName(update.callbackQuery().from().lastName());
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

    /**
     * The method checks the string so that it is not null, or empty
     * @param s
     * @return true or false
     */
    private boolean isNotNullOrEmpty(String s) {
        return s != null && s.length() > 0;
    }

    /**
     * The method makes a single word from a string with many words
     * @param s,
     * @param indexWord
     * @return word with indexWord <br>
     * if (s==null) then return null <br>
     * if (indexWord > sum of words into string) then return "" <br>
     * if (string don't contain {@link TelegramBotSenderService#REQUEST_SPLIT_SYMBOL}) then return string without changes <br>
     */
    public String toWord(String s, int indexWord) {
        logger.debug("Method toWord was start for parse from string = {} word # = {}", s, indexWord);
        if (s == null) {
            return null;
        }

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

    /**
     * This method juxtapose string to long
     * @param message is not null
     * @return long message
     */
    public Long mapStringToLong(String message) {
        return Long.parseLong(message, 10);
    }

    /**
     * This method juxtapose string to long
     * @param message is not null
     * @return Long message
     */
    public Integer mapStringToInt(String message) {
        return Integer.parseInt(message, 10);
    }

}