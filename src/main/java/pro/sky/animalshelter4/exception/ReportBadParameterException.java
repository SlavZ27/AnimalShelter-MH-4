package pro.sky.animalshelter4.exception;

public class ReportBadParameterException extends RuntimeException{
    public ReportBadParameterException() {
        super("The parameters must be the same: " +
                "setShelter() | setAnimalOwnership().setShelter()");
    }
}
