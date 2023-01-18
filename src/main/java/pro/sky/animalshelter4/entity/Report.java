package pro.sky.animalshelter4.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "id_animal_ownership")
    private AnimalOwnership animalOwnership;
    @JoinColumn(name = "report_date")
    private LocalDate reportDate;
    private String diet;
    private String feeling;
    private String behavior;
    @OneToOne
    @JoinColumn(name = "id_photo")
    private Photo photo;
    @JoinColumn(name = "is_approve")
    private Boolean isApprove;

    public Report() {
    }

    public Boolean isApprove() {
        return isApprove;
    }

    public void setApprove(Boolean approve) {
        isApprove = approve;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnimalOwnership getAnimalOwnership() {
        return animalOwnership;
    }

    public void setAnimalOwnership(AnimalOwnership animalOwnership) {
        this.animalOwnership = animalOwnership;
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

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Report{" +
                ", animalOwnership=" + animalOwnership +
                ", reportDate=" + reportDate +
                ", diet='" + diet + '\'' +
                ", feeling='" + feeling + '\'' +
                ", behavior='" + behavior + '\'' +
                ", photo=" + photo+
                '}';
    }
}
