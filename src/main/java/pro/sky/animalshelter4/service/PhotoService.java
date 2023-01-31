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
import pro.sky.animalshelter4.entity.Shelter;
import pro.sky.animalshelter4.exception.PhotoNotFoundException;
import pro.sky.animalshelter4.exception.ShelterNotFoundException;
import pro.sky.animalshelter4.repository.PhotoRepository;
import pro.sky.animalshelter4.repository.ShelterRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final Logger logger = LoggerFactory.getLogger(PhotoService.class);
    private final TelegramBot telegramBot;
    private final ShelterRepository shelterRepository;

    public PhotoService(PhotoRepository photoRepository, TelegramBot telegramBot, ShelterRepository shelterRepository) {
        this.photoRepository = photoRepository;
        this.telegramBot = telegramBot;
        this.shelterRepository = shelterRepository;
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
    public Pair<byte[], String> readPhotoFromTelegram(Long id, String shelterDesignation) throws IOException {
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        Photo photo = findPhotoWithShelter(id, shelter);
        return Pair.of(getByteFromTelegram(photo.getIdMedia()), MediaType.IMAGE_JPEG_VALUE);
    }

    private byte[] getByteFromTelegram(String idMedia) throws IOException {
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
    public Photo findPhotoWithShelter(Long id, Shelter shelter) {
        logger.info("Method findPhoto was start for find Photo by id");
        return photoRepository.findByIdAndIdShelter(id, shelter.getId()).
                orElseThrow(() -> new PhotoNotFoundException(String.valueOf(id)));
    }

    /**
     * This method delete photo by id
     * <p>
     *
     * @param id is not null
     *           <r></r>
     * @return delete photo
     */
    public Long deletePhoto(Long id, String shelterDesignation) {
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        Photo photo = new Photo();
        photo.setId(id);
        return deletePhoto(photo, shelter);
    }

    /**
     * This method delete photo by photo id
     *
     * @param photo is not null
     * @return delete photo
     */
    public Long deletePhoto(Photo photo, Shelter shelter) {
        logger.info("Method deletePhoto was start for delete Photo");
        if (photo.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of Photo");
        }
        Photo photoFound = photoRepository.findByIdAndIdShelter(photo.getId(), shelter.getId()).
                orElseThrow(() -> new PhotoNotFoundException(String.valueOf(photo.getId())));
        photoRepository.delete(photoFound);
        return photoFound.getId();
    }

    /**
     * This method was start forget all Id of photo
     *
     * @return delete photo
     */
    public List<Long> getAllId(String shelterDesignation) {
        logger.info("Method getAllId was start for get all Id of photo");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        return photoRepository.findAllByIdShelter(shelter.getId()).stream().
                map(Photo::getId).collect(Collectors.toList());
    }

}
