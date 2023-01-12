package pro.sky.animalshelter4.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.animalshelter4.entityDto.ChatDto;
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

    @PostMapping                //POST http://localhost:8080/chat
    public ResponseEntity<ChatDto> createChat(@RequestBody @Valid ChatDto chatDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.createChat(chatDto));
    }

    @GetMapping("{id}")  //GET http://localhost:8080/chat/1
    public ResponseEntity<ChatDto> readChat(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.readChat(id));
    }

    @PutMapping()               //PUT http://localhost:8080/chat/
    public ResponseEntity<ChatDto> updateChat(@RequestBody @Valid ChatDto chatDto) {
        return ResponseEntity.ok(chatService.updateChat(chatDto));
    }

    @DeleteMapping("{id}")    //DELETE http://localhost:8080/chat/1
    public ResponseEntity<ChatDto> deleteChat(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.deleteChat(id));
    }

    @GetMapping()  //GET http://localhost:8080/chat/
    public ResponseEntity<Collection<ChatDto>> getAllChats() {
        return ResponseEntity.ok(chatService.getAll());
    }

}
