package pro.sky.animalshelter4.component;

import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

public enum Command {
    START("/start", "START", true, true, false),
    INFO("/info", "About shelter", false, true, false),
    EMPTY_CALLBACK_DATA_FOR_BUTTON("...", "", true, true, false);


    private final String title;
    private final String nameButton;
    private final boolean isHide;
    private final boolean isPublic;
    private final boolean isVolunteer;

    Command(String title, String nameButton, boolean isHide, boolean isPublic, boolean isVolunteer) {
        this.title = title;
        this.nameButton = nameButton;
        this.isHide = isHide;
        this.isPublic = isPublic;
        this.isVolunteer = isVolunteer;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isVolunteer() {
        return isVolunteer;
    }

    public boolean isHide() {
        return isHide;
    }

    public String getTitle() {
        return title;
    }

    public String getNameButton() {
        return nameButton;
    }

    public static Command fromString(String text) {
        for (Command command : Command.values()) {
            if (command.getTitle().equalsIgnoreCase(text)) {
                return command;
            }
        }
        return null;
    }

    public static String getAllValuesFromNewLineExcludeHideCommand() {
        StringBuilder sb = new StringBuilder();
        for (Command commandClient : Command.values()) {
            if (!commandClient.isHide) {
                sb.append(commandClient.getTitle());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static List<String> getListValuesExcludeHideCommand() {
        List<String> stringList = new ArrayList<>();
        for (Command commandClient : Command.values()) {
            if (!commandClient.isHide) {
                stringList.add(commandClient.getTitle());
            }
        }
        return stringList;
    }

    public static Pair<List<String>, List<String>> getListsForButtonExcludeHideCommand() {
        List<String> commandList = new ArrayList<>();
        List<String> nameList = new ArrayList<>();
        for (Command commandClient : Command.values()) {
            if (!commandClient.isHide) {
                commandList.add(commandClient.getTitle());
                nameList.add(commandClient.getNameButton());
            }
        }
        return Pair.of(nameList, commandList);
    }
}
