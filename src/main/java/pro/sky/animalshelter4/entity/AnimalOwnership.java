package pro.sky.animalshelter4.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class AnimalOwnership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "id_user")
    @OneToOne
    private User owner;
    @OneToOne
    @JoinColumn(name = "id_animal")
    private Animal animal;
    @JoinColumn(name = "date_start_own")
    private LocalDate dateStartOwn;
    @JoinColumn(name = "date_end_trial")
    private LocalDate dateEndTrial;
    @JoinColumn(name = "is_approve")
    private Boolean isApprove;
    @JoinColumn(name = "is_open")
    private boolean isOpen;

    public AnimalOwnership() {
    }

    public boolean isApprove() {
        return isApprove;
    }

    public void setApprove(boolean approve) {
        isApprove = approve;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public LocalDate getDateStartOwn() {
        return dateStartOwn;
    }

    public void setDateStartOwn(LocalDate dateStartOwn) {
        this.dateStartOwn = dateStartOwn;
    }

    public LocalDate getDateEndTrial() {
        return dateEndTrial;
    }

    public void setDateEndTrial(LocalDate dateEndTrial) {
        this.dateEndTrial = dateEndTrial;
    }

    @Override
    public String toString() {
        return "AnimalOwnership\n" +
                "Owner: " + owner +
                "\nanimal: " + animal +
                "\n dateStartOwn: " + dateStartOwn.toString() +
                "\ndateEndTrial: " + dateEndTrial.toString();
    }
}
