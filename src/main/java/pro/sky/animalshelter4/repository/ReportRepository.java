package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Report;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = "select * from report where report.id_animal_ownership = :idAnimalOwnership and report.report_date = :localDate"
            , nativeQuery = true)
    Report findReportByIdAnimalOwnershipAndDate(Long idAnimalOwnership, LocalDate localDate);

    @Query(value = "select * from report where report.is_open = true and report.is_approve is null limit 1"
            , nativeQuery = true)
    Report getOpenAndNotApproveReport();

    @Query(value = "SELECT DISTINCT on (report.id_animal_ownership) report.* FROM report,animal_ownership where animal_ownership.is_open=true and  animal_ownership.id=report.id_animal_ownership order by report.id_animal_ownership, report.report_date desc"
            , nativeQuery = true)
    List<Report> getLatestUniqueOwnerReportWithOpenAnimalOwnership();
}
