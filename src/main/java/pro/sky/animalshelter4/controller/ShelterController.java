package pro.sky.animalshelter4.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.animalshelter4.entityDto.ShelterDto;
import pro.sky.animalshelter4.service.ShelterService;

import javax.validation.Valid;
import java.util.Collection;
@RestController
@RequestMapping("shelter")
public class ShelterController {

    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Creating data for ShelterDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShelterDto.class)
                    )}
            )
    })
    @PostMapping      //POST http://localhost:8080/shelter
    public ResponseEntity<ShelterDto> createShelter(@RequestBody @Valid ShelterDto ShelterDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shelterService.createShelter(ShelterDto));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Getting data for ShelterDto by id.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShelterDto.class)
                    )}
            )
    })
    @GetMapping("{id}")  //GET http://localhost:8080/shelter/1
    public ResponseEntity<ShelterDto> readShelter(@Parameter(description = "Shelter id") @PathVariable Long id) {
        return ResponseEntity.ok(shelterService.readShelter(id));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "We change the data according to the parameters ShelterDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShelterDto.class)
                    )}
            )
    })
    @PutMapping()               //PUT http://localhost:8080/shelter/
    public ResponseEntity<ShelterDto> updateShelter(@RequestBody @Valid ShelterDto ShelterDto) {
        return ResponseEntity.ok(shelterService.updateShelter(ShelterDto));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delete the data in ShelterDto by id",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShelterDto.class)
                    )}
            )
    })
    @DeleteMapping("{id}")    //DELETE http://localhost:8080/shelter/1
    public ResponseEntity<ShelterDto> deleteShelter(@Parameter(description = "Shelter id") @PathVariable Long id) {
        return ResponseEntity.ok(shelterService.deleteShelter(id));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Getting all the data about the type ShelterDto",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ShelterDto[].class)
                    )}
            )
    })
    @GetMapping()  //GET http://localhost:8080/shelter/
    public ResponseEntity<Collection<ShelterDto>> getAllShelter() {
        return ResponseEntity.ok(shelterService.getAllDto());
    }

}
