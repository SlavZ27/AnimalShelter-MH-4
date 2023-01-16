package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entityDto.ChatDto;
import pro.sky.animalshelter4.exception.*;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.UpdateDPO;
import pro.sky.animalshelter4.repository.ChatRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class is designed for the operation of the Call Request Service class
 * An important parameter that affects the operation of the CALL_REQUEST CALL_CLIENT command.
 */
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final DtoMapperService dtoMapperService;
    private final TelegramUnfinishedRequestService telegramUnfinishedRequestService;
    private final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final TelegramBotSenderService telegramBotSenderService;
    private final TelegramBotContentSaverService telegramBotContentSaverService;
    private final TelegramMapperService telegramMapperService;
    private final UserService userService;

    public ChatService(ChatRepository chatRepository, DtoMapperService dtoMapperService, TelegramUnfinishedRequestService telegramUnfinishedRequestService, TelegramBotSenderService telegramBotSenderService, TelegramBotContentSaverService telegramBotContentSaverService, TelegramMapperService telegramMapperService, UserService userService) {
        this.chatRepository = chatRepository;
        this.dtoMapperService = dtoMapperService;
        this.telegramUnfinishedRequestService = telegramUnfinishedRequestService;
        this.telegramBotSenderService = telegramBotSenderService;
        this.telegramBotContentSaverService = telegramBotContentSaverService;
        this.telegramMapperService = telegramMapperService;
        this.userService = userService;
    }

    public Chat getChatByIdOrNew(UpdateDPO updateDPO) {
        logger.info("Method getChatByIdOrNew was start for find Chat by id = {}, or return new Chat",
                updateDPO.getIdChat());
        Chat chat = getChatByIdOrNew(updateDPO.getIdChat());
        chat.setUserNameTelegram(updateDPO.getUserName());
        chat.setFirstNameUser(updateDPO.getFirstName());
        chat.setLastNameUser(updateDPO.getLastName());
        chat.setLast_activity(LocalDateTime.now());
        return addChat(chat);
    }

    /**
     * The method searches for and returns an existing {@link Chat} instance,
     * if it does not exist, then creates a new one with the necessary parameters
     *
     * @param id must be not null
     * @return {@link Chat} new or found
     */
    public Chat getChatByIdOrNew(Long id) {
        logger.info("Method getChatByIdOrNew was start for find Chat by id = {}, or return new Chat", id);
        Chat chat = chatRepository.getChatById(id);
        if (chat == null) {
            logger.debug("Method getChatByIdOrNew will return the new chat");
            chat = new Chat();
            chat.setId(id);
        }
        logger.debug("Method getChatByIdOrNew will return the found chat");
        return chatRepository.save(chat);
    }

    /**
     * The method adds a new chat to the repository and returns the same instance
     * using {@link ChatRepository#save(Object)}
     *
     * @param chatDto id of chat must be not null
     * @return {@link Chat}
     */
    public ChatDto createChat(ChatDto chatDto) {
        logger.info("Method createChat was start for create new Chat");
        return dtoMapperService.toDto(chatRepository.save(dtoMapperService.toEntity(chatDto)));
    }

    public Chat addChat(Chat chat) {
        logger.info("Method createChat was start for create new Chat");
        return chatRepository.save(chat);
    }

    /**
     * The method searches for a chat in the repository by id of chat
     * using {@link ChatRepository#getChatById(Long)}
     *
     * @param id
     * @return found chat
     */
    public ChatDto readChat(Long id) {
        logger.info("Method readChat was start for find Chat by id");
        return dtoMapperService.toDto(
                chatRepository.findById(id).
                        orElseThrow(() -> new ChatNotFoundException(String.valueOf(id))));
    }

    public Chat findChat(Long id) {
        logger.info("Method readChat was start for find Chat by id");
        return chatRepository.findById(id).
                orElseThrow(() -> new ChatNotFoundException(String.valueOf(id)));
    }

    public ChatDto updateChat(ChatDto chatDto) {
        logger.info("Method updateChat was start for update Chat");
        Chat newChat = dtoMapperService.toEntity(chatDto);
        Chat oldChat = findChat(newChat.getId());
        if (oldChat == null) {
            throw new ChatNotFoundException(String.valueOf(newChat.getId()));
        }
        oldChat.setFirstNameUser(newChat.getFirstNameUser());
        oldChat.setLastNameUser(newChat.getLastNameUser());
        oldChat.setUserNameTelegram(newChat.getUserNameTelegram());
        oldChat.setLast_activity(newChat.getLast_activity());
        return dtoMapperService.toDto(chatRepository.save(oldChat));
    }

    /**
     * The method delete chat from repository
     * using {@link ChatService#deleteChat(Chat)}
     *
     * @param id must be not null
     */
    public ChatDto deleteChat(Long id) {
        Chat chat = new Chat();
        chat.setId(id);
        return dtoMapperService.toDto(deleteChat(chat));
    }

    /**
     * The method delete chat from repository
     * using {@link ChatRepository#delete(Object)}
     *
     * @param chat
     */
    public Chat deleteChat(Chat chat) {
        logger.info("Method deleteChat was start for delete Chat");
        if (chat.getId() == null) {
            throw new IllegalArgumentException("Incorrect id chat");
        }
        Chat chatFound = chatRepository.findById(chat.getId()).
                orElseThrow(() -> new ChatNotFoundException(String.valueOf(chat.getId())));
        chatRepository.delete(chatFound);
        return chatFound;
    }

    public List<ChatDto> getAll() {
        logger.info("Method getAll was start for return all Chats");
        return chatRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public void startChangePhoneByUserFromTelegram(Chat chat) {
        telegramUnfinishedRequestService.addUnfinishedRequestForChat(chat, Command.CHANGE_PHONE);
        telegramBotSenderService.sendIDontKnowYourPhoneWriteIt(chat.getId());
    }

    public void changePhoneUser(Chat chat, String phone) {
        logger.info("Method tryChangePhoneFromTelegram was start for change phone by User with chat id = {}",
                chat.getId());
        try {
            userService.changePhone(userService.getUserWithTelegramUserId(chat.getId()), phone);
        } catch (BadPhoneNumber e) {
            telegramBotSenderService.sendMessageWithButtonCancel(
                    chat.getId(),
                    UserService.MESSAGE_BAD_PHONE,
                    TelegramBotSenderService.NAME_BUTTON_FOR_CANCEL);
            return;
        }
        telegramUnfinishedRequestService.delUnfinishedRequestForChat(chat);
        telegramBotSenderService.sendButtonsCommandForChat(chat.getId());
    }

    public void savePhoto(Update update) {
        try {
            telegramBotContentSaverService.savePhoto(update);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Command getUnfinishedRequestForChat(UpdateDPO updateDpo) {
        return telegramUnfinishedRequestService.
                findUnfinishedRequestForChat(getChatByIdOrNew(updateDpo));
    }

    public void sendSorryIKnowThis(Long idChat) {
        telegramBotSenderService.sendSorryIKnowThis(idChat);
    }

    public void sendUnknownProcess(Long idChat) {
        telegramBotSenderService.sendUnknownProcess(idChat);
        telegramBotSenderService.sendButtonsCommandForChat(idChat);
    }

    public void sendHello(Long idChat, String name) {
        telegramBotSenderService.sendHello(idChat, name);
    }

    public void sendInfoAboutShelter(Long idChat) {
        telegramBotSenderService.sendInfoAboutShelter(idChat);
        telegramBotSenderService.sendButtonsCommandForChat(idChat);
    }

    public void sendHowTakeDog(Long idChat) {
        telegramBotSenderService.sendHowTakeDog(idChat);
        telegramBotSenderService.sendButtonsCommandForChat(idChat);
    }

    public void createCallRequest(UpdateDPO updateDPO) {
        Chat chatClient = getChatByIdOrNew(updateDPO);
        CallRequest callRequest = new CallRequest();
        try {
            callRequest = userService.createCallRequest(chatClient);
        } catch (VolunteersIsAbsentException e) {
            telegramBotSenderService.sendMessage(chatClient.getId(), UserService.MESSAGE_VOLUNTEERS_IS_ABSENT);
            return;
        }
        telegramBotSenderService.sendMessage(chatClient.getId(), CallRequestService.MESSAGE_SUCCESSFUL_CREATION);
        if (callRequest != null &&
                (callRequest.getClient().getPhone() == null || callRequest.getClient().getPhone().length() == 0)) {
            startChangePhoneByUserFromTelegram(chatClient);
        } else {
            telegramBotSenderService.sendButtonsCommandForChat(chatClient.getId());
        }
        if (
                callRequest != null &&
                        callRequest.getVolunteer() != null &&
                        callRequest.getVolunteer().getChatTelegram() != null) {
            sendNotificationAboutCallRequestsToTelegramVolunteer(callRequest.getVolunteer().getChatTelegram(), false);
        }
    }

    public void sendNotificationAboutCallRequestsToTelegramVolunteer(UpdateDPO updateDPO) {
        Chat chatVolunteer = getChatByIdOrNew(updateDPO);
        sendNotificationAboutCallRequestsToTelegramVolunteer(chatVolunteer, true);
        telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
    }

    private void sendNotificationAboutCallRequestsToTelegramVolunteer(Chat chatVolunteer, boolean requiredResponse) {
        List<CallRequest> callRequestList = userService.getListOpenCallRequests(chatVolunteer);

        List<String> nameButtons = new ArrayList<>();
        List<String> dataButtons = new ArrayList<>();

        if (callRequestList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            callRequestList.forEach(callRequest -> {
                nameButtons.add("Close " + callRequest.getId());
                dataButtons.add(callRequest.getId().toString());
                sb.append(CallRequestService.MESSAGE_YOU_HAVE_CALL_REQUEST);
                sb.append("\n");
                sb.append(callRequest.getId());
                sb.append(" ");
                sb.append(callRequest.getClient().getChatTelegram().getFirstNameUser());
                sb.append(" ");
                sb.append(callRequest.getClient().getChatTelegram().getLastNameUser());
                sb.append(" @");
                sb.append(callRequest.getClient().getChatTelegram().getUserNameTelegram());
                if (callRequest.getClient().getPhone() != null &&
                        callRequest.getClient().getPhone().length() > 0) {
                    sb.append(" ");
                    sb.append(callRequest.getClient().getPhone());
                }
                sb.append("\n");
            });
            Pair<Integer, Integer> widthAndHeight = telegramBotSenderService.getTableSize(nameButtons.size());
            telegramBotSenderService.sendMessage(chatVolunteer.getId(), sb.toString());
            telegramBotSenderService.sendButtonsWithOneData(
                    chatVolunteer.getId(),
                    CallRequestService.MESSAGE_YOU_CAN_CLOSE_CALL_REQUEST,
                    Command.CLOSE_CALL_REQUEST.getTextCommand(),
                    nameButtons,
                    dataButtons,
                    widthAndHeight.getFirst(),
                    widthAndHeight.getSecond()
            );
        } else if (requiredResponse) {
            telegramBotSenderService.sendMessage(
                    chatVolunteer.getId(),
                    CallRequestService.MESSAGE_YOU_DONT_HAVE_CALL_REQUEST
            );
        }
    }

    public void closeCallRequest(UpdateDPO updateDPO) {
        Chat chatVolunteer = getChatByIdOrNew(updateDPO);
        try {
            userService.closeCallRequest(chatVolunteer, telegramMapperService.mapStringToLong(updateDPO.getMessage()));
        } catch (CallRequestNotFoundException e) {
            telegramBotSenderService.sendMessage(
                    chatVolunteer.getId(),
                    CallRequestService.MESSAGE_CALL_REQUEST_NOT_FOUND);
            return;
        } catch (CantCloseCallRequestException e) {
            telegramBotSenderService.sendMessage(
                    chatVolunteer.getId(),
                    CallRequestService.MESSAGE_YOU_CANT_CLOSE_CALL_REQUEST);
            return;
        }
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), CallRequestService.MESSAGE_CALL_REQUEST_IS_CLOSE);
    }

    public void closeUnfinishedRequest(UpdateDPO updateDPO) {
        Chat chat = getChatByIdOrNew(updateDPO.getIdChat());
        telegramUnfinishedRequestService.delUnfinishedRequestForChat(chat);
        telegramBotSenderService.sendButtonsCommandForChat(chat.getId());
    }



}
