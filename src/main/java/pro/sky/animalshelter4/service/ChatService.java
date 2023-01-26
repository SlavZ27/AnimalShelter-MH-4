package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.*;
import pro.sky.animalshelter4.entityDto.ChatDto;
import pro.sky.animalshelter4.exception.*;
import pro.sky.animalshelter4.info.*;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.UpdateDPO;
import pro.sky.animalshelter4.repository.ChatRepository;

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
    private final TelegramMapperService telegramMapperService;
    private final AnimalService animalService;
    private final UserService userService;

    public ChatService(ChatRepository chatRepository, DtoMapperService dtoMapperService, TelegramUnfinishedRequestService telegramUnfinishedRequestService, TelegramBotSenderService telegramBotSenderService, TelegramMapperService telegramMapperService, AnimalService animalService, UserService userService) {
        this.chatRepository = chatRepository;
        this.dtoMapperService = dtoMapperService;
        this.telegramUnfinishedRequestService = telegramUnfinishedRequestService;
        this.telegramBotSenderService = telegramBotSenderService;
        this.telegramMapperService = telegramMapperService;
        this.animalService = animalService;
        this.userService = userService;
    }


    /**
     * The method performs a search and returns an existing chat
     *
     * @param updateDPO is not null
     * @return chat
     */
    public Chat getChatByIdOrNew(UpdateDPO updateDPO) {
        logger.info("Method getChatByIdOrNew was start for find Chat by id = {}, or return new Chat",
                updateDPO.getIdChat());
        Chat chat = getChatByIdOrNew(updateDPO.getIdChat());
        chat.setUserNameTelegram(updateDPO.getUserName());
        chat.setFirstNameUser(updateDPO.getFirstName());
        chat.setLastNameUser(updateDPO.getLastName());
        chat.setLastActivity(LocalDateTime.now());
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

    /**
     * The method adds a new chat to the repository and returns the same instance
     * using {@link ChatRepository#save(Object)}
     *
     * @param chat id of chat must be not null
     * @return {@link Chat}
     */
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

    /**
     * The method searches for a chat in the repository by id of chat
     * using {@link ChatRepository#getChatById(Long)}
     *
     * @param id
     * @return found chat
     */
    public Chat findChat(Long id) {
        logger.info("Method readChat was start for find Chat by id");
        return chatRepository.findById(id).
                orElseThrow(() -> new ChatNotFoundException(String.valueOf(id)));
    }

    /**
     * The method update  a new chat to the repository and returns the same instance
     * using {@link ChatRepository#save(Object)}
     *
     * @param chatDto id of chat must be not null
     * @return {@link Chat}
     */
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
        oldChat.setLastActivity(newChat.getLastActivity());
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

    /**
     * The all method outputs the chat from the database using the repository
     * using {@link ChatRepository#findAll()}
     *
     * @return full user
     */
    public List<ChatDto> getAll() {
        logger.info("Method getAll was start for return all Chats");
        return chatRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    /**
     * The method allows the user to create their phone number and add it to telegram
     * using {@link TelegramUnfinishedRequestService#addUnfinishedRequestForChat(Chat, Command)}
     * using {@link TelegramBotSenderService#sendIDontKnowYourPhoneWriteIt(Long)}
     *
     * @param chat is not by null
     */
    public void startChangePhoneByUserFromTelegram(Chat chat) {
        telegramUnfinishedRequestService.addUnfinishedRequestForChat(chat, Command.CHANGE_PHONE);
        telegramBotSenderService.sendIDontKnowYourPhoneWriteIt(chat.getId());
    }

    /**
     * This method allows the user change phone number
     * using {@link TelegramBotSenderService#sendMessageWithButtonCancel(Long, String, String)}
     * using {@link TelegramUnfinishedRequestService#delUnfinishedRequestForChat(Chat)}
     * using {@link TelegramBotSenderService#sendButtonsCommandForChat(Long)}
     *
     * @param updateDPO is not by null
     */
    public void changePhoneUser(UpdateDPO updateDPO) {
        Chat chat = getChatByIdOrNew(updateDPO.getIdChat());
        String phone = updateDPO.getMessage();
        logger.info("Method tryChangePhoneFromTelegram was start for change phone by User with chat id = {}",
                chat.getId());
        try {
            userService.changePhone(chat, phone);
        } catch (BadPhoneNumberException e) {
            telegramBotSenderService.sendMessageWithButtonCancel(
                    chat.getId(),
                    UserService.MESSAGE_BAD_PHONE,
                    TelegramBotSenderService.NAME_BUTTON_FOR_CANCEL);
            return;
        }
        telegramUnfinishedRequestService.delUnfinishedRequestForChat(chat);
        telegramBotSenderService.sendMessage(chat.getId(), UserService.MESSAGE_PHONE_IS_OK);
        telegramBotSenderService.sendButtonsCommandForChat(chat.getId());
    }


    /**
     * This method allows unfinished request for chat by updateDpo
     *
     * @param updateDpo is not null
     *                  using {@link TelegramUnfinishedRequestService#findUnfinishedRequestForChat(Chat)}
     * @return unfinished request
     */
    public Command getUnfinishedRequestForChat(UpdateDPO updateDpo) {
        return telegramUnfinishedRequestService.
                findUnfinishedRequestForChat(getChatByIdOrNew(updateDpo));
    }

    /**
     * This method allows send message in chat user on id chat Sorry I Know This
     *
     * @param idChat is not by null
     */
    public void sendSorryIKnowThis(Long idChat) {
        telegramBotSenderService.sendSorryIKnowThis(idChat);
    }


    /**
     * this method uses the send button command for the chat process
     *
     * @param idChat is not null
     */
    public void sendUnknownProcess(Long idChat) {
        telegramBotSenderService.sendUnknownProcess(idChat);
        telegramBotSenderService.sendButtonsCommandForChat(idChat);
    }

    /**
     * This method send hello message
     *
     * @param idChat is not null
     * @param name   is not null
     */
    public void sendHello(Long idChat, String name) {
        telegramBotSenderService.sendHello(idChat, name);
    }

    /**
     * This method send Info About Shelter message
     *
     * @param idChat is not null
     */
    public void sendInfoAboutShelter(Long idChat) {
        telegramBotSenderService.sendInfoAboutShelter(idChat);
        telegramBotSenderService.sendButtonsCommandForChat(idChat);
    }

    /**
     * This method send How Take Dog
     *
     * @param idChat is not null
     */
    public void sendHowTakeDog(Long idChat) {
        telegramBotSenderService.sendHowTakeDog(idChat);
        telegramBotSenderService.sendButtonsCommandForChat(idChat);
    }


    /**
     * This method processes the call request command thereby creating a call request
     * Using{@link UserService#createCallRequest(Chat)}
     * Using{@link TelegramBotSenderService#sendMessage(Long, String)}
     * Using{@link TelegramBotSenderService#sendButtonsCommandForChat(Long)}
     *
     * @param updateDPO is not null
     */
    public void createCallRequest(UpdateDPO updateDPO) {
        Chat chatClient = getChatByIdOrNew(updateDPO);
        CallRequest callRequest;
        try {
            callRequest = userService.createCallRequest(chatClient);
        } catch (VolunteersIsAbsentException e) {
            telegramBotSenderService.sendMessage(chatClient.getId(), UserService.MESSAGE_VOLUNTEERS_IS_ABSENT);
            telegramBotSenderService.sendButtonsCommandForChat(chatClient.getId());
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

    /**
     * This method sends a call request to a volunteer
     * Using{@link TelegramBotSenderService#sendButtonsWithDifferentData}
     *
     * @param updateDPO is not null
     */
    public void sendNotificationAboutCallRequestsToTelegramVolunteer(UpdateDPO updateDPO) {
        Chat chatVolunteer = getChatByIdOrNew(updateDPO);
        sendNotificationAboutCallRequestsToTelegramVolunteer(chatVolunteer, true);
        telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
    }

    /**
     * This method processes the call request command and prompts the client to fill out his phone
     * Using{@link TelegramBotSenderService#sendMessage}
     * Using{@link TelegramBotSenderService#sendButtonsWithDifferentData}
     *
     * @param chatVolunteer    is not null
     * @param requiredResponse MESSAGE_YOU_DONT_HAVE_CALL_REQUEST
     */
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

    /**
     * This method close call request
     * Using{@link UserService#closeCallRequest(Chat, Long)}
     * Using{@link TelegramMapperService#mapStringToLong(String)}
     * Using{@link TelegramBotSenderService#sendMessage(Long, String)}
     *
     * @param updateDPO is not null
     * @Exception : CallRequestNotFoundException, CantCloseCallRequestException
     */
    public void closeCallRequest(UpdateDPO updateDPO) {
        Chat chatVolunteer = getChatByIdOrNew(updateDPO);
        try {
            userService.closeCallRequest(chatVolunteer, telegramMapperService.mapStringToLong(updateDPO.getMessage()));
        } catch (CallRequestNotFoundException e) {
            telegramBotSenderService.sendMessage(
                    chatVolunteer.getId(),
                    CallRequestService.MESSAGE_CALL_REQUEST_NOT_FOUND);
            telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
            return;
        } catch (CantCloseCallRequestException e) {
            telegramBotSenderService.sendMessage(
                    chatVolunteer.getId(),
                    CallRequestService.MESSAGE_YOU_CANT_CLOSE_CALL_REQUEST);
            telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
            return;
        }
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), CallRequestService.MESSAGE_CALL_REQUEST_IS_CLOSE);
        telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
    }

    /**
     * This method close unfinished request using method class
     * Using{@link TelegramUnfinishedRequestService#delUnfinishedRequestForChat(Chat)}
     * Using{@link TelegramBotSenderService#sendButtonsCommandForChat(Long)}
     *
     * @param updateDPO is not null
     */
    public void closeUnfinishedRequest(UpdateDPO updateDPO) {
        Chat chat = getChatByIdOrNew(updateDPO.getIdChat());
        telegramUnfinishedRequestService.delUnfinishedRequestForChat(chat);
        telegramBotSenderService.sendButtonsCommandForChat(chat.getId());
    }

    /**
     * This method creates an owner by letting him choose a pet for temporary maintenance
     * Using{@link UserService#getAllClientsEntity()}
     * Using{@link AnimalService#getAllNotBusyAnimals()}
     * Using{@link TelegramBotSenderService#sendMessage(Long, String)}
     *
     * @param updateDPO is not null
     */
    public void createOwn(UpdateDPO updateDPO) {
        Chat chatVolunteer = getChatByIdOrNew(updateDPO.getIdChat());
        String message = updateDPO.getMessage();
        List<User> clientList = userService.getAllClientsEntity();
        List<Animal> animalList = animalService.getAllNotBusyAnimals();
        if (clientList.size() == 0) {
            telegramBotSenderService.sendMessage(chatVolunteer.getId(), UserService.MESSAGE_CLIENTS_IS_ABSENT);
            return;
        }
        if (animalList.size() == 0) {
            telegramBotSenderService.sendMessage(chatVolunteer.getId(), AnimalService.MESSAGE_ANIMALS_IS_ABSENT);
            return;
        }

        String[] messageMas = null;

        if (message == null || message.length() == 0) {
            //send List of Users
            startCreateOwnSendListClients(chatVolunteer, clientList);
            return;
        }
        messageMas = message.split(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);


        switch (messageMas.length) {
            case 1:
                //send list of animals
                Long idUserClient = telegramMapperService.mapStringToLong(messageMas[messageMas.length - 1]);
                continueCreateOwnSendListAnimals(chatVolunteer, idUserClient, animalList);
                break;
            case 2:
                //finish request
                idUserClient = telegramMapperService.mapStringToLong(messageMas[messageMas.length - 2]);
                Long idAnimal = telegramMapperService.mapStringToLong(messageMas[messageMas.length - 1]);
                AnimalOwnership ownership;
                try {
                    ownership = userService.createOwnershipAnimal(idUserClient, idAnimal);
                } catch (AnimalNotFoundException e) {
                    telegramBotSenderService.sendMessage(chatVolunteer.getId(), AnimalService.MESSAGE_ANIMAL_NOT_FOUND);
                    return;
                } catch (UserNotFoundException e) {
                    telegramBotSenderService.sendMessage(chatVolunteer.getId(), UserService.MESSAGE_CLIENT_NOT_FOUND);
                    return;
                }
                telegramBotSenderService.sendMessage(chatVolunteer.getId(),
                        AnimalOwnershipService.MESSAGE_SUCCESSFUL_CREATION + " " +
                                ownership.getOwner().getNameUser() + " is now the owner of " +
                                ownership.getAnimal().getNameAnimal() + " starting  " +
                                ownership.getDateStartOwn().toString() + " Probation period up to " +
                                ownership.getDateEndTrial().toString());
                telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
                if (ownership.getOwner() != null &&
                        ownership.getOwner().getChatTelegram() != null &&
                        ownership.getOwner().getChatTelegram().getId() != null) {
                    telegramBotSenderService.sendMessage(ownership.getOwner().getChatTelegram().getId(),
                            AnimalOwnershipService.MESSAGE_SUCCESSFUL_CREATION +
                                    "You is now the owner of " +
                                    ownership.getAnimal().getNameAnimal() + " starting  " +
                                    ownership.getDateStartOwn().toString() + " Probation period up to " +
                                    ownership.getDateEndTrial().toString());
                }
        }
    }

    /**
     * This method sends a list of clients who want to take animals
     * Using{@link TelegramBotSenderService#sendButtonsWithOneData} ()}
     *
     * @param chatVolunteer is not null
     * @param clientList    is not null
     */
    private void startCreateOwnSendListClients(Chat chatVolunteer, List<User> clientList) {
        List<String> nameButtons = clientList.stream().
                map(user -> user.getId() + " " + user.getNameUser())
                .collect(Collectors.toList());
        List<String> dataButtons = clientList.stream().
                map(user -> user.getId().toString())
                .collect(Collectors.toList());
        telegramBotSenderService.sendButtonsWithOneData(
                chatVolunteer.getId(),
                UserService.CAPTION_SELECT_USER,
                Command.CREATE_OWNERSHIP.getTextCommand(),
                nameButtons,
                dataButtons,
                1,
                dataButtons.size());
    }

    /**
     * This method continues to create a list of animals that are at OWN at the temporary stage of maintenance
     * Using{@link TelegramBotSenderService#sendButtonsWithOneData}
     *
     * @param chatVolunteer is not null
     * @param idUserClient  is not null
     * @param animalList    is not null
     */
    private void continueCreateOwnSendListAnimals(Chat chatVolunteer, Long idUserClient, List<Animal> animalList) {
        List<String> nameButtons = animalList.stream().
                map(animal -> animal.getId() +
                        TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animal.getAnimalType().getTypeAnimal() +
                        TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animal.getNameAnimal()
                ).
                collect(Collectors.toList());
        List<String> dataButtons = animalList.stream().
                map(animal ->
                        idUserClient +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animal.getId())
                .collect(Collectors.toList());
        telegramBotSenderService.sendButtonsWithOneData(
                chatVolunteer.getId(),
                AnimalService.CAPTION_SELECT_ANIMAL,
                Command.CREATE_OWNERSHIP.getTextCommand(),
                nameButtons,
                dataButtons,
                1,
                dataButtons.size());
    }


    /**
     * This method crate new animal and their description
     *
     * @param updateDpo is not null
     */
    public void addAnimal(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        String message = updateDpo.getMessage();
        //send invitation name
        if (message == null || message.length() == 0) {
            telegramUnfinishedRequestService.addUnfinishedRequestForChat(
                    chatVolunteer,
                    Command.ADD_ANIMAL);
            telegramBotSenderService.sendMessageWithButtonCancel(
                    chatVolunteer.getId(),
                    AnimalService.CAPTION_WRITE_NAME_OF_ANIMAL,
                    TelegramBotSenderService.NAME_BUTTON_FOR_CANCEL);
        } else {
            Animal animal = new Animal();
            animal.setNameAnimal(message);
            animalService.addAnimal(animal);
            telegramUnfinishedRequestService.delUnfinishedRequestForChat(chatVolunteer);
            telegramBotSenderService.sendMessage(chatVolunteer.getId(),
                    AnimalService.MESSAGE_ANIMAL_CREATED + " " + animal.getNameAnimal());
            answerQuestionAboutAnimals(chatVolunteer, false);
            telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
        }
    }

    /**
     * This method allow complement their description animal
     *
     * @param updateDPO is not null
     */
    public void complementAnimal(UpdateDPO updateDPO) {
        Chat chatVolunteer = getChatByIdOrNew(updateDPO.getIdChat());
        String message = updateDPO.getMessage();

        String[] messageMas = null;

        if (message == null || message.length() == 0) {
            answerQuestionAboutAnimals(chatVolunteer, true);
        }
        messageMas = message.split(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
        switch (messageMas.length) {
            case 2:
                Long idAnimal = telegramMapperService.mapStringToLong(messageMas[messageMas.length - 2]);
                Long idTypeAnimal = telegramMapperService.mapStringToLong(messageMas[messageMas.length - 1]);
                Animal animal = animalService.updateAnimal(idAnimal, idTypeAnimal);
                if (animal.getAnimalType().getId().equals(idTypeAnimal)) {
                    telegramBotSenderService.sendMessage(chatVolunteer.getId(), AnimalService.MESSAGE_ANIMAL_UPDATED +
                            " " + animal.toString());
                }
                answerQuestionAboutAnimals(chatVolunteer, true);
                break;
        }
    }

    /**
     * This method answer question about animal
     *
     * @param chatVolunteer    is not null
     * @param requiredResponse is not null
     */
    private void answerQuestionAboutAnimals(Chat chatVolunteer, boolean requiredResponse) {
        Animal animal = animalService.getNotComplement();
        if (animal == null) {
            if (requiredResponse) {
                telegramBotSenderService.sendMessage(
                        chatVolunteer.getId(), AnimalService.MESSAGE_ALL_ANIMAL_COMPLEMENT);
                telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
            }
            return;
        }
        if (animal.getAnimalType() == null) {
            sendAnimalTypeButtons(chatVolunteer, animal);
            return;
        }
    }


    /**
     * This method describes sending buttons for different types of animals
     *
     * @param chatVolunteer is not null
     * @param animal        is not null
     */
    private void sendAnimalTypeButtons(Chat chatVolunteer, Animal animal) {
        List<AnimalType> animalTypeList = animalService.getAllAnimalType();
        List<String> nameButtons = animalTypeList.stream().
                map(AnimalType::getTypeAnimal).
                collect(Collectors.toList());
        List<String> dataButtons = animalTypeList.stream().
                map(animalType ->
                        animal.getId() + TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                animalType.getId()).
                collect(Collectors.toList());
        telegramBotSenderService.sendButtonsWithOneData(
                chatVolunteer.getId(),
                AnimalService.CAPTION_SELECT_TYPE_OF_ANIMAL + " '" + animal.getNameAnimal() + "'",
                Command.COMPLEMENT_ANIMAL.getTextCommand(),
                nameButtons,
                dataButtons,
                1,
                dataButtons.size());
    }

    /**
     * This method allows you to start creating a report on the condition of the animal
     * Using{@link TelegramBotSenderService#sendMessageWithButtonCancel}
     *
     * @param chatOwner is not null
     */
    private void startCreateReport(Chat chatOwner) {
        telegramBotSenderService.sendMessageWithButtonCancel(
                chatOwner.getId(),
                ReportService.MESSAGE_WRITE_DIET + " or " + ReportService.MESSAGE_SEND_PHOTO,
                TelegramBotSenderService.NAME_BUTTON_FOR_CANCEL
        );
    }

    /**
     * This method allows you to continue (supplement) the animal report
     * Using{@link TelegramBotSenderService#sendMessageWithButtonCancel}
     * Using{@link TelegramUnfinishedRequestService#delUnfinishedRequestForChat}
     * Using{@link TelegramBotSenderService#sendMessage(Long, String)}
     * Using{@link TelegramBotSenderService#sendButtonsCommandForChat(Long)}
     *
     * @param chatOwner is not null
     * @param report    is not null
     */
    private void continueCreateReport(Chat chatOwner, Report report) {
        if (report != null) {
            StringBuilder message = new StringBuilder();
            if (report.getDiet() == null) {
                message.append(ReportService.MESSAGE_WRITE_DIET);
                message.append(" ");
            } else if (report.getFeeling() == null) {
                message.append(ReportService.MESSAGE_WRITE_FEELING);
                message.append(" ");
            } else if (report.getBehavior() == null) {
                message.append(ReportService.MESSAGE_WRITE_BEHAVIOR);
                message.append(" ");
            }

            if (report.getPhoto() == null) {
                if (message.length() > 0) {
                    message.append("or ");
                }
                message.append(ReportService.MESSAGE_SEND_PHOTO);
            }
            if (message.toString().length() != 0) {
                telegramBotSenderService.sendMessageWithButtonCancel(
                        chatOwner.getId(),
                        message.toString(),
                        TelegramBotSenderService.NAME_BUTTON_FOR_CANCEL);
            } else {
                telegramUnfinishedRequestService.delUnfinishedRequestForChat(chatOwner);
                telegramBotSenderService.sendMessage(
                        chatOwner.getId(),
                        ReportService.MESSAGE_REPORT_CREATE + "\ndiet: " +
                                report.getDiet() + "\nfeeling: " +
                                report.getFeeling() + "\nbehavior: " +
                                report.getBehavior() + "\nand photo");
                telegramBotSenderService.sendButtonsCommandForChat(chatOwner.getId());
            }
        }
    }

    /**
     * This method shows what should be in the report and how it can be supplemented
     * Using{@link TelegramUnfinishedRequestService#addUnfinishedRequestForChat(Chat, Command)}
     * Using{@link UserService#findOrCreateActualReport(Chat)
     * Using{@link UserService#createUpdateReport(Chat, String, String, String, String)}
     *
     * @param updateDpo is not null
     */
    public void report(UpdateDPO updateDpo) {
        Chat chatOwner = getChatByIdOrNew(updateDpo.getIdChat());
        telegramUnfinishedRequestService.addUnfinishedRequestForChat(chatOwner, Command.REPORT);
        String message = updateDpo.getMessage();
        Report report = userService.findOrCreateActualReport(chatOwner);
        String idMedia = null;
        String diet = null;
        String feeling = null;
        String behavior = null;

        if (updateDpo.getIdMedia() != null) {
            idMedia = updateDpo.getIdMedia();
        }
        if (report != null && message != null && message.length() != 0) {
            if (report.getDiet() == null) {
                diet = message;
            } else if (report.getFeeling() == null) {
                feeling = message;
            } else if (report.getBehavior() == null) {
                behavior = message;
            }
        }

        if (report != null &&
                report.getPhoto() == null &&
                report.getDiet() == null &&
                report.getFeeling() == null &&
                report.getBehavior() == null &&
                idMedia == null &&
                diet == null &&
                feeling == null &&
                behavior == null
        ) {
            startCreateReport(chatOwner);
            return;
        }

        report = userService.createUpdateReport(chatOwner, diet, feeling, behavior, idMedia);
        continueCreateReport(chatOwner, report);
    }

    /**
     * This method allows you to approve the report and send it to the database
     * Using{@link TelegramMapperService#mapStringToLong(String)}
     * Using{@link UserService#approveReport(Long, boolean)}
     * Using{@link TelegramBotSenderService#sendMessage(Long, String)}
     *
     * @param updateDpo is not null
     */
    public void approveReport(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        String message = updateDpo.getMessage();
        if (message != null && message.length() > 0 && message.contains(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL)) {
            String[] messageMas = message.split(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
            if (messageMas.length == 2) {
                Long idReport = telegramMapperService.mapStringToLong(messageMas[0]);
                boolean approve = Boolean.parseBoolean(messageMas[1]);
                Report report = null;
                try {
                    report = userService.approveReport(idReport, approve);
                } catch (ReportNotFoundException e) {
                    //don't do anything
                }
                if (report != null) {
                    if (approve) {
                        telegramBotSenderService.sendMessage(chatVolunteer.getId(),
                                ReportService.MESSAGE_REPORT_IS_PLACED_GOOD);
                        telegramBotSenderService.sendMessage(report.getAnimalOwnership().getOwner().getChatTelegram().getId(),
                                ReportService.MESSAGE_REPORT_IS_PLACED_GOOD);
                    } else {
                        telegramBotSenderService.sendMessage(chatVolunteer.getId(),
                                ReportService.MESSAGE_REPORT_IS_PLACED_BAD);
                        telegramBotSenderService.sendMessage(report.getAnimalOwnership().getOwner().getChatTelegram().getId(),
                                ReportService.MESSAGE_REPORT_IS_PLACED_BAD_OWNER);
                    }
                }
            }
        }
    }

    /**
     * This method allows you to view the owner report
     * Using {@link UserService#getOpenAndNotApproveReport()}
     * Using {@link TelegramBotSenderService#sendMessage(Long, String)}
     * Using {@link TelegramBotSenderService#sendButtonsWithOneData(Long, String, String, List, List, int, int)}
     *
     * @param updateDpo is not null
     */
    public void viewReport(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        String message = updateDpo.getMessage();
        if (message == null || message.length() == 0) {
            Report report = userService.getOpenAndNotApproveReport();
            if (report == null) {
                telegramBotSenderService.sendMessage(chatVolunteer.getId(), ReportService.MESSAGE_ALL_REPORT_ARE_APPROVE);
                telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
            } else {
                List<String> nameButtons = new ArrayList<>();
                List<String> dataButtons = new ArrayList<>();
                nameButtons.add(ReportService.BUTTON_GOOD);
                dataButtons.add(report.getId() + TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true);
                nameButtons.add(ReportService.BUTTON_BAD);
                dataButtons.add(report.getId() + TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + false);
                telegramBotSenderService.sendMessage(chatVolunteer.getId(), report.toString());
                telegramBotSenderService.sendButtonsWithOneData(
                        chatVolunteer.getId(),
                        ReportService.MESSAGE_APPROVE_OR_NOT,
                        Command.APPROVE_REPORT.getTextCommand(),
                        nameButtons,
                        dataButtons,
                        2, 1);
            }
        }
    }

    /**
     * This method sends the owner a notification about the need to fill out a report
     * Using {@link UserService#changeUserDateLastNotificationToNow(Chat)}
     * Using {@link TelegramBotSenderService#sendMessage(Long, String)}
     *
     * @param lateList is not null
     */
    public void sendNotificationAboutReport(List<AnimalOwnership> lateList) {
        if (lateList == null || lateList.size() == 0) {
            return;
        }
        for (AnimalOwnership animalOwnership : lateList) {
            if (animalOwnership.getOwner().getChatTelegram() != null) {
                Chat chat = animalOwnership.getOwner().getChatTelegram();
                userService.changeUserDateLastNotificationToNow(chat);
                telegramBotSenderService.sendMessage(chat.getId(), ReportService.MESSAGE_NOTIFICATION_ABOUT_REPORT);
            }
        }
    }

    /**
     * This method allows the volunteer to contact the owner through a request
     * Using {@link UserService#getRandomVolunteer()}
     * Using {@link TelegramBotSenderService#sendMessage(Long, String)}
     *
     * @param lateList is not null
     */
    public void sendRequestToVolunteerToContactOwner(List<AnimalOwnership> lateList) {
        if (lateList == null || lateList.size() == 0) {
            return;
        }
        Chat chatVolunteer = userService.getRandomVolunteer().getChatTelegram();
        if (chatVolunteer == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (AnimalOwnership animalOwnership : lateList) {
            sb.append(animalOwnership.toString());
        }
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), ReportService.MESSAGE_NEED_CONTACT_OWNER);
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), sb.toString());
    }

    /**
     * This method allows you not to approve the open receipt of an animal without a trial period
     * Using{@link UserService#getRandomVolunteer()}
     * Using {@link TelegramBotSenderService#sendMessage(Long, String)}
     *
     * @param animalOwnershipList is not null
     */
    public void sendNotApproveOpenAnimalOwnershipWithNotTrial(List<AnimalOwnership> animalOwnershipList) {
        if (animalOwnershipList == null || animalOwnershipList.size() == 0) {
            return;
        }
        Chat chatVolunteer = userService.getRandomVolunteer().getChatTelegram();
        if (chatVolunteer == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (AnimalOwnership animalOwnership : animalOwnershipList) {
            sb.append(animalOwnership.toString());
        }
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), AnimalOwnershipService.MESSAGE_TRIAL_IS_OVER);
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), sb.toString());
    }

    /**
     * This method really allows the volunteer to view the ownership of the animal for the owner
     * Using{@link UserService#getOneNotApproveOpenAnimalOwnershipWithNotTrial()}
     * Using{@link TelegramBotSenderService#REQUEST_SPLIT_SYMBOL}
     * Using{@link TelegramBotSenderService#sendButtonsWithOneData}
     *
     * @param updateDpo
     */
    public void viewAnimalOwnership(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        String message = updateDpo.getMessage();
        if (message == null || message.length() == 0) {
            AnimalOwnership animalOwnership = userService.getOneNotApproveOpenAnimalOwnershipWithNotTrial();
            if (animalOwnership == null) {
                telegramBotSenderService.sendMessage(chatVolunteer.getId(), AnimalOwnershipService.MESSAGE_ALL_ANIMAL_OWNERSHIP_ARE_APPROVE);
            } else {
                List<String> nameButtons = new ArrayList<>();
                List<String> dataButtons = new ArrayList<>();
                nameButtons.add(ReportService.BUTTON_GOOD);
                dataButtons.add(animalOwnership.getId() + TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + true);
                nameButtons.add(ReportService.BUTTON_BAD);
                dataButtons.add(animalOwnership.getId() + TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + false);
                nameButtons.add("+week");
                dataButtons.add(Command.EXTEND_TRIAL.getTextCommand() + TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                        animalOwnership.getId());
                telegramBotSenderService.sendMessage(chatVolunteer.getId(), animalOwnership.toString());
                telegramBotSenderService.sendButtonsWithOneData(
                        chatVolunteer.getId(),
                        ReportService.MESSAGE_APPROVE_OR_NOT,
                        Command.APPROVE_OWNERSHIP.getTextCommand(),
                        nameButtons,
                        dataButtons,
                        2, 2);
            }
        }
    }

    /**
     * This method does allow a volunteer to approve the ownership of an animal for a owner
     * Using{@link TelegramMapperService#mapStringToLong(String)}
     * Using{@link UserService#approveAnimalOwnership(Long, boolean)}
     * Using{@link TelegramBotSenderService#sendMessage(Long, String)}
     *
     * @param updateDpo is not null
     */
    public void approveAnimalOwnership(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        String message = updateDpo.getMessage();
        if (message != null && message.length() > 0 && message.contains(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL)) {
            String[] messageMas = message.split(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
            if (messageMas.length == 2) {
                Long idAnimalOwnership = telegramMapperService.mapStringToLong(messageMas[0]);
                boolean approve = Boolean.parseBoolean(messageMas[1]);
                AnimalOwnership animalOwnership = null;
                try {
                    animalOwnership = userService.approveAnimalOwnership(idAnimalOwnership, approve);
                } catch (AnimalOwnershipNotFoundException e) {
                    //don't do anything
                    return;
                } catch (AnimalOwnershipAlreadyCloseException e) {
                    telegramBotSenderService.sendMessage(chatVolunteer.getId(), AnimalOwnershipService.MESSAGE_ALREADY_CLOSE);
                    telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
                    return;
                }
                if (animalOwnership != null) {
                    if (approve) {
                        telegramBotSenderService.sendMessage(chatVolunteer.getId(),
                                AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_GOOD);
                        telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
                        telegramBotSenderService.sendMessage(animalOwnership.getOwner().getChatTelegram().getId(),
                                AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_GOOD);
                    } else {
                        telegramBotSenderService.sendMessage(chatVolunteer.getId(),
                                AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_BAD);
                        telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
                        telegramBotSenderService.sendMessage(animalOwnership.getOwner().getChatTelegram().getId(),
                                AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_BAD_OWNER);
                    }
                }
            }
        }
    }

    /**
     * This method makes it possible to extend the pet's stay at the client's home
     * Using {@link TelegramMapperService#mapStringToLong(String)}
     * Using {@link UserService#extendTrialAnimalOwnership(Long)}
     * Using {@link TelegramBotSenderService#sendMessage(Long, String)}
     *
     * @param updateDpo is not null
     */
    public void extendTrial(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        String message = updateDpo.getMessage();
        if (message != null && message.length() > 0 && !message.contains(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL)) {
            Long idAnimalOwnership = telegramMapperService.mapStringToLong(message);
            AnimalOwnership animalOwnership = null;
            try {
                animalOwnership = userService.extendTrialAnimalOwnership(idAnimalOwnership);
            } catch (AnimalOwnershipNotFoundException e) {
                //don't do anything
                return;
            }
            if (animalOwnership != null) {
                telegramBotSenderService.sendMessage(chatVolunteer.getId(),
                        animalOwnership.toString());
                telegramBotSenderService.sendButtonsCommandForChat(chatVolunteer.getId());
                telegramBotSenderService.sendMessage(animalOwnership.getOwner().getChatTelegram().getId(),
                        animalOwnership.toString());

            }
        }
    }

    /**
     * This method send info dogs disabilities
     *
     * @param updateDpo is not null
     */
    public void sendInfoDogsDisabilities(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), InfoDogsWithDisabilities.getInfoEn());
    }
    /**
     * This method send info list documents
     *
     * @param updateDpo is not null
     */
    public void sendInfoListDocuments(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), InfoListOfDocuments.getInfoEn());
    }
    /**
     * This method send info recommend home dog
     *
     * @param updateDpo is not null
     */
    public void sendInfoRecommendHomeDog(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), InfoRecommendationsHomeDog.getInfoEn());
    }
    /**
     * This method send info recommend home dog small
     *
     * @param updateDpo is not null
     */
    public void sendInfoRecommendHomeDogSmall(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), InfoRecommendationsHomeSmallDog.getInfoEn());
    }
    /**
     * This method send info refuse
     *
     * @param updateDpo is not null
     */
    public void sendInfoRefuse(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), InfoRefuseDogFromShelter.getInfoEn());
    }
    /**
     * This method send info tips
     *
     * @param updateDpo is not null
     */
    public void sendInfoTips(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), InfoTipsFromDogHandler.getInfoEn());
    }
    /**
     * This method send info transportation
     *
     * @param updateDpo is not null
     */
    public void sendInfoTransportation(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), InfoTransportationAnimals.getInfoEn());
    }

    /**
     * This method send info need handler
     *
     * @param updateDpo is not null
     */
    public void sendInfoNeedHandler(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), InfoWhyDoYouNeedDogHandler.getInfoEn());
    }

    /**
     * This method send info get dog
     *
     * @param updateDpo is not null
     */
    public void sendInfoGetDog(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        telegramBotSenderService.sendMessage(chatVolunteer.getId(), InfoGettingKnowDog.getInfoEn());
    }
}
