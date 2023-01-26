package pro.sky.animalshelter4.entity;

import javax.persistence.*;

@Entity
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text_designation")
    private String shelterDesignation;
    @Column(name = "name_shelter")
    private String nameShelter;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;

    public Shelter() {
    }

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
