package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.TimeUnit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramCalendarService {
    private final static String CAPTION_SEND_YEAR = "Select year";
    private final static String CAPTION_SEND_MONTH = "Select month";
    private final static String CAPTION_SEND_DAY = "Select day";
    private final TelegramBotSenderService telegramBotSenderService;
    private final Logger logger = LoggerFactory.getLogger(TelegramCalendarService.class);

    public TelegramCalendarService(TelegramBotSenderService telegramBotSenderService) {
        this.telegramBotSenderService = telegramBotSenderService;
    }

    public void calendarStart(Long idChat, Command subCommand) {
        logger.info("ChatId={}; Method calendarStart was start for start generate calendar for command = {}", idChat, subCommand.getTextCommand());
        logger.debug("ChatId={}; Method calendarStart going to start method sendYear", idChat);
        sendYear(idChat, subCommand);
    }

    public void processNext(Long idChat, String request) {
        logger.info("ChatId={}; Method processNext was start for continue generate calendar", idChat);

        String[] requestMas = request.split(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
        TimeUnit timeUnit = TimeUnit.valueOf(requestMas[requestMas.length - 2]);

        switch (timeUnit) {
            case YEAR:
                logProcessNextDetectedValidCommand(timeUnit.getTitle(), idChat);
                sendMonth(
                        idChat,
                        Command.fromStringUpperCase(requestMas[1]),
                        Integer.parseInt(requestMas[requestMas.length - 1])
                );
                break;
            case MONTH:
                logProcessNextDetectedValidCommand(timeUnit.getTitle(), idChat);
                sendDay(
                        idChat,
                        Command.fromStringUpperCase(requestMas[1]),
                        Integer.parseInt(requestMas[requestMas.length - 3]),
                        Integer.parseInt(requestMas[requestMas.length - 1])
                );
                break;
        }
    }

    private void sendDay(Long idChat, Command subCommand, int year, int month) {
        logSendMessageForContinueGenerateCalendar("sendMinutes", idChat);
        List<String> tableDay = new ArrayList<>();
        int lengthMonth = LocalDate.of(year, month, 1).lengthOfMonth();

        for (int i = 1; i <= lengthMonth; i++) {
            tableDay.add(String.valueOf(i));
        }
        int tableHeight = 6;
        if (lengthMonth < 31) {
            tableHeight = 5;
        }
        telegramBotSenderService.sendButtonsWithOneData(idChat, "Select minutes",
                getRequestFinish(subCommand, year, month),
                tableDay, tableDay, 6, tableHeight);
    }

    private void sendMonth(Long idChat, Command subCommand, int year) {
        logSendMessageForContinueGenerateCalendar("sendMonth", idChat);
        List<String> tableMonth = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            tableMonth.add(String.valueOf(i));
        }
        telegramBotSenderService.sendButtonsWithOneData(idChat, "Select month",
                getRequestContinueForButton(subCommand, year),
                tableMonth,
                tableMonth,
                3, 4);
    }

    private void sendYear(Long idChat, Command subCommand) {
        logSendMessageForContinueGenerateCalendar("sendYear", idChat);
        List<String> tableYear = new ArrayList<>();
        int yearNow = LocalDateTime.now().getYear();
        byte yearCount = 20;
        for (int i = yearNow - yearCount / 2; i < yearNow + yearCount / 2 + yearCount; i++) {
            tableYear.add(String.valueOf(i));
        }
        telegramBotSenderService.sendButtonsWithOneData(idChat, "Select year",
                getRequestContinueForButton(subCommand, -1),
                tableYear,
                tableYear,
                4, 1);
    }

    private void logProcessNextDetectedValidCommand(String timeUnitTitle, long idChat) {
        logger.info("ChatId={}; Method processNext was detected command: {}", idChat, timeUnitTitle);
    }

    private void logSendMessageForContinueGenerateCalendar(String method, long idChat) {
        logger.info("ChatId={}; Method {} was start for send message for continue generate calendar query",
                idChat, method);
    }

    private String getRequestFinish(Command subCommand, int year, int month) {
        StringBuilder sb = new StringBuilder();
        sb.append(subCommand.getTextCommand());
        sb.append(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
        sb.append(TimeUnit.YEAR);
        if (year > 0) {
            sb.append(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
            sb.append(year);
            sb.append(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
            sb.append(TimeUnit.MONTH);
            if (month > 0) {
                sb.append(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
                sb.append(month);
                sb.append(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
                sb.append(TimeUnit.DAY);
            }
        }
        return sb.toString();
    }

    private String getRequestContinueForButton(Command subCommand, int year) {
        StringBuilder sb = new StringBuilder();
        sb.append(Command.CALENDAR.getTextCommand());
        sb.append(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
        sb.append(subCommand.getTextCommand());
        sb.append(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
        sb.append(TimeUnit.YEAR);
        if (year > 0) {
            sb.append(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
            sb.append(year);
            sb.append(TelegramBotSenderService.REQUEST_SPLIT_SYMBOL);
            sb.append(TimeUnit.MONTH);
        }
        return sb.toString();
    }

}
