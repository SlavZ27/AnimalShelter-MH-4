package pro.sky.animalshelter4.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.animalshelter4.entity.Photo;
import pro.sky.animalshelter4.entityDto.AnimalDto;
import pro.sky.animalshelter4.entityDto.ReportDto;
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


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Receives Photo by id.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Photo.class)
                    )}
            )
    })
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

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Receives all id photo.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Photo[].class)
                    )}
            )
    })
    @GetMapping()  //GET http://localhost:8080/photo/
    public ResponseEntity<Collection<Long>> getAllId() {
        return ResponseEntity.ok(photoService.getAllId());
    }



    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Deletes photo by id.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Photo.class)
                    )}
            )
    })
    @DeleteMapping("{id}")    //DELETE http://localhost:8080/photo/1
    public ResponseEntity<Long> deletePhoto(@Parameter(description = "Photo id") @PathVariable Long id) {
        return ResponseEntity.ok(photoService.deletePhoto(id));
    }

}
