package pro.sky.animalshelter4.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.service.TelegramBotUpdatesService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final TelegramBotUpdatesService telegramBotUpdatesService;

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    public TelegramBotUpdatesListener(TelegramBot telegramBot, TelegramBotUpdatesService telegramBotUpdatesService) {
        this.telegramBot = telegramBot;
        this.telegramBotUpdatesService = telegramBotUpdatesService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                logger.info("Processing update: {}", update);
                telegramBotUpdatesService.processUpdate(update);
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }
    }
}
