package pro.sky.animalshelter4.exception;

public class CallRequestBadParameterException extends RuntimeException {
    public CallRequestBadParameterException() {
        super("The parameters must be the same: " +
                "CallRequest.setShelter() | client.setShelter() | volunteer.setShelter()");
    }
}
