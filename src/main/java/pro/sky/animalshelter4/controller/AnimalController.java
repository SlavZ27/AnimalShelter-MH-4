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
import pro.sky.animalshelter4.entityDto.UserDto;
import pro.sky.animalshelter4.service.AnimalService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("animal")
public class AnimalController {
    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Creating data for AnimalDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalDto.class)
                    )}
            )
    })
    @PostMapping                //POST http://localhost:8080/animal
    public ResponseEntity<AnimalDto> createAnimal(@RequestBody @Valid AnimalDto animalDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalService.createAnimalDto(animalDto));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Getting data for AnimalDto by id.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalDto.class)
                    )}
            )
    })
    @GetMapping("{id}")  //GET http://localhost:8080/animal/1
    public ResponseEntity<AnimalDto> readAnimal(@Parameter(description = "Animal id") @PathVariable Long id) {
        return ResponseEntity.ok(animalService.readAnimal(id));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "We change the data according to the parameters AnimalDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalDto.class)
                    )}
            )
    })
    @PutMapping()               //PUT http://localhost:8080/animal/
    public ResponseEntity<AnimalDto> updateAnimal(@RequestBody @Valid AnimalDto animalDto) {
        return ResponseEntity.ok(animalService.updateAnimal(animalDto));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delete the data in AnimalDto by id",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalDto.class)
                    )}
            )
    })
    @DeleteMapping("{id}")    //DELETE http://localhost:8080/animal/1
    public ResponseEntity<AnimalDto> deleteAnimal(@Parameter(description = "Animal id") @PathVariable Long id) {
        return ResponseEntity.ok(animalService.deleteAnimal(id));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Getting all the data about the AnimalDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AnimalDto[].class)
                    )}
            )
    })
    @GetMapping()  //GET http://localhost:8080/animal/
    public ResponseEntity<Collection<AnimalDto>> getAllAnimals() {
        return ResponseEntity.ok(animalService.getAll());
    }



}
