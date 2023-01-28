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
import pro.sky.animalshelter4.entityDto.UserDto;
import pro.sky.animalshelter4.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("{shelterDesignation}/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Creating data for UserDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )}
            )
    })
    @PostMapping                //POST http://localhost:8080/user
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto,
                                              @PathVariable String shelterDesignation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto, shelterDesignation));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Getting data for UserDto by id.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )}
            )
    })
    @GetMapping("{id}")  //GET http://localhost:8080/user/1
    public ResponseEntity<UserDto> readUser(@Parameter(description = "user Id") @PathVariable Long id,
                                            @PathVariable String shelterDesignation) {
        return ResponseEntity.ok(userService.readUser(id, shelterDesignation));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "We change the data according to the parameters UserDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )}
            )
    })
    @PutMapping()               //PUT http://localhost:8080/user/
    public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserDto userDto,
                                              @PathVariable String shelterDesignation) {
        return ResponseEntity.ok(userService.updateUser(userDto, shelterDesignation));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delete the data in UserDto by id",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                    )}
            )
    })
    @DeleteMapping("{id}")    //DELETE http://localhost:8080/user/1
    public ResponseEntity<UserDto> deleteUser(@Parameter(description = "user Id") @PathVariable Long id,
                                              @PathVariable String shelterDesignation) {
        return ResponseEntity.ok(userService.deleteUser(id, shelterDesignation));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Getting all volunteers with data in UserDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto[].class)
                    )}
            )
    })
    @GetMapping("volunteers")  //GET http://localhost:8080/user/volunteers
    public ResponseEntity<Collection<UserDto>> getAllVolunteers(@PathVariable String shelterDesignation) {
        return ResponseEntity.ok(userService.getAllVolunteers( shelterDesignation));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Getting all clients with data according to UserDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto[].class)
                    )}
            )
    })
    @GetMapping("clients")  //GET http://localhost:8080/user/clients
    public ResponseEntity<Collection<UserDto>> getAllClients(@PathVariable String shelterDesignation) {
        return ResponseEntity.ok(userService.getAllClientsDto( shelterDesignation));
    }

}
