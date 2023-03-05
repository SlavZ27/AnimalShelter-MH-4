package pro.sky.animalshelter4.entity;

import pro.sky.animalshelter4.exception.AnimalOwnershipBadParameterException;

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
    @Column(name = "date_start_own")
    private LocalDate dateStartOwn;
    @Column(name = "date_end_trial")
    private LocalDate dateEndTrial;
    @Column(name = "is_approve")
    private Boolean isApprove;
    @Column(name = "is_open")
    private boolean isOpen;
    @OneToOne
    @JoinColumn(name = "id_shelter")
    private Shelter shelter;

    public AnimalOwnership() {
    }

    public Boolean getApprove() {
        return isApprove;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        if (shelter == null || shelter.getId() == null) {
            throw new IllegalArgumentException();
        }
        if (getOwner() != null &&
                getOwner().getShelter() != null && getOwner().getShelter().getId() != null &&
                !getOwner().getShelter().getId().equals(shelter.getId())) {
            throw new AnimalOwnershipBadParameterException();
        }
        if (getAnimal() != null &&
                getAnimal().getShelter() != null && getAnimal().getShelter().getId() != null &&
                !getAnimal().getShelter().getId().equals(shelter.getId())) {
            throw new AnimalOwnershipBadParameterException();
        }
        this.shelter = shelter;
    }

    public Boolean isApprove() {
        return isApprove;
    }

    public void setApprove(Boolean approve) {
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
        if (owner == null || owner.getId() == null ||
                owner.getShelter() == null || owner.getShelter().getId() == null) {
            throw new IllegalArgumentException();
        }
        if (getShelter() != null && getShelter().getId() != null &&
                !getShelter().getId().equals(owner.getShelter().getId())) {
            throw new AnimalOwnershipBadParameterException();
        }
        if (getAnimal() != null &&
                getAnimal().getShelter() != null && getAnimal().getShelter().getId() != null &&
                !getAnimal().getShelter().getId().equals(owner.getShelter().getId())) {
            throw new AnimalOwnershipBadParameterException();
        }
        this.owner = owner;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        if (animal == null || animal.getId() == null ||
                animal.getShelter() == null || animal.getShelter().getId() == null) {
            throw new IllegalArgumentException();
        }
        if (getShelter() != null && getShelter().getId() != null &&
                !getShelter().getId().equals(animal.getShelter().getId())) {
            throw new AnimalOwnershipBadParameterException();
        }
        if (getOwner() != null &&
                getOwner().getShelter() != null && getOwner().getShelter().getId() != null &&
                !getOwner().getShelter().getId().equals(animal.getShelter().getId())) {
            throw new AnimalOwnershipBadParameterException();
        }
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
                "\ndateStartOwn: " + dateStartOwn.toString() +
                "\ndateEndTrial: " + dateEndTrial.toString();
    }
}
