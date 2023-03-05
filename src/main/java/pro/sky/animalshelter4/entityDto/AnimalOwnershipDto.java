package pro.sky.animalshelter4.entityDto;

import java.time.LocalDate;

public class AnimalOwnershipDto {

    private Long id;
    private Long idOwner;
    private Long idAnimal;
    private LocalDate dateStartOwn;
    private LocalDate dateEndTrial;
    private Boolean isApprove;
    private boolean isOpen;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(Long idOwner) {
        this.idOwner = idOwner;
    }

    public Long getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(Long idAnimal) {
        this.idAnimal = idAnimal;
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

    public Boolean getApprove() {
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
}
