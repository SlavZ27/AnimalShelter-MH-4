package pro.sky.animalshelter4.exception;

public class ShelterIndexNotFoundException extends RuntimeException {
    public ShelterIndexNotFoundException(String message) {
        super("ShelterIndex  = " + message + " not found");
    }
}
