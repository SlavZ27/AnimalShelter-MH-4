package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * The class contains methods for saving various content that comes from telegram chat
 */
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

    /**
     * The method saves the photo that is contained in the {@link Update}.
     * From several copies of one photo, the method selects the last one,
     * because it is expected that it will have a higher quality than the others.
     * The photo from {@link Update#message()#photo()} is saved in the file system.
     * The file is saved in {@link TelegramBotContentSaver#materialsDir}.
     * The constant takes the value from the file using constructor.
     * using {@link GetFile}
     * using {@link GetFileResponse}
     * using {@link TelegramBot#execute(BaseRequest)}
     * using {@link TelegramBotContentSaver#getAndCreatePath(Long, String, String)}
     * using {@link Files#write(Path, byte[], OpenOption...)}
     *
     * {@link Update#message()#chat()#id()} must be not null
     * {@link Update#message()#photo()} must be not null
     * @param update
     * @throws IOException
     */
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
            logger.error("ChatId={}; Method savePhoto don't detect format in name of files = {}",
                    idChat, file.filePath());
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


    /**
     * The method checks folder exists in the file system and creates if necessary
     * using {@link java.io.File#mkdir()}
     * @param path must be not null
     * @return true if folder was created, else false
     */
    private boolean checkOrCreateFolder(String path) {
        java.io.File folder = new java.io.File(path);
        if (!folder.exists()) {
            return folder.mkdir();
        }
        return false;
    }

    /**
     * The method checks and, if necessary, creates the full path to the file.
     * The data is taken from the input data. <br>
     * Example of a path: <br> {@link TelegramBotContentSaver#materialsDir} + / + idChat + / +  folderName + / + YYYY.MM.DD_HH.MM. + fileFormat
     * using {@link TelegramBotContentSaver#checkOrCreateFolder(String)}
     * @param idChat
     * @param folderName
     * @param fileFormat
     * @return
     */
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


    /**
     * The method gets the path to the file and outputs characters after the last dot
     * @param filePath must be not null
     * @return file format without dot, if string don't contain dot then return null
     */
    private String parseFileFormat(String filePath) {
        if (filePath.contains(".")) {
            int index = filePath.lastIndexOf(".");
            return filePath.substring(index + 1);
        }
        return null;
    }
}
