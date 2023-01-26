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
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entityDto.CallRequestDto;
import pro.sky.animalshelter4.service.CallRequestService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("{shelterDesignation}/call_request")
public class CallRequestController {

    private final CallRequestService callRequestService;

    public CallRequestController(CallRequestService callRequestService) {
        this.callRequestService = callRequestService;
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Creates call requests.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CallRequest.class)
                    )}
            )
    })
    @PostMapping                //POST http://localhost:8080/call_request
    public ResponseEntity<CallRequestDto> createCallRequest(@RequestBody @Valid CallRequestDto callRequestDto,
                                                            @PathVariable String shelterDesignation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(callRequestService.createCallRequest(
                callRequestDto,
                shelterDesignation));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Receives all call requests by id.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CallRequest.class)
                    )}
            )
    })
    @GetMapping("{id}")  //GET http://localhost:8080/call_request/1
    public ResponseEntity<CallRequestDto> readCallRequest(@Parameter(description = "user id") @PathVariable Long id,
                                                          @PathVariable String shelterDesignation) {
        return ResponseEntity.ok(callRequestService.readCallRequest(id, shelterDesignation));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Updates call requests.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CallRequest.class)
                    )}
            )
    })
    @PutMapping()               //PUT http://localhost:8080/call_request/
    public ResponseEntity<CallRequestDto> updateCallRequest(@RequestBody @Valid CallRequestDto callRequestDto,
                                                            @PathVariable String shelterDesignation) {
        return ResponseEntity.ok(callRequestService.updateCallRequest(callRequestDto, shelterDesignation));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Deletes call requests by id.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CallRequest.class)
                    )}
            )
    })
    @DeleteMapping("{id}")    //DELETE http://localhost:8080/call_request/1
    public ResponseEntity<CallRequestDto> deleteCallRequest(@Parameter(description = "user id") @PathVariable Long id,
                                                            @PathVariable String shelterDesignation) {
        return ResponseEntity.ok(callRequestService.deleteCallRequest(id, shelterDesignation));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Receives all open customer requests that are sent to the volunteer by the volunteer id",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CallRequest[].class)
                    )}
            )
    })
    @GetMapping("volunteer/{id}")  //GET http://localhost:8080/call_request/volunteer/1
    public ResponseEntity<Collection<CallRequestDto>> getAllCallRequestVolunteer(@Parameter(description = "user id") @PathVariable Long id,
                                                                                 @PathVariable String shelterDesignation) {
        return ResponseEntity.ok(callRequestService.getAllOpenCallRequestVolunteer(id, shelterDesignation));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Receives all open client requests for a call by client id.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CallRequest[].class)
                    )}
            )
    })
    @GetMapping("client/{id}")  //GET http://localhost:8080/call_request/client/1
    public ResponseEntity<Collection<CallRequestDto>> getAllCallRequestClient(@PathVariable Long id,
                                                                              @PathVariable String shelterDesignation) {
        return ResponseEntity.ok(callRequestService.getAllOpenCallRequestClient(id, shelterDesignation));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Opens all requests that were sent to volunteers.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CallRequest[].class)
                    )}
            )
    })
    @GetMapping("open")  //GET http://localhost:8080/call_request/open/
    public ResponseEntity<Collection<CallRequestDto>> getAllOpenCallRequest(@PathVariable String shelterDesignation) {
        return ResponseEntity.ok(callRequestService.getAllOpenCallRequest(shelterDesignation));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Closes all requests that were sent to volunteers.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CallRequest[].class)
                    )}
            )
    })

    @GetMapping("close")  //GET http://localhost:8080/call_request/close/
    public ResponseEntity<Collection<CallRequestDto>> getAllCloseCallRequest(@PathVariable String shelterDesignation) {
        return ResponseEntity.ok(callRequestService.getAllCloseCallRequest(shelterDesignation));
    }
}
