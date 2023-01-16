package pro.sky.animalshelter4.service;

import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.UnfinishedRequestTelegram;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.repository.UnfinishedRequestTelegramRepository;

@Service
public class TelegramUnfinishedRequestService {
    private final UnfinishedRequestTelegramRepository unfinishedRequestTelegramRepository;

    public TelegramUnfinishedRequestService(UnfinishedRequestTelegramRepository unfinishedRequestTelegramRepository) {
        this.unfinishedRequestTelegramRepository = unfinishedRequestTelegramRepository;
    }

    private UnfinishedRequestTelegram add(UnfinishedRequestTelegram unfinishedRequestTelegram) {
        if (unfinishedRequestTelegramRepository.findByIdChat(unfinishedRequestTelegram.getChat().getId()) == null) {
            return unfinishedRequestTelegramRepository.save(unfinishedRequestTelegram);
        }
        return null;
    }

    private UnfinishedRequestTelegram read(Long id) {
        return unfinishedRequestTelegramRepository.findById(id).orElse(null);
    }

    public UnfinishedRequestTelegram findByIdChat(Long idChat) {
        return unfinishedRequestTelegramRepository.findByIdChat(idChat);
    }

    private void delete(Long id) {
        if (read(id) != null) {
            unfinishedRequestTelegramRepository.deleteById(id);
        }
    }

    private void delete(UnfinishedRequestTelegram unfinishedRequestTelegram) {
        unfinishedRequestTelegramRepository.delete(unfinishedRequestTelegram);

    }

    public void addUnfinishedRequestForChat(Chat chat, Command command) {
        UnfinishedRequestTelegram unfinishedRequestTelegram = new UnfinishedRequestTelegram();
        unfinishedRequestTelegram.setChat(chat);
        unfinishedRequestTelegram.setCommand(command.getTextCommand());
        add(unfinishedRequestTelegram);
    }

    public void delUnfinishedRequestForChat(Chat chat) {
        UnfinishedRequestTelegram unfinishedRequestTelegram = findByIdChat(chat.getId());
        if (unfinishedRequestTelegram != null) {
            delete(unfinishedRequestTelegram);
        }
    }

    public Command findUnfinishedRequestForChat(Chat chat) {
        UnfinishedRequestTelegram unfinishedRequestTelegram = findByIdChat(chat.getId());
        if (unfinishedRequestTelegram != null) {
            return Command.fromStringUpperCase(unfinishedRequestTelegram.getCommand());
        }
        return null;
    }

}
