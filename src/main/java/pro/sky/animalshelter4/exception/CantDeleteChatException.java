package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.entity.Chat;

public class CantDeleteChatException extends RuntimeException{
    public CantDeleteChatException(String message) {
        super("Can't delete chat with id = " + message);
    }
}
