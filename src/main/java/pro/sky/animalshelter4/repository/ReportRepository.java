package pro.sky.animalshelter4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.Report;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = "select * from report where report.id_animal_ownership=:idAnimalOwnership and report.report_date = :localDate and report.id_shelter=:idShelter"
            , nativeQuery = true)
    Report findReportByIdAnimalOwnershipAndDateWithShelter(Long idAnimalOwnership, LocalDate localDate, Long idShelter);

    @Query(value = "select report.* from report where report.id_shelter=:idShelter and report.is_approve is null limit 1"
            , nativeQuery = true)
    Report getOpenAndNotApproveReportWithShelter(Long idShelter);

    @Query(value = "select report.* from report where report.id_shelter=:idShelter and report.id=:id"
            , nativeQuery = true)
    Optional<Report> getByIdWithShelter(Long id, Long idShelter);

    @Query(value = "SELECT DISTINCT on (report.id_animal_ownership) report.* FROM report,animal_ownership where report.id_shelter=:idShelter and animal_ownership.is_open=true and  animal_ownership.id=report.id_animal_ownership order by report.id_animal_ownership, report.report_date desc"
            , nativeQuery = true)
    List<Report> getLatestUniqueOwnerReportWithOpenAnimalOwnershipWithShelter(Long idShelter);

    @Query(value = "select report.* from report where report.id_shelter=:idShelter"
            , nativeQuery = true)
    List<Report> getAllWithShelter(Long idShelter);
}
