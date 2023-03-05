package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.service.ShelterService;

public class ShelterNotFoundException extends RuntimeException{
    public ShelterNotFoundException(String message) {
        super("Shelter with parameter = " + message + " not found");
    }
}
