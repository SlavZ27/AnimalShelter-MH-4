package pro.sky.animalshelter4.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.animalshelter4.service.AnimalService;
import pro.sky.animalshelter4.service.UserService;

public class AnimalsAbsentException extends RuntimeException {

    public AnimalsAbsentException() {
    }
}
