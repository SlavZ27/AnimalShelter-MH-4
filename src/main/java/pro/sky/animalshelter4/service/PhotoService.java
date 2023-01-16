package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Photo;
import pro.sky.animalshelter4.exception.PhotoNotFoundException;
import pro.sky.animalshelter4.repository.PhotoRepository;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final Logger logger = LoggerFactory.getLogger(PhotoService.class);

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }


    public Photo addPhoto(Photo photo) {
        logger.info("Method addPhoto was start for create new Photo");
        return photoRepository.save(photo);
    }

//    public CallRequestDto createCallRequest(CallRequestDto callRequestDto) {
//        logger.info("Method createCallRequest was start for create new CallRequest");
//        return dtoMapperService.toDto(callRequestRepository.save(dtoMapperService.toEntity(callRequestDto)));
//    }

//    public CallRequestDto readCallRequest(Long id) {
//        logger.info("Method readCallRequest was start for find CallRequest by id");
//        return dtoMapperService.toDto(
//                callRequestRepository.findById(id).
//                        orElseThrow(() -> new CallRequestNotFoundException(String.valueOf(id))));
//    }

    public Photo findPhoto(Long id) {
        logger.info("Method findPhoto was start for find Photo by id");
        return photoRepository.findById(id).
                orElseThrow(() -> new PhotoNotFoundException(String.valueOf(id)));
    }

    public Photo findPhotoByIdPhoto(String idMedia) {
        logger.info("Method findPhoto was start for find Photo by idPhoto");
        return photoRepository.findByIdPhoto(idMedia);
    }

//    public CallRequestDto updateCallRequest(CallRequestDto callRequestDto) {
//        logger.info("Method updateCallRequest was start for update callRequest");
//        CallRequest newCallRequest = dtoMapperService.toEntity(callRequestDto);
//        CallRequest oldCallRequest = findCallRequest(newCallRequest.getId());
//        if (oldCallRequest == null) {
//            throw new CallRequestNotFoundException(String.valueOf(newCallRequest.getId()));
//        }
//        oldCallRequest.setOpen(newCallRequest.isOpen());
//        oldCallRequest.setVolunteer(newCallRequest.getVolunteer());
//        oldCallRequest.setClient(newCallRequest.getClient());
//        oldCallRequest.setLocalDateTimeOpen(newCallRequest.getLocalDateTimeOpen());
//        oldCallRequest.setLocalDateTimeClose(newCallRequest.getLocalDateTimeClose());
//        return dtoMapperService.toDto(callRequestRepository.save(oldCallRequest));
//    }

//    public CallRequestDto deleteCallRequest(Long id) {
//        CallRequest callRequest = new CallRequest();
//        callRequest.setId(id);
//        return dtoMapperService.toDto(deleteCallRequest(callRequest));
//    }

    public Photo deletePhoto(Photo photo) {
        logger.info("Method deletePhoto was start for delete Photo");
        if (photo.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of Photo");
        }
        Photo photoFound = photoRepository.findById(photo.getId()).
                orElseThrow(() -> new PhotoNotFoundException(String.valueOf(photo.getId())));
        photoRepository.delete(photoFound);
        return photoFound;
    }
}
