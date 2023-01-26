package pro.sky.animalshelter4.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name_animal")
    private String nameAnimal;
    @Column(name = "born")
    private LocalDate born;
    @OneToOne
    @JoinColumn(name = "id_shelter")
    private Shelter shelter;

    public Animal() {
    }

    public LocalDate getBorn() {
        return born;
    }

    public void setBorn(LocalDate born) {
        this.born = born;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
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
        StringBuilder sb = new StringBuilder("Animal of shelter '");
        if (shelter != null) {
            sb.append(shelter.getNameShelter());
            sb.append("' ");
        }
        sb.append(nameAnimal);
        sb.append(" ");
        if (born != null) {
            sb.append(born.toString());
        }
        return sb.toString();
    }
}
