package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.model.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pro.sky.animalshelter4.model.Command.*;

/**
 * The class contains methods for working with {@link Command}.
 * The logic of executing and issuing only commands available to {@link Chat}.
 * Depending on the {@link Chat#getId()} ()}
 */
@Service
public class CommandService {

    UserService userService;
    private final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public CommandService(UserService userService) {
        this.userService = userService;
    }

    /**
     * The method checks whether the command is available to the {@link Chat}
     * for execution. The data for the solution is taken from {@link Command}.
     * The method must be executed after receiving and parsing the necessary data and before executing commands
     * using {@link UserService#isUserWithTelegramChatIdVolunteer(Long)} (Long)}
     *
     * @param command must be not null
     * @param idChat  must be not null
     * @return true if the command is available to the user, else false
     */
    public boolean approveLaunchCommand(Command command, Long idChat) {
        if (userService.isUserWithTelegramChatIdVolunteer(idChat)) {
            return command.isVolunteer();
        } else if (userService.isUserWithTelegramChatIdOwner(idChat)) {
            return command.isOwner();
        } else {
            return command.isClient();
        }
    }

    /**
     * The method outputs a string consisting of {@link Command#getTextCommand()} in the form of a list,
     * depending on the user's rights <br>
     * if the id of the volunteer using {@link CommandService#getAllTextCommandAsListForVolunteerExcludeHide()}  <br>
     * if else using {@link CommandService#getAllTextCommandAsListForClientExcludeHide()}  <br>
     * using {@link UserService#isUserWithTelegramChatIdVolunteer(Long)}
     *
     * @param idChat must be not null
     * @return String as list of {@link Command#getTextCommand()}
     */
    public String getAllTitlesAsListExcludeHide(Long idChat) {
        if (userService.isUserWithTelegramChatIdVolunteer(idChat)) {
            return getAllTextCommandAsListForVolunteerExcludeHide();
        } else if (userService.isUserWithTelegramChatIdOwner(idChat)) {
            return getAllTextCommandAsListForOwnerExcludeHide();
        } else {
            return getAllTextCommandAsListForClientExcludeHide();
        }
    }

    /**
     * The method outputs Pair<List<String>, List<String>>. First list contain names for the buttons,
     * second contain data for the buttons. <br>
     * For example, this use when sending buttons available to the user in the method
     * {@link TelegramBotSenderService#sendButtonsCommandForChat(Long)} <br>
     * if the id of the volunteer using
     * {@link CommandService#getListsNameButtonAndListsDataButtonForVolunteerExcludeHide()}  <br>
     * if else using {@link CommandService#getListsNameButtonAndListsDataButtonForClientExcludeHide()}  <br>
     * using {@link UserService#isUserWithTelegramChatIdVolunteer(Long)}
     *
     * @param idChat must be not null
     * @return Pair<List < nameButtons>, List<dataButtons>>
     */
    public Pair<List<String>, List<String>> getPairListsForButtonExcludeHide(Long idChat) {
        if (userService.isUserWithTelegramChatIdVolunteer(idChat)) {
            return getListsNameButtonAndListsDataButtonForVolunteerExcludeHide();
        } else if (userService.isUserWithTelegramChatIdOwner(idChat)) {
            return getListsNameButtonAndListsDataButtonForOwnerExcludeHide();
        } else {
            return getListsNameButtonAndListsDataButtonForClientExcludeHide();
        }
    }


    /**
     * This method allow get all command as text for Client
     * using {@link Command#getOnlyShowCommandForClient()}
     * @return String Command
     */
    private String getAllTextCommandAsListForClientExcludeHide() {
        StringBuilder sb = new StringBuilder();
        getOnlyShowCommandForClient().
                forEach(command -> {
                    sb.append(command.getTextCommand());
                    sb.append("\n");
                });
        return sb.toString();
    }

    /**
     * This method allow get all command as text for Owner
     * using {@link Command#getOnlyShowCommandForOwner()}
     * @return String Command
     */
    private String getAllTextCommandAsListForOwnerExcludeHide() {
        StringBuilder sb = new StringBuilder();
        getOnlyShowCommandForOwner().
                forEach(command -> {
                    sb.append(command.getTextCommand());
                    sb.append("\n");
                });
        return sb.toString();
    }

