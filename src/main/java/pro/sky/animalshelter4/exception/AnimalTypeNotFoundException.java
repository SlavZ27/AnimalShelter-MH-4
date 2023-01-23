package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.service.AnimalService;
import pro.sky.animalshelter4.service.AnimalTypeService;

public class AnimalTypeNotFoundException extends RuntimeException{
    public AnimalTypeNotFoundException(String message) {
        super(message);
        Logger logger = LoggerFactory.getLogger(AnimalTypeService.class);
        logger.error("AnimalType with id = " + message + " not found");
    }
}
