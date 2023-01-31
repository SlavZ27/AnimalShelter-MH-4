package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.service.AnimalOwnershipService;
import pro.sky.animalshelter4.service.AnimalService;

public class AnimalOwnershipAlreadyCloseException extends RuntimeException{
    public AnimalOwnershipAlreadyCloseException(String message) {
        super("AnimalOwnership with id = " + message + " already close");
    }
}
