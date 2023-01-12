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
     * The method searches for and returns an existing {@link Chat} instance,
     * if it does not exist, then creates a new one with the necessary parameters
     * @param id must be not null
     * @param name must be not null
     * @param userName must be not null
     * @return {@link Chat} new or found
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
     * The method adds a new chat to the repository and returns the same instance
     * using {@link ChatRepository#save(Object)}
     * @param chat id of chat must be not null
     * @return {@link Chat}
     */
    public Chat addChat(Chat chat) {
        return chatRepository.save(chat);
    }

    /**
     * The method searches for a chat in the repository by id of chat
     * using {@link ChatRepository#getChatById(Long)}
     * @param id
     * @return found chat
     */
    public Chat findChat(Long id) {
        return chatRepository.getChatById(id);
    }

    /**
     * The method delete chat from repository
     * using {@link ChatService#deleteChat(Chat)}
     * @param id must be not null
     */
    public void deleteChat(Long id) {
        Chat chat = new Chat();
        chat.setId(id);
        deleteChat(chat);
    }

    /**
     * The method delete chat from repository
     * using {@link ChatRepository#delete(Object)}
     * @param chat
     */
    public void deleteChat(Chat chat) {
        chatRepository.delete(chat);
    }


    /**
     * the method determines by id whether the user is a volunteer or not
     * @param id must be not null
     * @return true if user is volunteer, false if else
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
     * @return chat belonging to a volunteer or null
     */
    public Chat getChatOfVolunteer() {
        return chatRepository.getChatOfVolunteer();
    }
}
