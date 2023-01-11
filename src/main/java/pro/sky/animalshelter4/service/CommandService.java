package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.model.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pro.sky.animalshelter4.model.Command.*;

@Service
public class CommandService {

    ChatService chatService;
    private final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public CommandService(ChatService chatService) {
        this.chatService = chatService;
    }

    public boolean approveLaunchCommand(Command command, Long idChat) {
        if (chatService.isVolunteer(idChat)) {
            return command.isVolunteer();
        } else {
            return command.isClient();
        }
    }

    public String getAllTitlesAsListExcludeHide(Long idChat) {
        if (chatService.isVolunteer(idChat)) {
            return getAllTextCommandAsListForVolunteerExcludeHide();
        } else {
            return getAllTextCommandAsListForClientExcludeHide();
        }
    }

    public Pair<List<String>, List<String>> getPairListsForButtonExcludeHide(Long idChat) {
        if (chatService.isVolunteer(idChat)) {
            return getListsNameButtonAndListsDataButtonForVolunteerExcludeHide();
        } else {
            return getListsNameButtonAndListsDataButtonForClientExcludeHide();
        }
    }


    private static String getAllTextCommandAsListForClientExcludeHide() {
        StringBuilder sb = new StringBuilder();
        getOnlyShowCommandForClient().
                forEach(command -> {
                    sb.append(command.getTextCommand());
                    sb.append("\n");
                });
        return sb.toString();
    }

    private static String getAllTextCommandAsListForVolunteerExcludeHide() {
        StringBuilder sb = new StringBuilder();
        getOnlyShowCommandForVolunteer().
                forEach(command -> {
                    sb.append(command.getTextCommand());
                    sb.append("\n");
                });
        return sb.toString();
    }

    private static List<String> getAllTextCommandForClientExcludeHide() {
        return getOnlyShowCommandForClient().stream().
                map(Command::getTextCommand).
                collect(Collectors.toList());
    }

    private static List<String> getAllTextCommandForVolunteerExcludeHide() {
        return getOnlyShowCommandForVolunteer().stream().
                map(Command::getTextCommand).
                collect(Collectors.toList());
    }

    private static Pair<List<String>, List<String>> getListsNameButtonAndListsDataButtonForClientExcludeHide() {
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

    private static Pair<List<String>, List<String>> getListsNameButtonAndListsDataButtonForVolunteerExcludeHide() {
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
