package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.service.AnimalService;
import pro.sky.animalshelter4.service.CallRequestService;

public class AnimalNotFoundException extends RuntimeException{
    public AnimalNotFoundException(String message) {
        super("Animal with id = " + message + " not found");
    }


}
