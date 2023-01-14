package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.entityDto.CallRequestDto;
import pro.sky.animalshelter4.exception.CallRequestNotFoundException;
import pro.sky.animalshelter4.exception.CantCloseCallRequestException;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.UpdateDPO;
import pro.sky.animalshelter4.repository.CallRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is needed to send requests for communication with the volunteer
 * The class must have many dependencies so that it can work correctly.
 * As well as respond to requests received from {@link TelegramBotSenderService}
 */

@Service
public class CallRequestService {
    public final static String MESSAGE_YOU_HAVE_CALL_REQUEST = "You have call request by ";
    public final static String MESSAGE_YOU_DONT_HAVE_CALL_REQUEST = "You don't have any call request by ";
    public final static String MESSAGE_VOLUNTEERS_IS_ABSENT = "Sorry. All volunteers is absent";
    public final static String MESSAGE_OK_VOLUNTEERS_FOUND = "OK. Volunteer will call you";
    public final static String MESSAGE_YOU_CAN_CLOSE_CALL_REQUEST = "Press button with ID for close";
    public final static String MESSAGE_CALL_REQUEST_IS_CLOSE = "Call request closed";
    private final UserService userService;
    private final ChatService chatService;
    private final CallRequestRepository callRequestRepository;
    private final TelegramBotSenderService telegramBotSenderService;
    private final DtoMapperService dtoMapperService;
    private final TelegramUnfinishedRequestService telegramUnfinishedRequestService;

    private final Logger logger = LoggerFactory.getLogger(CallRequestService.class);


    public CallRequestService(UserService userService, ChatService chatService, CallRequestRepository callRequestRepository, TelegramBotSenderService telegramBotSenderService, DtoMapperService dtoMapperService, TelegramUnfinishedRequestService telegramUnfinishedRequestService) {
        this.userService = userService;
        this.chatService = chatService;
        this.callRequestRepository = callRequestRepository;
        this.telegramBotSenderService = telegramBotSenderService;
        this.dtoMapperService = dtoMapperService;
        this.telegramUnfinishedRequestService = telegramUnfinishedRequestService;
    }

    /**
     * This method handles requests received from TelegrammBotSenderServes.
     * They will determine which response to the command to send if the volunteer is on site or not.
     * In addition, the method outputs a message {@link TelegramBotSenderService#sendMessage }
     * As well as methods from the following classes {@link CallRequestService#sendNotificationAboutCallRequestsToTelegram(User, boolean)}
     * And {@link ChatService#getChatByIdOrNew(Long)}
     * Method from repository {@link CallRequestRepository#getFirstOpenByUserIdForClient(Long)}
     *
     * @param updateDpo
     */
    public void process(UpdateDPO updateDpo) {
        Chat chatClient = chatService.getChatByIdOrNew(updateDpo);

        User userClient = userService.getUserWithTelegramUserId(updateDpo.getIdChat());
        if (userClient == null) {
            userClient = new User();
            userClient.setNameUser(updateDpo.getFirstName() + " " + updateDpo.getLastName());
            userClient.setChatTelegram(chatClient);
            userClient.setVolunteer(false);
            userClient = userService.addUser(userClient);
        }


        User userVolunteer = userService.getRandomVolunteer();
        if (userVolunteer == null) {
            telegramBotSenderService.sendMessage(chatClient.getId(), MESSAGE_VOLUNTEERS_IS_ABSENT);
            return;
        }

        User chatUser = userService.getUserWithTelegramUserId(chatClient.getId());
        if (chatUser != null &&
                chatUser.getId() != null &&
                callRequestRepository.getFirstOpenByUserIdForClient(chatUser.getId()) != null) {
            telegramBotSenderService.sendMessage(chatClient.getId(), MESSAGE_OK_VOLUNTEERS_FOUND);
            if (userClient.getPhone() == null || userClient.getPhone().length() == 0) {
                chatService.startChangePhoneByUserFromTelegram(userClient.getChatTelegram());
            }
            return;
        }

        CallRequest callRequest = new CallRequest();
        callRequest.setOpen(true);
        callRequest.setLocalDateTimeOpen(LocalDateTime.now());
        callRequest.setClient(userClient);
        callRequest.setVolunteer(userVolunteer);
        addCallRequest(callRequest);

        sendNotificationAboutCallRequestsToTelegram(userVolunteer, false);
        telegramBotSenderService.sendMessage(chatClient.getId(), MESSAGE_OK_VOLUNTEERS_FOUND);

        if (userClient.getPhone() == null || userClient.getPhone().length() == 0) {
            chatService.startChangePhoneByUserFromTelegram(userClient.getChatTelegram());
        } else {
            telegramBotSenderService.sendButtonsCommandForChat(chatClient.getId());
        }


    }

