package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.service.UserService;

public class VolunteersIsAbsentException extends RuntimeException {
    public VolunteersIsAbsentException() {
        Logger logger = LoggerFactory.getLogger(UserService.class);
        logger.error("Volunteers is absent");
    }
}
