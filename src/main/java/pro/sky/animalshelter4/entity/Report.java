package pro.sky.animalshelter4.entity;

import net.bytebuddy.description.modifier.Ownership;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Report {
    @Id
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


}
