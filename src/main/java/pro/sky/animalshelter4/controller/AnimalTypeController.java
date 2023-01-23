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
import pro.sky.animalshelter4.entityDto.AnimalDto;
import pro.sky.animalshelter4.entityDto.AnimalOwnershipDto;
import pro.sky.animalshelter4.entityDto.AnimalTypeDto;
import pro.sky.animalshelter4.service.AnimalTypeService;

import javax.validation.Valid;
import java.util.Collection;
@RestController
@RequestMapping("animal_type")
public class AnimalTypeController {

    private final AnimalTypeService animalTypeService;

    public AnimalTypeController(AnimalTypeService animalTypeService) {
        this.animalTypeService = animalTypeService;
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Creating data for AnimalTypeDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalTypeDto.class)
                    )}
            )
    })
    @PostMapping      //POST http://localhost:8080/animal_type
    public ResponseEntity<AnimalTypeDto> createAnimalType(@RequestBody @Valid AnimalTypeDto AnimalTypeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalTypeService.createAnimalType(AnimalTypeDto));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Getting data for AnimalTypeDto by id.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalTypeDto.class)
                    )}
            )
    })
    @GetMapping("{id}")  //GET http://localhost:8080/animal_type/1
    public ResponseEntity<AnimalTypeDto> readAnimalType(@Parameter(description = "AnimalType id") @PathVariable Long id) {
        return ResponseEntity.ok(animalTypeService.readAnimalType(id));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "We change the data according to the parameters AnimalTypeDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalTypeDto.class)
                    )}
            )
    })
    @PutMapping()               //PUT http://localhost:8080/animal_type/
    public ResponseEntity<AnimalTypeDto> updateAnimalType(@RequestBody @Valid AnimalTypeDto AnimalTypeDto) {
        return ResponseEntity.ok(animalTypeService.updateAnimalType(AnimalTypeDto));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delete the data in AnimalTypeDto by id",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalTypeDto.class)
                    )}
            )
    })
    @DeleteMapping("{id}")    //DELETE http://localhost:8080/animal_type/1
    public ResponseEntity<AnimalTypeDto> deleteAnimalType(@Parameter(description = "AnimalType id") @PathVariable Long id) {
        return ResponseEntity.ok(animalTypeService.deleteAnimalType(id));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Getting all the data about the type AnimalTypeDto",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalTypeDto[].class)
                    )}
            )
    })
    @GetMapping()  //GET http://localhost:8080/animal_type/
    public ResponseEntity<Collection<AnimalTypeDto>> getAllAnimalType() {
        return ResponseEntity.ok(animalTypeService.getAllDto());
    }

}
