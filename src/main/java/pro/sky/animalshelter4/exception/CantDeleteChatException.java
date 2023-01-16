package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.entity.Chat;

public class CantDeleteChatException extends RuntimeException{
    public CantDeleteChatException(String message) {
        super(message);
        Logger logger = LoggerFactory.getLogger(Chat.class);
        logger.error("Can't delete chat with id = " + message);
    }
}
