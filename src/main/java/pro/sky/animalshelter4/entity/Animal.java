package pro.sky.animalshelter4.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "name_animal")
    private String nameAnimal;
    private LocalDate born;
    @OneToOne
    @JoinColumn(name = "id_animal_type")
    private AnimalType animalType;

    public Animal() {
    }

    public LocalDate getBorn() {
        return born;
    }

    public void setBorn(LocalDate born) {
        this.born = born;
    }

    public AnimalType getAnimalType() {
        return animalType;
    }

    public void setAnimalType(AnimalType animalType) {
        this.animalType = animalType;
    }

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Animal ");
        if (animalType != null) {
            sb.append(animalType.getTypeAnimal());
            sb.append(" ");
        }
        sb.append(nameAnimal);
        sb.append(" ");
        if (born != null) {
            sb.append(born.toString());
        }
        return sb.toString();
    }
}
