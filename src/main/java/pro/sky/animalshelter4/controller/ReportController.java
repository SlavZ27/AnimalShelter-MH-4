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
import pro.sky.animalshelter4.entityDto.ReportDto;
import pro.sky.animalshelter4.service.ReportService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("report")
public class ReportController {
    
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Creates report.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReportDto.class)
                    )}
            )
    })
    @PostMapping                //POST http://localhost:8080/report
    public ResponseEntity<ReportDto> createReport(@RequestBody @Valid ReportDto reportDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReport(reportDto));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Receives report by id.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReportDto.class)
                    )}
            )
    })
    @GetMapping("{id}")  //GET http://localhost:8080/report/1
    public ResponseEntity<ReportDto> readReport(@Parameter(description = "Report id") @PathVariable Long id) {
        return ResponseEntity.ok(reportService.readReport(id));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Updates report.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReportDto.class)
                    )}
            )
    })
    @PutMapping()               //PUT http://localhost:8080/report/
    public ResponseEntity<ReportDto> updateReport(@RequestBody @Valid ReportDto reportDto) {
        return ResponseEntity.ok(reportService.updateReport(reportDto));
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Deletes report by id.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReportDto.class)
                    )}
            )
    })
    @DeleteMapping("{id}")    //DELETE http://localhost:8080/report/1
    public ResponseEntity<ReportDto> deleteReport(@Parameter(description = "Report id") @PathVariable Long id) {
        return ResponseEntity.ok(reportService.deleteReport(id));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Receives all report.",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReportDto[].class)
                    )}
            )
    })
    @GetMapping()  //GET http://localhost:8080/report/
    public ResponseEntity<Collection<ReportDto>> getAllReports() {
        return ResponseEntity.ok(reportService.getAll());
    }



}
