package pro.sky.animalshelter4.entityDto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class ShelterDto {

    private Long id;
    private String shelterDesignation;
    private String nameShelter;
    private String address;
    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getshelterDesignation() {
        return shelterDesignation;
    }

    public void setshelterDesignation(String shelterDesignation) {
        this.shelterDesignation = shelterDesignation;
    }

    public String getNameShelter() {
        return nameShelter;
    }

    public void setNameShelter(String nameShelter) {
        this.nameShelter = nameShelter;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
