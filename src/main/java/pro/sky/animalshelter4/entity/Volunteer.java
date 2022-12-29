package pro.sky.animalshelter4.entity;
import javax.persistence.*;
import java.util.Objects;

@Entity(name = "volunteer")
public class Volunteer {
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
    private boolean isWork;

    public Volunteer() {
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

    public boolean isWork() {
        return isWork;
    }

    public void setWork(boolean work) {
        isWork = work;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Volunteer)) return false;
        Volunteer volunteer = (Volunteer) o;
        return isWork() == volunteer.isWork() && getId().equals(volunteer.getId()) && getName().equals(volunteer.getName()) && getPhone().equals(volunteer.getPhone()) && city.equals(volunteer.city) && getAddress().equals(volunteer.getAddress()) && getChatTelegramId().equals(volunteer.getChatTelegramId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getPhone(), city, getAddress(), getChatTelegramId(), isWork());
    }

    @Override
    public String toString() {
        return "Volunteer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", city=" + city +
                ", address='" + address + '\'' +
                ", chatTelegramId=" + chatTelegramId +
                ", isWork=" + isWork +
                '}';
    }
}
