package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.Photo;
import pro.sky.animalshelter4.entityDto.AnimalDto;
import pro.sky.animalshelter4.exception.PhotoNotFoundException;
import pro.sky.animalshelter4.repository.PhotoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final Logger logger = LoggerFactory.getLogger(PhotoService.class);
    private final TelegramBot telegramBot;

    public PhotoService(PhotoRepository photoRepository, TelegramBot telegramBot) {
        this.photoRepository = photoRepository;
        this.telegramBot = telegramBot;
    }


    public Photo addPhoto(Photo photo) {
        logger.info("Method addPhoto was start for create new Photo");
        return photoRepository.save(photo);
    }

    public Pair<byte[], String> readPhotoFromTelegram(Long id) throws IOException {
        Photo photo = findPhoto(id);
        GetFile getFile = new GetFile(photo.getIdMedia());
        GetFileResponse response = telegramBot.execute(getFile);
        File file = response.file();
        return Pair.of(telegramBot.getFileContent(file).clone(), MediaType.IMAGE_JPEG_VALUE);
    }


    public Photo findPhoto(Long id) {
        logger.info("Method findPhoto was start for find Photo by id");
        return photoRepository.findById(id).
                orElseThrow(() -> new PhotoNotFoundException(String.valueOf(id)));
    }

    public Photo findPhotoByIdPhoto(String idMedia) {
        logger.info("Method findPhoto was start for find Photo by idPhoto");
        return photoRepository.findByIdPhoto(idMedia);
    }


    public Long deletePhoto(Long id) {
        Photo photo = new Photo();
        photo.setId(id);
        return deletePhoto(photo);
    }

    public Long deletePhoto(Photo photo) {
        logger.info("Method deletePhoto was start for delete Photo");
        if (photo.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of Photo");
        }
        Photo photoFound = photoRepository.findById(photo.getId()).
                orElseThrow(() -> new PhotoNotFoundException(String.valueOf(photo.getId())));
        photoRepository.delete(photoFound);
        return photoFound.getId();
    }

    public List<Long> getAllId() {
        logger.info("Method getAllId was start for get all Id of photo");
        return photoRepository.findAll().stream().
                map(Photo::getId).collect(Collectors.toList());
    }

}
