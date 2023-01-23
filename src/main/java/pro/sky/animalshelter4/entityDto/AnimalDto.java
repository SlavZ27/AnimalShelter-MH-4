package pro.sky.animalshelter4.entityDto;


import pro.sky.animalshelter4.entity.AnimalType;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDate;

public class AnimalDto {

    private Long id;
    private String nameAnimal;
    private LocalDate born;
    private Long idAnimalType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameAnimal() {
        return nameAnimal;
    }

    public void setNameAnimal(String nameAnimal) {
        this.nameAnimal = nameAnimal;
    }

    public LocalDate getBorn() {
        return born;
    }

    public void setBorn(LocalDate born) {
        this.born = born;
    }

    public Long getIdAnimalType() {
        return idAnimalType;
    }

    public void setIdAnimalType(Long idAnimalType) {
        this.idAnimalType = idAnimalType;
    }
}
