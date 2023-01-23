package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.service.UserService;

public class BadPhoneNumberException extends RuntimeException{
    public BadPhoneNumberException(String message) {
        super(message);
        Logger logger = LoggerFactory.getLogger(UserService.class);
        logger.error("Bad phone = " + message);
    }
}
