package pro.sky.animalshelter4.model;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Command {
    // isShow команду нужно показывать, но даже если isShow=false команда доступна для выполнения
    // isClient доступна для клиента(не волонтёры)
    // isVolunteer доступна для админов(волонтёров)
    // textCommand.substring(1).toUpperCase() = value
    START(0, "/start", "START", false, true, true),
    INFO(1, "/info", "About shelter", true, true, true),
    HOW(2, "/HOW", "Take a dog?", true, true, true),
    CALL_REQUEST(3, "/CALL_REQUEST", "Ask to call back", true, true, false),
    CALL_CLIENT(4, "/CALL_CLIENT", "Call client", true, false, true),
    EMPTY_CALLBACK_DATA_FOR_BUTTON(-1, "...", "", false, true, true);


    private final int order;
    private final String textCommand;
    private final String nameButton;
    private final boolean isShow;
    private final boolean isClient;
    private final boolean isVolunteer;

    Command(int order, String textCommand, String nameButton, boolean isShow, boolean isClient, boolean isVolunteer) {
        this.order = order;
        this.textCommand = textCommand;
        this.nameButton = nameButton;
        this.isShow = isShow;
        this.isClient = isClient;
        this.isVolunteer = isVolunteer;
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


    public static Command fromStringIteration(String text) {
        for (Command command : Command.values()) {
            if (command.getTextCommand().equalsIgnoreCase(text)) {
                return command;
            }
        }
        return null;
    }

    public static Command fromStringUpperCase(String text) {
        if (text == null || text.length() < 2) {
            return null;
        }
        text = text.substring(1).toUpperCase();
        try {
            return Command.valueOf(Command.class, text);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static List<Command> getOnlyShowCommand() {
        return Stream.of(
                        Command.values()).
                filter(command -> command.isShow).
                sorted(Comparator.comparingInt(Command::getOrder)).
                collect(Collectors.toList());
    }

    public static List<Command> getOnlyShowCommandForClient() {
        return getOnlyShowCommand().stream().
                filter(command -> command.isClient).
                collect(Collectors.toList());
    }

    public static List<Command> getOnlyShowCommandForVolunteer() {
        return getOnlyShowCommand().stream().
                filter(command -> command.isVolunteer).
                collect(Collectors.toList());
    }

}
