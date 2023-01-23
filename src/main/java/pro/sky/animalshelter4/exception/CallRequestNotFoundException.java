package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pro.sky.animalshelter4.service.CallRequestService;
import pro.sky.animalshelter4.service.ChatService;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CallRequestNotFoundException extends RuntimeException{
    public CallRequestNotFoundException(String message) {
        super(message);
        Logger logger = LoggerFactory.getLogger(CallRequestService.class);
        logger.error("CallRequest with id = " + message + " not found");
    }
}
