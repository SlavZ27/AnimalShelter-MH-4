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

import pro.sky.animalshelter4.entityDto.ChatDto;
import pro.sky.animalshelter4.entityDto.UserDto;
import pro.sky.animalshelter4.service.ChatService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "We record the following data from the client 's chat from the bot 's ChatDto",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChatDto.class)
                    )}
            )
    })
    @PostMapping                //POST http://localhost:8080/chat
    public ResponseEntity<ChatDto> createChat(@RequestBody @Valid ChatDto chatDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.createChat(chatDto));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Getting data for ChatDto by id.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChatDto.class)
                    )}
            )
    })
    @GetMapping("{id}")  //GET http://localhost:8080/chat/1
    public ResponseEntity<ChatDto> readChat(@Parameter(description = "chat id") @PathVariable Long id) {
        return ResponseEntity.ok(chatService.readChat(id));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "We change the data according to the parameters ChatDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChatDto.class)
                    )}
            )
    })
    @PutMapping()               //PUT http://localhost:8080/chat/
    public ResponseEntity<ChatDto> updateChat(@RequestBody @Valid ChatDto chatDto) {
        return ResponseEntity.ok(chatService.updateChat(chatDto));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delete the data in UserDto by id",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChatDto.class)
                    )}
            )
    })
    @DeleteMapping("{id}")    //DELETE http://localhost:8080/chat/1
    public ResponseEntity<ChatDto> deleteChat(@Parameter(description = "chat id") @PathVariable Long id) {
        return ResponseEntity.ok(chatService.deleteChat(id));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Getting all users with data according to ChatDto.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChatDto[].class)
                    )}
            )
    })
    @GetMapping()  //GET http://localhost:8080/chat/
    public ResponseEntity<Collection<ChatDto>> getAllChats() {
        return ResponseEntity.ok(chatService.getAll());
    }

}
