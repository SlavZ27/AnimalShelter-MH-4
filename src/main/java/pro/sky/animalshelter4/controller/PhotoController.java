package pro.sky.animalshelter4.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.animalshelter4.entityDto.AnimalDto;
import pro.sky.animalshelter4.service.PhotoService;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("photo")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }


    @GetMapping("{id}")  //GET http://localhost:8080/photo/1
    public ResponseEntity<byte[]> readPhoto(@Parameter(description = "Photo id") @PathVariable Long id) throws IOException {
        Pair<byte[], String> pair = photoService.readPhotoFromTelegram(id);
        return readPair(pair);
    }

    private ResponseEntity<byte[]> readPair(Pair<byte[], String> pair) {
        return ResponseEntity.ok()
                .contentLength(pair.getFirst().length)
                .contentType(MediaType.parseMediaType(pair.getSecond()))
                .body(pair.getFirst());
    }

    @GetMapping()  //GET http://localhost:8080/photo/
    public ResponseEntity<Collection<Long>> getAllId() {
        return ResponseEntity.ok(photoService.getAllId());
    }



    @DeleteMapping("{id}")    //DELETE http://localhost:8080/photo/1
    public ResponseEntity<Long> deletePhoto(@Parameter(description = "Photo id") @PathVariable Long id) {
        return ResponseEntity.ok(photoService.deletePhoto(id));
    }

}
