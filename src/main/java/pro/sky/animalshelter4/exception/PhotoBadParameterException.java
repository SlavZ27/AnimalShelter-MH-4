package pro.sky.animalshelter4.exception;

public class PhotoBadParameterException extends RuntimeException{
    public PhotoBadParameterException() {
        super("The parameters must be the same: " +
                "setShelter() | setAnimalOwnership().setShelter() | serPhoto().setShelter()");
    }
}
