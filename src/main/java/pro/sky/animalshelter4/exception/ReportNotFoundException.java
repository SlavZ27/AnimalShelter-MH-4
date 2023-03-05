package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.service.AnimalService;

public class ReportNotFoundException extends RuntimeException{
    public ReportNotFoundException(String message) {
        super("Report with id = " + message + " not found");
    }
}
