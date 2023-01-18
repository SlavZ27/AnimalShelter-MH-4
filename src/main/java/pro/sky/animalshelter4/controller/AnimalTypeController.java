package pro.sky.animalshelter4.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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


    @PostMapping      //POST http://localhost:8080/animal_type
    public ResponseEntity<AnimalTypeDto> createAnimalType(@RequestBody @Valid AnimalTypeDto AnimalTypeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(animalTypeService.createAnimalType(AnimalTypeDto));
    }


    @GetMapping("{id}")  //GET http://localhost:8080/animal_type/1
    public ResponseEntity<AnimalTypeDto> readAnimalType(@Parameter(description = "AnimalType id") @PathVariable Long id) {
        return ResponseEntity.ok(animalTypeService.readAnimalType(id));
    }


    @PutMapping()               //PUT http://localhost:8080/animal_type/
    public ResponseEntity<AnimalTypeDto> updateAnimalType(@RequestBody @Valid AnimalTypeDto AnimalTypeDto) {
        return ResponseEntity.ok(animalTypeService.updateAnimalType(AnimalTypeDto));
    }


    @DeleteMapping("{id}")    //DELETE http://localhost:8080/animal_type/1
    public ResponseEntity<AnimalTypeDto> deleteAnimalType(@Parameter(description = "AnimalType id") @PathVariable Long id) {
        return ResponseEntity.ok(animalTypeService.deleteAnimalType(id));
    }


    @GetMapping()  //GET http://localhost:8080/animal_type/
    public ResponseEntity<Collection<AnimalTypeDto>> getAllAnimalType() {
        return ResponseEntity.ok(animalTypeService.getAllDto());
    }

}
