package pro.sky.animalshelter4.entityDto;

import javax.persistence.JoinColumn;

public class AnimalTypeDto {
    private Long id;
    private String typeAnimal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeAnimal() {
        return typeAnimal;
    }

    public void setTypeAnimal(String typeAnimal) {
        this.typeAnimal = typeAnimal;
    }
}
