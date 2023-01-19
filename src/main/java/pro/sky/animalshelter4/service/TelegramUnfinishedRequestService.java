package pro.sky.animalshelter4.service;

import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.UnfinishedRequestTelegram;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.repository.UnfinishedRequestTelegramRepository;

/**
 * This class progress command unfinished request telegram, using method repository date base
 * {@link UnfinishedRequestTelegramRepository }
 */
@Service
public class TelegramUnfinishedRequestService {
    private final UnfinishedRequestTelegramRepository unfinishedRequestTelegramRepository;

    public TelegramUnfinishedRequestService(UnfinishedRequestTelegramRepository unfinishedRequestTelegramRepository) {
        this.unfinishedRequestTelegramRepository = unfinishedRequestTelegramRepository;
    }

    /**
     * This method create unfinished request in telegram using method repository
     * Using {@link UnfinishedRequestTelegramRepository#findByIdChat(Long)}
     * @param unfinishedRequestTelegram
     * @return unfinished request in telegram
     */
    private UnfinishedRequestTelegram add(UnfinishedRequestTelegram unfinishedRequestTelegram) {
        if (unfinishedRequestTelegramRepository.findByIdChat(unfinishedRequestTelegram.getChat().getId()) == null) {
            return unfinishedRequestTelegramRepository.save(unfinishedRequestTelegram);
        }
        return null;
    }

    /**
     * This method using method repository
     * Using {@link UnfinishedRequestTelegramRepository#findByIdChat(Long)}
     * @param id is not null
     * @return unfinished request
     */
    private UnfinishedRequestTelegram read(Long id) {
        return unfinishedRequestTelegramRepository.findById(id).orElse(null);
    }
    /**
     * This method searches for incomplete requests in telegram by id chat using method repository
     * Using {@link UnfinishedRequestTelegramRepository#findByIdChat(Long)}
     * @param idChat is not null
     * @return unfinished request
     */
    public UnfinishedRequestTelegram findByIdChat(Long idChat) {
        return unfinishedRequestTelegramRepository.findByIdChat(idChat);
    }


    /**
     * This method using method repository delete unfinished request by id
     * Using {@link UnfinishedRequestTelegramRepository#deleteById(Object)}
     * @param id is not null
     */
    private void delete(Long id) {
        if (read(id) != null) {
            unfinishedRequestTelegramRepository.deleteById(id);
        }
    }

    /**
     * This method using method repository delete unfinished request by unfinished Request
     * Using {@link UnfinishedRequestTelegramRepository#delete(Object)}
     * @param unfinishedRequestTelegram is not null
     */
    private void delete(UnfinishedRequestTelegram unfinishedRequestTelegram) {
        unfinishedRequestTelegramRepository.delete(unfinishedRequestTelegram);

    }

    /**
     * This method using method class{@link TelegramUnfinishedRequestService#add(UnfinishedRequestTelegram)}
     * add new unfinished Request in chat
     * @param chat is not null
     * @param command is not null
     */
    public void addUnfinishedRequestForChat(Chat chat, Command command) {
        UnfinishedRequestTelegram unfinishedRequestTelegram = new UnfinishedRequestTelegram();
        unfinishedRequestTelegram.setChat(chat);
        unfinishedRequestTelegram.setCommand(command.getTextCommand());
        add(unfinishedRequestTelegram);
    }

    /**
     * This method using method class{@link TelegramUnfinishedRequestService#findByIdChat(Long)}
     * delete  unfinished Request in chat
     * @param chat is not null
     */
    public void delUnfinishedRequestForChat(Chat chat) {
        UnfinishedRequestTelegram unfinishedRequestTelegram = findByIdChat(chat.getId());
        if (unfinishedRequestTelegram != null) {
            delete(unfinishedRequestTelegram);
        }
    }

    /**
     * This method locate unfinished request by id chat, using, method class
     * Using {@link TelegramUnfinishedRequestService#findByIdChat(Long)}
     * @param chat is not null
     * @return Unfinished request
     */
    public Command findUnfinishedRequestForChat(Chat chat) {
        UnfinishedRequestTelegram unfinishedRequestTelegram = findByIdChat(chat.getId());
        if (unfinishedRequestTelegram != null) {
            return Command.fromStringUpperCase(unfinishedRequestTelegram.getCommand());
        }
        return null;
    }

}
