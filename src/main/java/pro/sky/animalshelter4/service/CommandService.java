package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.Shelter;
import pro.sky.animalshelter4.model.Command;

import java.util.ArrayList;
import java.util.Arrays;
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

    private final ShelterService shelterService;
    private final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public CommandService(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    /**
     * The method checks whether the command is available to the {@link Chat}
     * for execution. The data for the solution is taken from {@link Command}.
     * The method must be executed after receiving and parsing the necessary data and before executing commands
     * using {@link ShelterService#isUserWithTelegramChatIdVolunteerInCurrentShelter(Chat)}
     * using {@link ShelterService#isUserWithTelegramChatIdOwnerInCurrentShelter(Chat)}
     *
     * @param command must be not null
     * @param chat    must be not null
     * @return true if the command is available to the user, else false
     */
    public boolean approveLaunchCommand(Command command, Chat chat) {
        if (shelterService.isUserWithTelegramChatIdVolunteerInCurrentShelter(chat)) {
            return command.isVolunteer();
        } else if (shelterService.isUserWithTelegramChatIdOwnerInCurrentShelter(chat)) {
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
     * using {@link ShelterService#isUserWithTelegramChatIdVolunteerInCurrentShelter(Chat)}
     * using {@link ShelterService#isUserWithTelegramChatIdOwnerInCurrentShelter(Chat)}
     *
     * @param chat must be not null
     * @return String as list of {@link Command#getTextCommand()}
     */
    public String getAllTitlesAsListExcludeHide(Chat chat) {
        if (shelterService.isUserWithTelegramChatIdVolunteerInCurrentShelter(chat)) {
            return getAllTextCommandAsListForVolunteerExcludeHide();
        } else if (shelterService.isUserWithTelegramChatIdOwnerInCurrentShelter(chat)) {
            return getAllTextCommandAsListForOwnerExcludeHide();
        } else {
            return getAllTextCommandAsListForClientExcludeHide();
        }
    }

    /**
     * The method outputs Pair<List<String>, List<String>>. First list contain names for the buttons,
     * second contain data for the buttons. <br>
     * For example, this use when sending buttons available to the user in the method
     * {@link TelegramBotSenderService#sendButtonsCommandForChat(Chat)}
     * if the id of the volunteer using
     * {@link CommandService#getListsNameButtonAndListsDataButtonForVolunteerExcludeHide(String, int)}
     * if else using {@link CommandService#getListsNameButtonAndListsDataButtonForClientExcludeHide(String, int)}
     * using {@link ShelterService#isUserWithTelegramChatIdVolunteerInCurrentShelter(Chat)}
     * using {@link ShelterService#isUserWithTelegramChatIdOwnerInCurrentShelter(Chat)}
     *
     * @param chat must be not null
     * @return Pair<List < nameButtons>, List<dataButtons>>
     */
    public Pair<List<String>, List<String>> getPairListsForButtonExcludeHide(Chat chat) {
        String shelterDesignation = chat.getShelter().getshelterDesignation();
        int indexMen = chat.getIndexMenu();
        if (shelterService.isUserWithTelegramChatIdVolunteerInCurrentShelter(chat)) {
            return getListsNameButtonAndListsDataButtonForVolunteerExcludeHide(shelterDesignation, indexMen);
        } else if (shelterService.isUserWithTelegramChatIdOwnerInCurrentShelter(chat)) {
            return getListsNameButtonAndListsDataButtonForOwnerExcludeHide(shelterDesignation, indexMen);
        } else {
            return getListsNameButtonAndListsDataButtonForClientExcludeHide(shelterDesignation, indexMen);
        }
    }


    /**
     * This method allow get all command as text for Client
     * using {@link Command#getOnlyShowCommandForClient()}
     *
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
     *
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
     *
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
     *
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
     *
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
     *
     * @return String Command
     */
    private List<String> getAllTextCommandForVolunteerExcludeHide() {
        return getOnlyShowCommandForVolunteer().stream().
                map(Command::getTextCommand).
                collect(Collectors.toList());
    }


    /**
     * This method allows you to get some buttons for the client and hide others
     *
     * @return Pair<List < String>, List<String>> for the client.
     * First list contains the names of the buttons {@link Command#getNameButton()},
     * second one contains data for buttons {@link Command#getTextCommand()}
     */
    private Pair<List<String>, List<String>> getListsNameButtonAndListsDataButtonForClientExcludeHide(
            String shelterDesignation,
            int indexMenu) {
        List<String> nameButton = new ArrayList<>();
        List<String> dataButton = new ArrayList<>();
        List<Command> commandList = getOnlyShowCommandForClient();
        for (int i = 0; i < commandList.size(); i++) {
            Command tCom = commandList.get(i);
            boolean contains = Arrays.asList(tCom.getShelterDesignation()).contains(shelterDesignation);
            if (tCom.getIndexMenu() == indexMenu && contains) {
                nameButton.add(tCom.getNameButton());
                dataButton.add(tCom.getTextCommand());
            }
        }
        return Pair.of(nameButton, dataButton);
    }

    /**
     * This method allows you to get some buttons for the owner and hide others
     *
     * @return Pair<List < String>, List<String>> for the owner.
     * First list contains the names of the buttons {@link Command#getNameButton()},
     * second one contains data for buttons {@link Command#getTextCommand()}
     */
    private Pair<List<String>, List<String>> getListsNameButtonAndListsDataButtonForOwnerExcludeHide(
            String shelterDesignation,
            int indexMenu) {
        List<String> nameButton = new ArrayList<>();
        List<String> dataButton = new ArrayList<>();
        List<Command> commandList = getOnlyShowCommandForOwner();
        for (int i = 0; i < commandList.size(); i++) {
            Command tCom = commandList.get(i);
            boolean contains = Arrays.asList(tCom.getShelterDesignation()).contains(shelterDesignation);
            if (tCom.getIndexMenu() == indexMenu && contains) {
                nameButton.add(tCom.getNameButton());
                dataButton.add(tCom.getTextCommand());
            }
        }
        return Pair.of(nameButton, dataButton);
    }

    /**
     * This method allows you to get some buttons for the owner and hide volunteer
     *
     * @return Pair<List < String>, List<String>> for the volunteer.
     * First list contains the names of the buttons {@link Command#getNameButton()},
     * second one contains data for buttons {@link Command#getTextCommand()}
     */
    private Pair<List<String>, List<String>> getListsNameButtonAndListsDataButtonForVolunteerExcludeHide(
            String shelterDesignation,
            int indexMenu) {
        List<String> nameButton = new ArrayList<>();
        List<String> dataButton = new ArrayList<>();
        List<Command> commandList = getOnlyShowCommandForVolunteer();
        for (int i = 0; i < commandList.size(); i++) {
            Command tCom = commandList.get(i);
            boolean contains = Arrays.asList(tCom.getShelterDesignation()).contains(shelterDesignation);
            if (tCom.getIndexMenu() == indexMenu && contains) {
                nameButton.add(tCom.getNameButton());
                dataButton.add(tCom.getTextCommand());
            }
        }
        return Pair.of(nameButton, dataButton);
    }
}
