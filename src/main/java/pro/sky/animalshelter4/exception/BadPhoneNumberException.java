package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.service.UserService;

public class BadPhoneNumberException extends RuntimeException{
    public BadPhoneNumberException(String message) {
        super("Bad phone = " + message);
    }
}
