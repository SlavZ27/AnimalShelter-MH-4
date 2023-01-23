package pro.sky.animalshelter4.model;


import com.pengrad.telegrambot.model.Update;
import pro.sky.animalshelter4.service.TelegramMapperService;

import java.util.Objects;

/**
 * The class is needed for simplified program interaction with {@link com.pengrad.telegrambot.model.Update}.
 * The class is mapped from {@link com.pengrad.telegrambot.model.Update}.
 * The class contains all the fields necessary for the work, which are filled in as needed and immediately pass checks
 * where {@link com.pengrad.telegrambot.model.Update} map to UpdateDPO by
 * {@link TelegramMapperService#toDPO(Update)}
 * Which has a positive effect on the fault tolerance of the application
 */
//Data Processing object
public class UpdateDPO {
    private Long idChat;
    private String firstName;
    private String lastName;
    private String userName;
    private Command command;
    private String message;
    private String idMedia;
    private InteractionUnit interactionUnit;

    public UpdateDPO() {
    }

    public UpdateDPO(Long idChat, String firstName, String lastName, String userName, Command command, String message, String idMedia, InteractionUnit interactionUnit) {
        this.idChat = idChat;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.command = command;
        this.message = message;
        this.idMedia = idMedia;
        this.interactionUnit = interactionUnit;
    }

    public Long getIdChat() {
        return idChat;
    }

    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(String idMedia) {
        this.idMedia = idMedia;
    }

    public InteractionUnit getInteractionUnit() {
        return interactionUnit;
    }

    public void setInteractionUnit(InteractionUnit interactionUnit) {
        this.interactionUnit = interactionUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateDPO updateDPO = (UpdateDPO) o;
        return Objects.equals(idChat, updateDPO.idChat) && Objects.equals(firstName, updateDPO.firstName) && Objects.equals(lastName, updateDPO.lastName) && Objects.equals(userName, updateDPO.userName) && command == updateDPO.command && Objects.equals(message, updateDPO.message) && Objects.equals(idMedia, updateDPO.idMedia) && interactionUnit == updateDPO.interactionUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idChat, firstName, lastName, userName, command, message, idMedia, interactionUnit);
    }
}
