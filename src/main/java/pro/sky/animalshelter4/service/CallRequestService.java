package pro.sky.animalshelter4.service;

import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.listener.TelegramBotUpdatesListener;
import pro.sky.animalshelter4.model.UpdateDPO;
import pro.sky.animalshelter4.repository.CallRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
/**
 *  This class is needed to send requests for communication with the volunteer
 *  The class must have many dependencies so that it can work correctly.
 *  As well as respond to requests received from {@link TelegramBotSenderService}
 */

@Service
public class CallRequestService {
    public final static String MESSAGE_ABOUT_CALL_REQUEST = "You have call request by ";
    public final static String MESSAGE_VOLUNTEERS_IS_ABSENT = "Sorry. All volunteers is absent";
    public final static String MESSAGE_OK_VOLUNTEERS_FOUND = "OK. Volunteer will call you";
    private final ChatService chatService;
    private final CallRequestRepository callRequestRepository;
    private final TelegramBotSenderService telegramBotSenderService;

    public CallRequestService(ChatService chatService, CallRequestRepository callRequestRepository, TelegramBotSenderService telegramBotSenderService) {
        this.chatService = chatService;
        this.callRequestRepository = callRequestRepository;
        this.telegramBotSenderService = telegramBotSenderService;
    }

    /**
     * This method handles requests received from TelegrammBotSenderServes.
     * They will determine which response to the command to send if the volunteer is on site or not.
     * In addition, the method outputs a message {@link TelegramBotSenderService#sendMessage }
     * As well as methods from the following classes {@link CallRequestService#sendNotificationAboutAllCallRequest(Long)}
     * And {@link ChatService#getChatByIdOrNewWithNameAndUserName(Long, String, String)}
     * Method from repository {@link CallRequestRepository#getFirstOpenByChatClientId(Long)}
     * @param updateDpo
     */
    public void process(UpdateDPO updateDpo) {
        Chat chatClient = chatService.getChatByIdOrNewWithNameAndUserName(updateDpo.getIdChat(), updateDpo.getName(), updateDpo.getUserName());

        Chat chatVolunteer = chatService.getChatOfVolunteer();
        if (chatVolunteer == null) {
            telegramBotSenderService.sendMessage(chatClient.getId(), MESSAGE_VOLUNTEERS_IS_ABSENT);
            return;
        }

        if (null != callRequestRepository.getFirstOpenByChatClientId(chatClient.getId())) {
            telegramBotSenderService.sendMessage(chatClient.getId(), MESSAGE_OK_VOLUNTEERS_FOUND);
            return;
        }

        CallRequest callRequest = new CallRequest();
        callRequest.setOpen(true);
        callRequest.setLocalDateTimeOpen(LocalDateTime.now());
        callRequest.setChatClient(chatClient);
        callRequest.setChatVolunteer(chatVolunteer);
        add(callRequest);

        telegramBotSenderService.sendMessage(chatClient.getId(), MESSAGE_OK_VOLUNTEERS_FOUND);
        sendNotificationAboutAllCallRequest(chatVolunteer.getId());
    }
    /**
     * This method sends all the call requests that are available to the volunteer.
     * The method from telegrammBotSendlerServes {@link TelegramBotSenderService#sendMessage(Long, String)}
     * The request list must be greater than 0.
     * Also, the method outputs a message from{@link CallRequestService#MESSAGE_ABOUT_CALL_REQUEST}
     * @param idChat must be not null
     */
    public void sendNotificationAboutAllCallRequest(Long idChat) {
        List<CallRequest> callRequestList = getAllOpenByChat(idChat);
        if (callRequestList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            getAllOpenByChat(idChat).forEach(callRequest -> {
                sb.append(MESSAGE_ABOUT_CALL_REQUEST);
                sb.append(callRequest.getChatClient().getName());
                sb.append(" @");
                sb.append(callRequest.getChatClient().getUserName());
                sb.append("\n");
            });
            telegramBotSenderService.sendMessage(idChat, sb.toString());
        }
    }

    /**
     * This method outputs all the Hat_ids to the volunteer.
     * Using the repository method {@link CallRequestRepository#getAllOpenByChatId(Long)}
     * @param idChat must be not null
     * @return
     */
    public List<CallRequest> getAllOpenByChat(Long idChat) {
        return callRequestRepository.getAllOpenByChatId(idChat);
    }

    /**
     * The method saves the call requests.
     * Using the repository metod {@link CallRequestRepository#save(Object)}
     * @param callRequest
     * @return returns a call Request
     */
    public CallRequest add(CallRequest callRequest) {
        return callRequestRepository.save(callRequest);
    }
}
