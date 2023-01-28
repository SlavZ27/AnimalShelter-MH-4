package pro.sky.animalshelter4.exception;

public class ChatDontHaveShelterIndex extends RuntimeException {
    public ChatDontHaveShelterIndex(String message) {
        super("Chat with id = " + message + " does not have a shelter index");
    }
}
