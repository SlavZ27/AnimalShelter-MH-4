package pro.sky.animalshelter4.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.animalshelter4.entityDto.UserDto;
import pro.sky.animalshelter4.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping                //POST http://localhost:8080/user
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    @GetMapping("{id}")  //GET http://localhost:8080/user/1
    public ResponseEntity<UserDto> readUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.readUser(id));
    }

    @PutMapping()               //PUT http://localhost:8080/user/
    public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(userDto));
    }

    @DeleteMapping("{id}")    //DELETE http://localhost:8080/user/1
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @GetMapping()  //GET http://localhost:8080/user/
    public ResponseEntity<Collection<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("volunteers")  //GET http://localhost:8080/user/volunteers
    public ResponseEntity<Collection<UserDto>> getAllVolunteers() {
        return ResponseEntity.ok(userService.getAllVolunteers());
    }

    @GetMapping("clients")  //GET http://localhost:8080/user/clients
    public ResponseEntity<Collection<UserDto>> getAllClients() {
        return ResponseEntity.ok(userService.getAllClients());
    }

}
