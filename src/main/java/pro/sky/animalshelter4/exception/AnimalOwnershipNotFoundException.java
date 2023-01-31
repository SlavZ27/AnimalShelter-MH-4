package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.service.AnimalOwnershipService;
import pro.sky.animalshelter4.service.CallRequestService;

public class AnimalOwnershipNotFoundException extends RuntimeException{
    public AnimalOwnershipNotFoundException(String message) {
        super("AnimalOwnership with id = " + message + " not found");
    }

}
