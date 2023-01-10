package pro.sky.animalshelter4.service;

import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.model.UpdateDPO;
import pro.sky.animalshelter4.repository.CallRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

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

    public void process(UpdateDPO updateDpo) {
        Chat chatClient = chatService.getChatByIdOrNewWithName(updateDpo.getIdChat(), updateDpo.getUserName());
        chatClient.setName(updateDpo.getUserName());
        chatService.addChat(chatClient);

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

    public void sendNotificationAboutAllCallRequest(Long idChat) {
        List<CallRequest> callRequestList = getAllOpenByChat(idChat);
        if (callRequestList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            getAllOpenByChat(idChat).forEach(callRequest -> {
                sb.append(MESSAGE_ABOUT_CALL_REQUEST);
                sb.append(callRequest.getChatClient().getName());
                sb.append("\n");
            });
            telegramBotSenderService.sendMessage(idChat, sb.toString());
        }
    }

    public List<CallRequest> getAllOpenByChat(Long idChat) {
        return callRequestRepository.getAllOpenByChatId(idChat);
    }

    public CallRequest add(CallRequest callRequest) {
        return callRequestRepository.save(callRequest);
    }
}
