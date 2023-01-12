package pro.sky.animalshelter4.model;

/**
 * The class contains the types of user interaction with the telegram bot.
 * It can be a message, a command, sending a photo, pressing a button...
 */
public enum InteractionUnit {
    /**
     * The type of interaction is "just a message"
     */
    MESSAGE,
    /**
     * The type of interaction is applied when a command is detected in the message
     */
    COMMAND,
    /**
     * The type of interaction is applied when the user sends a photo
     */
    PHOTO,
    /**
     * The type of interaction is applied when the user clicks the button
     */
    CALLBACK_QUERY;

    InteractionUnit() {
    }
}
