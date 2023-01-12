package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.repository.ChatRepository;

import java.util.List;

/**
 * The class is designed for the operation of the Call Request Service class
 * An important parameter that affects the operation of the CALL_REQUEST CALL_CLIENT command.
 */
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final Logger logger = LoggerFactory.getLogger(ChatService.class);


    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    /**
     * @param id
     * @param name
     * @param userName
     * @return
     */
    public Chat getChatByIdOrNewWithNameAndUserName(Long id, String name, String userName) {
        logger.info("Method getChatByIdOrNew was start for find Chat by id = {}, or return new Chat", id);
        Chat chat = chatRepository.getChatById(id);
        if (chat == null) {
            logger.debug("Method getChatByIdOrNew will return the new chat");
            chat = new Chat();
            chat.setId(id);
        }
        chat.setName(name);
        chat.setUserName(userName);
        chatRepository.save(chat);
        logger.debug("Method getChatByIdOrNew will return the found chat");
        return chat;
    }

    /**
     * @param chat
     * @return
     */
    public Chat addChat(Chat chat) {
        return chatRepository.save(chat);
    }

    /**
     * @param id
     * @return
     */
    public Chat findChat(Long id) {
        return chatRepository.getChatById(id);
    }

    /**
     * @param id
     */
    public void deleteChat(Long id) {
        Chat chat = new Chat();
        chat.setId(id);
        deleteChat(chat);
    }

    /**
     * @param chat
     */
    public void deleteChat(Chat chat) {
        chatRepository.delete(chat);
    }

    /**
     * @param id
     * @return
     */
    public boolean isVolunteer(Long id) {
        logger.info("Method isVolunteer was start for to check if the chat with id = {} is a volunteer", id);
        Chat chat = findChat(id);
        if (chat == null || !chat.isVolunteer()) {
            logger.debug("Method isVolunteer detected volunteer by idChat = {}", id);
            return false;
        }
        logger.debug("Method isVolunteer don't detected volunteer by idChat = {}", id);
        return true;
    }

    /**
     * @return
     */
    public Chat getChatOfVolunteer() {
        return chatRepository.getChatOfVolunteer();
    }
}
