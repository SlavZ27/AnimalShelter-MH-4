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
import pro.sky.animalshelter4.service.AnimalOwnershipService;

import javax.validation.Valid;
import java.util.Collection;

    @RestController
    @RequestMapping("animal_ownership")
    public class AnimalOwnershipController {
        private final AnimalOwnershipService animalOwnershipService;

        public AnimalOwnershipController(AnimalOwnershipService animalOwnershipService) {
            this.animalOwnershipService = animalOwnershipService;
        }

        @ApiResponses({
                @ApiResponse(
                        responseCode = "201",
                        description = "Creating data for AnimalOwnershipDto.",
                        content = {@Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = AnimalOwnershipDto.class)
                        )}
                )
        })
        @PostMapping      //POST http://localhost:8080/animal_ownership
        public ResponseEntity<AnimalOwnershipDto> createAnimalOwnership(@RequestBody @Valid AnimalOwnershipDto animalOwnershipDto) {
            return ResponseEntity.status(HttpStatus.CREATED).body(animalOwnershipService.createAnimalOwnership(animalOwnershipDto));
        }


        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                            description = "Getting data for AnimalOwnershipDto by id.",
                        content = {@Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = AnimalOwnershipDto.class)
                        )}
                )
        })
        @GetMapping("{id}")  //GET http://localhost:8080/animal_ownership/1
        public ResponseEntity<AnimalOwnershipDto> readAnimalOwnership(@Parameter(description = "AnimalOwnership id") @PathVariable Long id) {
            return ResponseEntity.ok(animalOwnershipService.readAnimalOwnership(id));
        }


        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "We change the data according to the parameters AnimalOwnershipDto.",
                        content = {@Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = AnimalOwnershipDto.class)
                        )}
                )
        })
        @PutMapping()               //PUT http://localhost:8080/animal_ownership/
        public ResponseEntity<AnimalOwnershipDto> updateAnimalOwnership(@RequestBody @Valid AnimalOwnershipDto animalOwnershipDto) {
            return ResponseEntity.ok(animalOwnershipService.updateAnimalOwnership(animalOwnershipDto));
        }


        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "Delete the data in AnimalOwnershipDto by id",
                        content = {@Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = AnimalOwnershipDto.class)
                        )}
                )
        })
        @DeleteMapping("{id}")    //DELETE http://localhost:8080/animal_ownership/1
        public ResponseEntity<AnimalOwnershipDto> deleteAnimalOwnership(@Parameter(description = "AnimalOwnership id") @PathVariable Long id) {
            return ResponseEntity.ok(animalOwnershipService.deleteAnimalOwnership(id));
        }


        @ApiResponses({
                @ApiResponse(
                        responseCode = "200",
                        description = "Getting all the data about the AnimalOwnershipDto",
                        content = {@Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = AnimalOwnershipDto[].class)
                        )}
                )
        })
        @GetMapping()  //GET http://localhost:8080/animal_ownership/
        public ResponseEntity<Collection<AnimalOwnershipDto>> getAllAnimalOwnerships() {
            return ResponseEntity.ok(animalOwnershipService.getAll());
        }
}
