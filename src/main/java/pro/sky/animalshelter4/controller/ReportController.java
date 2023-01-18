package pro.sky.animalshelter4.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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


    @PostMapping                //POST http://localhost:8080/report
    public ResponseEntity<ReportDto> createReport(@RequestBody @Valid ReportDto reportDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReport(reportDto));
    }


    @GetMapping("{id}")  //GET http://localhost:8080/report/1
    public ResponseEntity<ReportDto> readReport(@Parameter(description = "Report id") @PathVariable Long id) {
        return ResponseEntity.ok(reportService.readReport(id));
    }


    @PutMapping()               //PUT http://localhost:8080/report/
    public ResponseEntity<ReportDto> updateReport(@RequestBody @Valid ReportDto reportDto) {
        return ResponseEntity.ok(reportService.updateReport(reportDto));
    }


    @DeleteMapping("{id}")    //DELETE http://localhost:8080/report/1
    public ResponseEntity<ReportDto> deleteReport(@Parameter(description = "Report id") @PathVariable Long id) {
        return ResponseEntity.ok(reportService.deleteReport(id));
    }


    @GetMapping()  //GET http://localhost:8080/report/
    public ResponseEntity<Collection<ReportDto>> getAllReports() {
        return ResponseEntity.ok(reportService.getAll());
    }



}
