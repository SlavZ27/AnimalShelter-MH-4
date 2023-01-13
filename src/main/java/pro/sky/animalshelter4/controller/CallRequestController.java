package pro.sky.animalshelter4.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.animalshelter4.entityDto.CallRequestDto;
import pro.sky.animalshelter4.service.CallRequestService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("call_request")
public class CallRequestController {

    private final CallRequestService callRequestService;

    public CallRequestController(CallRequestService callRequestService) {
        this.callRequestService = callRequestService;
    }

    @PostMapping                //POST http://localhost:8080/call_request
    public ResponseEntity<CallRequestDto> createCallRequest(@RequestBody @Valid CallRequestDto callRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(callRequestService.createCallRequest(callRequestDto));
    }

    @GetMapping("{id}")  //GET http://localhost:8080/call_request/1
    public ResponseEntity<CallRequestDto> readCallRequest(@PathVariable Long id) {
        return ResponseEntity.ok(callRequestService.readCallRequest(id));
    }

    @PutMapping()               //PUT http://localhost:8080/call_request/
    public ResponseEntity<CallRequestDto> updateCallRequest(@RequestBody @Valid CallRequestDto callRequestDto) {
        return ResponseEntity.ok(callRequestService.updateCallRequest(callRequestDto));
    }

    @DeleteMapping("{id}")    //DELETE http://localhost:8080/call_request/1
    public ResponseEntity<CallRequestDto> deleteCallRequest(@PathVariable Long id) {
        return ResponseEntity.ok(callRequestService.deleteCallRequest(id));
    }

    @GetMapping()  //GET http://localhost:8080/call_request/
    public ResponseEntity<Collection<CallRequestDto>> getAllCallRequest() {
        return ResponseEntity.ok(callRequestService.getAll());
    }

    @GetMapping("volunteer/{id}")  //GET http://localhost:8080/call_request/volunteer/1
    public ResponseEntity<Collection<CallRequestDto>> getAllCallRequestVolunteer(@PathVariable Long id) {
        return ResponseEntity.ok(callRequestService.getAllOpenCallRequestVolunteer(id));
    }

    @GetMapping("client/{id}")  //GET http://localhost:8080/call_request/client/1
    public ResponseEntity<Collection<CallRequestDto>> getAllCallRequestClient(@PathVariable Long id) {
        return ResponseEntity.ok(callRequestService.getAllOpenCallRequestClient(id));
    }

    @GetMapping("open")  //GET http://localhost:8080/call_request/open/
    public ResponseEntity<Collection<CallRequestDto>> getAllOpenCallRequest() {
        return ResponseEntity.ok(callRequestService.getAllOpenCallRequest());
    }

    @GetMapping("close")  //GET http://localhost:8080/call_request/close/
    public ResponseEntity<Collection<CallRequestDto>> getAllCloseCallRequest() {
        return ResponseEntity.ok(callRequestService.getAllCloseCallRequest());
    }
}
