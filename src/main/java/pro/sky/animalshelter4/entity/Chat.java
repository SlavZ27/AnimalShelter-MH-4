package pro.sky.animalshelter4.entity;

import javax.persistence.*;

@Entity(name = "chat")
public class Chat {
    @Id
    private Long id;
    private String name;
    private String userName;
    private String phone;
    private String address;
    boolean isVolunteer;

    public Chat() {
    }

    public Chat(Long id, String name, String userName, String phone, String address, boolean isVolunteer) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.phone = phone;
        this.address = address;
        this.isVolunteer = isVolunteer;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isVolunteer() {
        return isVolunteer;
    }

    public void setVolunteer(boolean volunteer) {
        isVolunteer = volunteer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}

