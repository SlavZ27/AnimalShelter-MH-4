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


    /**
     * This method add new Photo and save photo
     * <p>
     *
     * @param photo is not null
     *              <r>
     * @return photo
     */
    public Photo addPhoto(Photo photo) {
        logger.info("Method addPhoto was start for create new Photo");
        return photoRepository.save(photo);
    }

    /**
     * This method read photo is telegram
     * <p>
     *
     * @param id is not null
     *           <r>
     * @return photo
     * <t>
     * @throws IOException
     */
    public Pair<byte[], String> readPhotoFromTelegram(Long id) throws IOException {
        Photo photo = findPhoto(id);
        return Pair.of(getByteFromTelegram(photo.getIdMedia()), MediaType.IMAGE_JPEG_VALUE);
    }

    public byte[] getByteFromTelegram(String idMedia) throws IOException {
        GetFile getFile = new GetFile(idMedia);
        GetFileResponse response = telegramBot.execute(getFile);
        File file = response.file();
        return telegramBot.getFileContent(file).clone();
    }


    /**
     * This method locate photo by id, using method repository
     * Using{@link PhotoRepository#findById(Object)}
     * <p>
     *
     * @param id is not null
     *           <t>
     * @return photo
     * @throws IOException PhotoNotFoundException(id)
     *                     <r>
     */
    public Photo findPhoto(Long id) {
        logger.info("Method findPhoto was start for find Photo by id");
        return photoRepository.findById(id).
                orElseThrow(() -> new PhotoNotFoundException(String.valueOf(id)));
    }

    /**
     * This method locate photo by Photo id, using method repository
     * Using{@link PhotoRepository#findByIdPhoto(String)}
     * <p>
     *
     * @param idMedia is not null
     *                <r></r>
     * @return photo
     */
    public Photo findPhotoByIdPhoto(String idMedia) {
        logger.info("Method findPhoto was start for find Photo by idPhoto");
        return photoRepository.findByIdPhoto(idMedia);
    }


    /**
     * This method delete photo by id
     * <p>
     *
     * @param id is not null
     *           <r></r>
     * @return delete photo
     */
    public Long deletePhoto(Long id) {
        Photo photo = new Photo();
        photo.setId(id);
        return deletePhoto(photo);
    }

    /**
     * This method delete photo by photo id
     *
     * @param photo is not null
     * @return delete photo
     */
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

    /**
     * This method was start forget all Id of photo
     *
     * @return delete photo
     */
    public List<Long> getAllId() {
        logger.info("Method getAllId was start for get all Id of photo");
        return photoRepository.findAll().stream().
                map(Photo::getId).collect(Collectors.toList());
    }

}
