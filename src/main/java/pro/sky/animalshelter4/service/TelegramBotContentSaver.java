package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
public class TelegramBotContentSaver {
    private final String materialsDir;
    private final Logger logger = LoggerFactory.getLogger(TelegramBotContentSaver.class);
    private final TelegramBotSenderService telegramBotSenderService;
    private final TelegramBot telegramBot;

    public TelegramBotContentSaver(@Value("${path.to.materials.folder}") String materialsDir, TelegramBotSenderService telegramBotSenderService, TelegramBot telegramBot) {
        this.materialsDir = materialsDir;
        this.telegramBotSenderService = telegramBotSenderService;
        this.telegramBot = telegramBot;
    }

    public void savePhoto(Update update) throws IOException {
        Long idChat = update.message().chat().id();
        logger.info("ChatId={}; Method savePhoto was start for save receive photo", idChat);
        int maxPhotoIndex = update.message().photo().length - 1;
        logger.debug("ChatId={}; Method savePhoto go to save photo: width = {}, heugh = {}, file size = {}",
                idChat,
                update.message().photo()[maxPhotoIndex].width(),
                update.message().photo()[maxPhotoIndex].height(),
                update.message().photo()[maxPhotoIndex].fileSize());

        GetFile getFile = new GetFile(update.message().photo()[maxPhotoIndex].fileId());
        GetFileResponse response = telegramBot.execute(getFile);
        File file = response.file();
        String fileFormat = parseFileFormat(file.filePath());
        if (fileFormat == null) {
            logger.error("ChatId={}; Method savePhoto don't detect format in name of files = {}", idChat, file.filePath());
            return;
        }
        Path myPath = getAndCreatePath(idChat, "report-1", fileFormat);
        if (myPath == null) {
            logger.error("ChatId={}; Method savePhoto can't find or create folder for save photo", idChat);
            return;
        }
        Files.write(myPath, telegramBot.getFileContent(file).clone());
        logger.info("ChatId={}; Method savePhoto successfully received the photo", idChat);
    }

    private boolean checkOrCreateFolder(String path) {
        java.io.File folder = new java.io.File(path);
        if (!folder.exists()) {
            return folder.mkdir();
        }
        return false;
    }

    private Path getAndCreatePath(Long idChat, String folderName, String fileFormat) {
        StringBuilder pathFolder = new StringBuilder(materialsDir + "/");
        if (checkOrCreateFolder(pathFolder.toString())) {
            pathFolder.append(idChat);
            pathFolder.append("/");
            if (checkOrCreateFolder(pathFolder.toString())) {
                pathFolder.append(folderName);
                pathFolder.append("/");
                if (checkOrCreateFolder(pathFolder.toString())) {
                    LocalDateTime ldt = LocalDateTime.now();
                    String fileName =
                            ldt.getYear() + "." + ldt.getMonthValue() + "." + ldt.getDayOfMonth() + "_"
                                    + ldt.getHour() + "." + ldt.getMinute()
                                    + "." + fileFormat;
                    return Path.of(pathFolder + fileName);
                }
            }
        }
        return null;
    }

    private String parseFileFormat(String filePath) {
        if (filePath.contains(".")) {
            int index = filePath.lastIndexOf(".");
            return filePath.substring(index + 1);
        }
        return null;
    }
}
