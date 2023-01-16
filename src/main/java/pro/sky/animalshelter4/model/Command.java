package pro.sky.animalshelter4.model;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The class contains all the commands for working with the application via telegram bot.
 * Each element contains several values. <br>
 * {@link Command#order} is needed to sort the buttons in output in telegram chat. <br>
 * {@link Command#textCommand} is needed to recognize commands in the form of text.
 * For the normal operation of {@link Command#fromStringUpperCase(String)}, {@link Command#name()}
 * and {@link Command#textCommand} must meet the requirements <b>textCommand.substring(1).toUpperCase() = name</b> <br>
 * {@link Command#nameButton} is needed to name buttons for issuing to the chat. <br>
 * {@link Command#isShow} answers whether it is necessary to show the command in the output.
 * For example, the user uses the {@link Command#START} command once and does not need to show it in the output. <br>
 * {@link Command#isClient} determines whether the client can use the command or not,
 * whether it needs to be added to the output or not.  <br>
 * {@link Command#isVolunteer} is also active. <br>
 * Class also contains methods for issuing all commands to a volunteer or client.
 * There are also methods for parsing commands from a string
 */
public enum Command {
    // isShow команду нужно показывать, но даже если isShow=false команда доступна для выполнения
    // isClient доступна для клиента(не волонтёры)
    // isVolunteer доступна для админов(волонтёров)
    // textCommand.substring(1).toUpperCase() = value
    /**
     * A command to greet the user and familiarize them with the available functions
     */
    START(0, "/start", "START", false, true, true, true),
    /**
     * The command is used to call information about the shelter
     */
    INFO(1, "/info", "About", true, true, true, true),
    /**
     * The command is used to call information about how to take an animal from a shelter
     */
    HOW(2, "/HOW", "Dog?", true, true, true, true),
    /**
     * The command is used by the client to create a callback request
     */
    CALL_REQUEST(3, "/CALL_REQUEST", "Ask to call back", true, true, true, false),
    /**
     * The command is used by a volunteer to call up a list of users who created a callback request
     */
    CALL_CLIENT(4, "/CALL_CLIENT", "Call client", true, false, false, true),
    CLOSE_CALL_REQUEST(5, "/CLOSE_CALL_REQUEST", "Close req", false, false, false, true),
    CHANGE_PHONE(6, "/CHANGE_PHONE", "phone", false, true, true, false),
    SET_OWNER(7, "/SET_OWN", "Set owner", false, false, false, true),
    CLOSE_UNFINISHED_REQUEST(80, "/CLOSE_UNFINISHED_REQUEST", "Cancel", false, true, true, true),
    /**
     * The command is used to add it to dummy buttons. Clicking on such buttons does nothing
     */
    EMPTY_CALLBACK_DATA_FOR_BUTTON(-1, "...", "", false, true, true, true);


    /**
     * Responsible for sorting
     */
    private final int order;
    /**
     * Responsible for the text display of the command
     */
    private final String textCommand;
    /**
     * Responsible for the text display of the button with the command
     */
    private final String nameButton;
    /**
     * Responsible for displaying the command to all users
     */
    private final boolean isShow;
    /**
     * Responsible for the availability of the team from the client
     */
    private final boolean isClient;
    private final boolean isOwner;
    /**
     * Responsible for the availability of the team from the volunteer
     */
    private final boolean isVolunteer;

    Command(int order, String textCommand, String nameButton, boolean isShow, boolean isClient, boolean isOwner, boolean isVolunteer) {
        this.order = order;
        this.textCommand = textCommand;
        this.nameButton = nameButton;
        this.isShow = isShow;
        this.isClient = isClient;
        this.isVolunteer = isVolunteer;
        this.isOwner = isOwner;
    }

    public int getOrder() {
        return order;
    }

    public boolean isClient() {
        return isClient;
    }

    public boolean isVolunteer() {
        return isVolunteer;
    }

    public boolean isShow() {
        return isShow;
    }

    public String getTextCommand() {
        return textCommand;
    }

    public String getNameButton() {
        return nameButton;
    }

    public boolean isOwner() {
        return isOwner;
    }

    /**
     * The method uses brute-force String-parsing on the {@link Command#values()#textCommand}.
     * using {@link String#equalsIgnoreCase(String)}
     *
     * @param textCommand must be not null
     * @return outputs instance of the class or null
     */
    public static Command fromStringIteration(String textCommand) {
        for (Command command : Command.values()) {
            if (command.getTextCommand().equalsIgnoreCase(textCommand)) {
                return command;
            }
        }
        return null;
    }

    /**
     * The method uses the native {@link Command#valueOf(String)} of returning an instance of the class.
     * For the normal operation of {@link Command#fromStringUpperCase(String)}, {@link Command#name()}
     * and {@link Command#textCommand} must meet the requirements <b>textCommand.substring(1).toUpperCase() = name
     *
     * @param textCommand must be not null
     * @return outputs instance of the class or null
     */
    public static Command fromStringUpperCase(String textCommand) {
        if (textCommand == null || textCommand.length() < 2) {
            return null;
        }
        textCommand = textCommand.substring(1).toUpperCase();
        try {
            return Command.valueOf(Command.class, textCommand);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    /**
     * @return list of Command available for display
     */
    private static List<Command> getOnlyShowCommand() {
        return Stream.of(
                        Command.values()).
                filter(command -> command.isShow).
                sorted(Comparator.comparingInt(Command::getOrder)).
                collect(Collectors.toList());
    }

    /**
     * @return list of commands available for display to the client
     */
    public static List<Command> getOnlyShowCommandForClient() {
        return getOnlyShowCommand().stream().
                filter(command -> command.isClient).
                collect(Collectors.toList());
    }

    public static List<Command> getOnlyShowCommandForOwner() {
        return getOnlyShowCommand().stream().
                filter(command -> command.isOwner).
                collect(Collectors.toList());
    }

    /**
     * @return list of commands available for display to the volunteer
     */
    public static List<Command> getOnlyShowCommandForVolunteer() {
        return getOnlyShowCommand().stream().
                filter(command -> command.isVolunteer).
                collect(Collectors.toList());
    }
}
