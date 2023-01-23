package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pro.sky.animalshelter4.service.ChatService;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ChatNotFoundException extends RuntimeException{
    public ChatNotFoundException(String message) {
        super(message);
        Logger logger = LoggerFactory.getLogger(ChatService.class);
        logger.error("Chat with id = " + message + " not found");
    }
}
