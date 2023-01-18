package pro.sky.animalshelter4.entityDto;

import java.time.LocalDate;

public class ReportDto {
    private Long id;
    private Long idAnimalOwnership;
    private LocalDate reportDate;
    private String diet;
    private String feeling;
    private String behavior;
    private Long idPhoto;
    private Boolean isApprove;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdAnimalOwnership() {
        return idAnimalOwnership;
    }

    public void setIdAnimalOwnership(Long idAnimalOwnership) {
        this.idAnimalOwnership = idAnimalOwnership;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public Long getIdPhoto() {
        return idPhoto;
    }

    public void setIdPhoto(Long idPhoto) {
        this.idPhoto = idPhoto;
    }

    public Boolean getApprove() {
        return isApprove;
    }

    public void setApprove(Boolean approve) {
        isApprove = approve;
    }
}
