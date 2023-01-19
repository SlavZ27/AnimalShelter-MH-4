package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.*;
import pro.sky.animalshelter4.entityDto.ChatDto;
import pro.sky.animalshelter4.exception.*;
import pro.sky.animalshelter4.model.AnimalUnit;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.OwnershipUnit;
import pro.sky.animalshelter4.model.UpdateDPO;
import pro.sky.animalshelter4.repository.ChatRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pro.sky.animalshelter4.model.AnimalUnit.ANIMAL_TYPE;
import static pro.sky.animalshelter4.model.AnimalUnit.START;

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

    public void closeUnfinishedRequest(UpdateDPO updateDPO) {
        Chat chat = getChatByIdOrNew(updateDPO.getIdChat());
        telegramUnfinishedRequestService.delUnfinishedRequestForChat(chat);
        telegramBotSenderService.sendButtonsCommandForChat(chat.getId());
    }

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
        OwnershipUnit ownershipUnit = null;
        String[] messageMas = null;

        if (message == null || message.length() == 0) {
            ownershipUnit = OwnershipUnit.START;
        } else if (message.contains(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL)) {
            messageMas = message.split(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
            if (message.length() > 2) {
                ownershipUnit = OwnershipUnit.valueOf(messageMas[messageMas.length - 2]);
            }
        }

        if (ownershipUnit == null) {
            //badRequest
            return;
        }

        switch (ownershipUnit) {
            case START:
                //send List of Users
                startCreateOwnSendListClients(chatVolunteer, clientList);
                break;
            case USER:
                //send list of animals
                if (messageMas == null || messageMas.length < 2) {
                    return;
                }
                Long idUserClient = telegramMapperService.mapStringToLong(messageMas[messageMas.length - 1]);
                continueCreateOwnSendListAnimals(chatVolunteer, idUserClient, animalList);
                break;
            case ANIMAL:
                //finish request
                if (messageMas == null || messageMas.length < 4) {
                    return;
                }
                idUserClient = telegramMapperService.mapStringToLong(messageMas[messageMas.length - 3]);
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

    private void startCreateOwnSendListClients(Chat chatVolunteer, List<User> clientList) {
        List<String> nameButtons = clientList.stream().
                map(user -> user.getId() + " " + user.getNameUser())
                .collect(Collectors.toList());
        List<String> dataButtons = clientList.stream().
                map(user ->
                        OwnershipUnit.USER.name() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + user.getId())
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

    private void continueCreateOwnSendListAnimals(Chat chatVolunteer, Long idUserClient, List<Animal> animalList) {
        List<String> nameButtons = animalList.stream().
                map(animal -> animal.getId() +
                        TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animal.getAnimalType().getTypeAnimal() +
                        TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + animal.getNameAnimal()
                ).
                collect(Collectors.toList());
        List<String> dataButtons = animalList.stream().
                map(animal ->
                        OwnershipUnit.USER.name() +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                                idUserClient +
                                TelegramBotSenderService.REQUEST_SPLIT_SYMBOL + OwnershipUnit.ANIMAL.name() +
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

    public void complementAnimal(UpdateDPO updateDPO) {
        Chat chatVolunteer = getChatByIdOrNew(updateDPO.getIdChat());
        String message = updateDPO.getMessage();

        AnimalUnit animalUnit = null;
        String[] messageMas = null;

        if (message == null || message.length() == 0) {
            animalUnit = START;
        } else if (message.contains(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL)) {
            messageMas = message.split(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
            if (message.length() > 2) {
                animalUnit = AnimalUnit.valueOf(messageMas[messageMas.length - 2]);
            }
        }
        if (animalUnit == null && messageMas != null) {
            //badRequest
            return;
        }
        switch (animalUnit) {
            case START:
                answerQuestionAboutAnimals(chatVolunteer, true);
                break;
            case ANIMAL_TYPE:
                Long idAnimal = telegramMapperService.mapStringToLong(messageMas[messageMas.length - 3]);
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


    private void sendAnimalTypeButtons(Chat chatVolunteer, Animal animal) {
        List<AnimalType> animalTypeList = animalService.getAllAnimalType();
        List<String> nameButtons = animalTypeList.stream().
                map(AnimalType::getTypeAnimal).
                collect(Collectors.toList());
        List<String> dataButtons = animalTypeList.stream().
                map(animalType -> AnimalUnit.ID + TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                        animal.getId() + TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
                        ANIMAL_TYPE + TelegramBotSenderService.REQUEST_SPLIT_SYMBOL +
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

    private void startCreateReport(Chat chatOwner) {
        telegramBotSenderService.sendMessageWithButtonCancel(
                chatOwner.getId(),
                ReportService.MESSAGE_WRITE_DIET + " or " + ReportService.MESSAGE_SEND_PHOTO,
                TelegramBotSenderService.NAME_BUTTON_FOR_CANCEL
        );
    }

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

    public void approveReport(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        String message = updateDpo.getMessage();
        if (message != null && message.length() > 0 && message.contains(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL)) {
            String[] messageMas = message.split(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
            if (message.length() == 2) {
                Long idReport = telegramMapperService.mapStringToLong(messageMas[0]);
                boolean approve = Boolean.parseBoolean(messageMas[1]);
                Report report = userService.approveReport(idReport, approve);
                if (report != null) {
                    if (approve) {
                        telegramBotSenderService.sendMessage(chatVolunteer.getId(),
                                ReportService.MESSAGE_REPORT_IS_PLACED_GOOD);
                        telegramBotSenderService.sendMessage(report.getAnimalOwnership().getOwner().getId(),
                                ReportService.MESSAGE_REPORT_IS_PLACED_GOOD);
                    } else {
                        telegramBotSenderService.sendMessage(chatVolunteer.getId(),
                                ReportService.MESSAGE_REPORT_IS_PLACED_BAD);
                        telegramBotSenderService.sendMessage(report.getAnimalOwnership().getOwner().getId(),
                                ReportService.MESSAGE_REPORT_IS_PLACED_BAD_OWNER);
                    }
                }
            }
        }
    }

    public void viewReport(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        String message = updateDpo.getMessage();
        if (message == null || message.length() == 0) {
            Report report = userService.getOpenAndNotApproveReport();
            if (report == null) {
                telegramBotSenderService.sendMessage(chatVolunteer.getId(), ReportService.MESSAGE_ALL_REPORT_ARE_APPROVE);
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

    public void approveAnimalOwnership(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        String message = updateDpo.getMessage();
        if (message != null && message.length() > 0 && message.contains(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL)) {
            String[] messageMas = message.split(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
            if (message.length() == 2) {
                Long idAnimalOwnership = telegramMapperService.mapStringToLong(messageMas[0]);
                boolean approve = Boolean.parseBoolean(messageMas[1]);
                AnimalOwnership animalOwnership = userService.approveAnimalOwnership(idAnimalOwnership, approve);
                if (animalOwnership != null) {
                    if (approve) {
                        telegramBotSenderService.sendMessage(chatVolunteer.getId(),
                                AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_GOOD);
                        telegramBotSenderService.sendMessage(animalOwnership.getOwner().getId(),
                                AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_GOOD);
                    } else {
                        telegramBotSenderService.sendMessage(chatVolunteer.getId(),
                                AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_BAD);
                        telegramBotSenderService.sendMessage(animalOwnership.getOwner().getId(),
                                AnimalOwnershipService.MESSAGE_ANIMAL_OWNERSHIP_IS_PLACED_BAD_OWNER);
                    }
                }
            }
        }
    }

    public void extendTrial(UpdateDPO updateDpo) {
        Chat chatVolunteer = getChatByIdOrNew(updateDpo.getIdChat());
        String message = updateDpo.getMessage();
        if (message != null && message.length() > 0 && !message.contains(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL)) {
            Long idAnimalOwnership = telegramMapperService.mapStringToLong(message);
            AnimalOwnership animalOwnership = userService.extendTrialAnimalOwnership(idAnimalOwnership);
            if (animalOwnership != null) {
                telegramBotSenderService.sendMessage(chatVolunteer.getId(),
                        animalOwnership.toString());
                telegramBotSenderService.sendMessage(animalOwnership.getOwner().getId(),
                        animalOwnership.toString());

            }
        }
    }
}
