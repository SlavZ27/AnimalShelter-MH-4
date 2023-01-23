package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.service.AnimalService;
import pro.sky.animalshelter4.service.PhotoService;

public class PhotoNotFoundException extends RuntimeException{
    public PhotoNotFoundException(String message) {
        super(message);
        Logger logger = LoggerFactory.getLogger(PhotoService.class);
        logger.error("Photo with id = " + message + " not found");
    }
}
