package pro.sky.animalshelter4.entityDto;



import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDate;

public class AnimalDto {

    private Long id;
    private String nameAnimal;
    private LocalDate born;

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
    
}
