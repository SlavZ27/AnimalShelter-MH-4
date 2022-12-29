package pro.sky.animalshelter4.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phone;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    private String address;
    private Long chatTelegramId;

    public Client() {
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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getChatTelegramId() {
        return chatTelegramId;
    }

    public void setChatTelegramId(Long chatTelegramId) {
        this.chatTelegramId = chatTelegramId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return getId().equals(client.getId()) && getName().equals(client.getName()) && getPhone().equals(client.getPhone()) && getCity().equals(client.getCity()) && getAddress().equals(client.getAddress()) && getChatTelegramId().equals(client.getChatTelegramId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getPhone(), getCity(), getAddress(), getChatTelegramId());
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", city=" + city +
                ", address='" + address + '\'' +
                ", chatTelegramId=" + chatTelegramId +
                '}';
    }
}