    /**
     * This method sends all the call requests that are available to the volunteer.
     * The method from telegrammBotSendlerServes {@link TelegramBotSenderService#sendMessage(Long, String)}
     * The request list must be greater than 0.
     * Also, the method outputs a message from{@link CallRequestService#MESSAGE_YOU_HAVE_CALL_REQUEST}
     *
     * @param user must be not null
     */
    public void sendNotificationAboutCallRequestsToTelegram(User user, boolean requiredResponse) {
        List<CallRequest> callRequestList = callRequestRepository.getAllOpenByUserIdForVolunteer(user.getId());

        List<String> nameButtons = new ArrayList<>();
        List<String> dataButtons = new ArrayList<>();

        if (callRequestList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            callRequestList.forEach(callRequest -> {
                nameButtons.add("Close " + callRequest.getId());
                dataButtons.add(callRequest.getId().toString());
                sb.append(MESSAGE_YOU_HAVE_CALL_REQUEST);
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
            telegramBotSenderService.sendMessage(user.getChatTelegram().getId(), sb.toString());
            telegramBotSenderService.sendButtonsWithOneData(
                    user.getChatTelegram().getId(),
                    MESSAGE_YOU_CAN_CLOSE_CALL_REQUEST,
                    Command.CLOSE_CALL_REQUEST.getTextCommand(),
                    nameButtons,
                    dataButtons,
                    widthAndHeight.getFirst(),
                    widthAndHeight.getSecond()
            );
        } else if (requiredResponse) {
            telegramBotSenderService.sendMessage(user.getChatTelegram().getId(), MESSAGE_YOU_DONT_HAVE_CALL_REQUEST);
        }
    }

    public void closeCallRequest(User user, Long idCallRequest) {
        CallRequest callRequest = findCallRequest(idCallRequest);
        if (callRequest == null) {
            throw new CallRequestNotFoundException(idCallRequest.toString());
        }
        if (!user.getId().equals(callRequest.getVolunteer().getId())) {
            throw new CantCloseCallRequestException(idCallRequest.toString());
        }
        callRequest.setOpen(false);
        callRequestRepository.save(callRequest);
        telegramBotSenderService.sendMessage(user.getChatTelegram().getId(), MESSAGE_CALL_REQUEST_IS_CLOSE);
    }

    /**
     * This method outputs all the Hat_ids to the volunteer.
     * Using the repository method {@link CallRequestRepository#getAllOpenByUserIdForClient(Long)}
     *
     * @param idUser must be not null
     * @return
     */
    public List<CallRequest> getAllOpenByClient(Long idUser) {
        return callRequestRepository.getAllOpenByUserIdForClient(idUser);
    }

    /**
     * The method saves the call requests.
     * Using the repository metod {@link CallRequestRepository#save(Object)}
     *
     * @param callRequest
     * @return returns a call Request
     */
    public CallRequest addCallRequest(CallRequest callRequest) {
        logger.info("Method addCallRequest was start for create new CallRequest");
        return callRequestRepository.save(callRequest);
    }

    public CallRequestDto createCallRequest(CallRequestDto callRequestDto) {
        logger.info("Method createCallRequest was start for create new CallRequest");
        return dtoMapperService.toDto(callRequestRepository.save(dtoMapperService.toEntity(callRequestDto)));
    }

    public CallRequestDto readCallRequest(Long id) {
        logger.info("Method readCallRequest was start for find CallRequest by id");
        return dtoMapperService.toDto(
                callRequestRepository.findById(id).
                        orElseThrow(() -> new CallRequestNotFoundException(String.valueOf(id))));
    }

    public CallRequest findCallRequest(Long id) {
        logger.info("Method findCallRequest was start for find CallRequest by id");
        return callRequestRepository.findById(id).
                orElseThrow(() -> new CallRequestNotFoundException(String.valueOf(id)));
    }

    public CallRequestDto updateCallRequest(CallRequestDto callRequestDto) {
        logger.info("Method updateCallRequest was start for update callRequest");
        CallRequest newCallRequest = dtoMapperService.toEntity(callRequestDto);
        CallRequest oldCallRequest = findCallRequest(newCallRequest.getId());
        if (oldCallRequest == null) {
            throw new CallRequestNotFoundException(String.valueOf(newCallRequest.getId()));
        }
        oldCallRequest.setOpen(newCallRequest.isOpen());
        oldCallRequest.setVolunteer(newCallRequest.getVolunteer());
        oldCallRequest.setClient(newCallRequest.getClient());
        oldCallRequest.setLocalDateTimeOpen(newCallRequest.getLocalDateTimeOpen());
        oldCallRequest.setLocalDateTimeClose(newCallRequest.getLocalDateTimeClose());
        return dtoMapperService.toDto(callRequestRepository.save(oldCallRequest));
    }

    public CallRequestDto deleteCallRequest(Long id) {
        CallRequest callRequest = new CallRequest();
        callRequest.setId(id);
        return dtoMapperService.toDto(deleteCallRequest(callRequest));
    }

    public CallRequest deleteCallRequest(CallRequest callRequest) {
        logger.info("Method deleteCallRequest was start for delete CallRequest");
        if (callRequest.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of callRequest");
        }
        CallRequest callRequestFound = callRequestRepository.findById(callRequest.getId()).
                orElseThrow(() -> new CallRequestNotFoundException(String.valueOf(callRequest.getId())));
        callRequestRepository.delete(callRequestFound);
        return callRequestFound;
    }

    public List<CallRequestDto> getAll() {
        logger.info("Method getAll was start for return all CallRequest");
        return callRequestRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public List<CallRequestDto> getAllOpenCallRequestVolunteer(Long id) {
        logger.info(
                "Method getAllOpenCallRequestVolunteer was start for return all CallRequest Volunteer with id = {}"
                , id);
        return callRequestRepository.getAllOpenByUserIdForVolunteer(id).stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public List<CallRequestDto> getAllOpenCallRequestClient(Long id) {
        logger.info(
                "Method getAllCallRequestUser was start for return all CallRequest Client with id = {}"
                , id);
        return callRequestRepository.getAllOpenByUserIdForClient(id).stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public List<CallRequestDto> getAllOpenCallRequest() {
        logger.info("Method getAllOpenCallRequest was start for return all open CallRequest");
        return callRequestRepository.getAllOpenCallRequest().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public List<CallRequestDto> getAllCloseCallRequest() {
        logger.info("Method getAllOpenCallRequest was start for return all close CallRequest");
        return callRequestRepository.getAllCloseCallRequest().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }
}
