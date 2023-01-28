package pro.sky.animalshelter4.entity;

import pro.sky.animalshelter4.exception.AnimalOwnershipBadParameterException;
import pro.sky.animalshelter4.exception.ReportBadParameterException;

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
    @Column(name = "report_date")
    private LocalDate reportDate;
    @Column(name = "diet")
    private String diet;
    @Column(name = "feeling")
    private String feeling;
    @Column(name = "behavior")
    private String behavior;
    @OneToOne
    @JoinColumn(name = "id_photo")
    private Photo photo;
    @Column(name = "is_approve")
    private Boolean isApprove;
    @OneToOne
    @JoinColumn(name = "id_shelter")
    private Shelter shelter;

    public Report() {
    }


    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        if (shelter == null || shelter.getId() == null) {
            throw new IllegalArgumentException();
        }
        if (getAnimalOwnership() != null &&
                getAnimalOwnership().getShelter() != null && getAnimalOwnership().getShelter().getId() != null &&
                !getAnimalOwnership().getShelter().getId().equals(shelter.getId())) {
            throw new ReportBadParameterException();
        }
        this.shelter = shelter;
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
        if (animalOwnership == null || animalOwnership.getId() == null ||
                animalOwnership.getShelter() == null &&
                        animalOwnership.getShelter().getId() == null) {
            throw new IllegalArgumentException();
        }
        if (getShelter() != null && getShelter().getId() != null &&
                !getShelter().getId().equals(animalOwnership.getShelter().getId())) {
            throw new ReportBadParameterException();
        }
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

    public Boolean getApprove() {
        return isApprove;
    }

    @Override
    public String toString() {
        String photoExist;
        if (photo != null) {
            photoExist = "+";
        } else {
            photoExist = "-";
        }
        return "Report\n" +
                "AnimalOwnership: " + animalOwnership +
                "\nDate: " + reportDate.toString() +
                "\ndiet: " + diet + '\'' +
                "\nfeeling: " + feeling + '\'' +
                "\nbehavior: " + behavior + '\'' +
                "\nphoto: " + photoExist;
    }
}
