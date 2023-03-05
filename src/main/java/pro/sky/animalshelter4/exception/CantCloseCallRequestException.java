package pro.sky.animalshelter4.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.service.CallRequestService;

public class CantCloseCallRequestException extends RuntimeException {
    public CantCloseCallRequestException(String message) {
        super("Can't close callRequest with id = " + message);
    }
}
