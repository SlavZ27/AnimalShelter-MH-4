package pro.sky.animalshelter4.exception;

public class ShelterInfoNotFoundException extends RuntimeException{
    public ShelterInfoNotFoundException(String message) {
        super("ShelterInfo with parameter = " + message + " not found");
    }
}
