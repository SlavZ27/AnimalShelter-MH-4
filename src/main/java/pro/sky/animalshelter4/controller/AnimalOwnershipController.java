package pro.sky.animalshelter4.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

        @PostMapping      //POST http://localhost:8080/animal_ownership
        public ResponseEntity<AnimalOwnershipDto> createAnimalOwnership(@RequestBody @Valid AnimalOwnershipDto animalOwnershipDto) {
            return ResponseEntity.status(HttpStatus.CREATED).body(animalOwnershipService.createAnimalOwnership(animalOwnershipDto));
        }


        @GetMapping("{id}")  //GET http://localhost:8080/animal_ownership/1
        public ResponseEntity<AnimalOwnershipDto> readAnimalOwnership(@Parameter(description = "AnimalOwnership id") @PathVariable Long id) {
            return ResponseEntity.ok(animalOwnershipService.readAnimalOwnership(id));
        }


        @PutMapping()               //PUT http://localhost:8080/animal_ownership/
        public ResponseEntity<AnimalOwnershipDto> updateAnimalOwnership(@RequestBody @Valid AnimalOwnershipDto animalOwnershipDto) {
            return ResponseEntity.ok(animalOwnershipService.updateAnimalOwnership(animalOwnershipDto));
        }


        @DeleteMapping("{id}")    //DELETE http://localhost:8080/animal_ownership/1
        public ResponseEntity<AnimalOwnershipDto> deleteAnimalOwnership(@Parameter(description = "AnimalOwnership id") @PathVariable Long id) {
            return ResponseEntity.ok(animalOwnershipService.deleteAnimalOwnership(id));
        }


        @GetMapping()  //GET http://localhost:8080/animal_ownership/
        public ResponseEntity<Collection<AnimalOwnershipDto>> getAllAnimalOwnerships() {
            return ResponseEntity.ok(animalOwnershipService.getAll());
        }
}