    /**
     * This method allow get all command as text for Volunteer
     * using {@link Command#getOnlyShowCommandForVolunteer()}
     * @return String Command
     */
    private String getAllTextCommandAsListForVolunteerExcludeHide() {
        StringBuilder sb = new StringBuilder();
        getOnlyShowCommandForVolunteer().
                forEach(command -> {
                    sb.append(command.getTextCommand());
                    sb.append("\n");
                });
        return sb.toString();
    }


    /**
     * This method allow get all command as text for Client
     * using {@link Command#getOnlyShowCommandForClient()}
     * @return String Command
     */
    private List<String> getAllTextCommandForClientExcludeHide() {
        return getOnlyShowCommandForClient().stream().
                map(Command::getTextCommand).
                collect(Collectors.toList());
    }

    /**
     * This method allow get all command as text for Owner
     * using {@link Command#getOnlyShowCommandForOwner()}
     * @return String Command
     */
    private List<String> getAllTextCommandForOwnerExcludeHide() {
        return getOnlyShowCommandForOwner().stream().
                map(Command::getTextCommand).
                collect(Collectors.toList());
    }

    /**
     * This method allow get all command as text for Volunteer
     * using {@link Command#getOnlyShowCommandForVolunteer()}
     * @return String Command
     */
    private List<String> getAllTextCommandForVolunteerExcludeHide() {
        return getOnlyShowCommandForVolunteer().stream().
                map(Command::getTextCommand).
                collect(Collectors.toList());
    }


    /**
     * This method allows you to get some buttons for the client and hide others
     * @return Pair<List < String>, List<String>> for the client.
     * First list contains the names of the buttons {@link Command#getNameButton()},
     * second one contains data for buttons {@link Command#getTextCommand()}
     */
    private Pair<List<String>, List<String>> getListsNameButtonAndListsDataButtonForClientExcludeHide() {
        List<String> nameButton = new ArrayList<>();
        List<String> dataButton = new ArrayList<>();
        List<Command> commandList = getOnlyShowCommandForClient();
        for (int i = 0; i < commandList.size(); i++) {
            Command tCom = commandList.get(i);
            nameButton.add(tCom.getNameButton());
            dataButton.add(tCom.getTextCommand());
        }
        return Pair.of(nameButton, dataButton);
    }

    /**
     * This method allows you to get some buttons for the owner and hide others
     * @return Pair<List < String>, List<String>> for the owner.
     * First list contains the names of the buttons {@link Command#getNameButton()},
     * second one contains data for buttons {@link Command#getTextCommand()}
     */
    private Pair<List<String>, List<String>> getListsNameButtonAndListsDataButtonForOwnerExcludeHide() {
        List<String> nameButton = new ArrayList<>();
        List<String> dataButton = new ArrayList<>();
        List<Command> commandList = getOnlyShowCommandForOwner();
        for (int i = 0; i < commandList.size(); i++) {
            Command tCom = commandList.get(i);
            nameButton.add(tCom.getNameButton());
            dataButton.add(tCom.getTextCommand());
        }
        return Pair.of(nameButton, dataButton);
    }

    /**
     * This method allows you to get some buttons for the owner and hide volunteer
     * @return Pair<List < String>, List<String>> for the volunteer.
     * First list contains the names of the buttons {@link Command#getNameButton()},
     * second one contains data for buttons {@link Command#getTextCommand()}
     */
    private Pair<List<String>, List<String>> getListsNameButtonAndListsDataButtonForVolunteerExcludeHide() {
        List<String> nameButton = new ArrayList<>();
        List<String> dataButton = new ArrayList<>();
        List<Command> commandList = getOnlyShowCommandForVolunteer();
        for (int i = 0; i < commandList.size(); i++) {
            Command tCom = commandList.get(i);
            nameButton.add(tCom.getNameButton());
            dataButton.add(tCom.getTextCommand());
        }
        return Pair.of(nameButton, dataButton);
    }
}
