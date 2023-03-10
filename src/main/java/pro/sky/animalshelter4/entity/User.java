package pro.sky.animalshelter4.entity;

import pro.sky.animalshelter4.exception.BadPhoneNumberException;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nameUser;
    @OneToOne
    @JoinColumn(name = "id_telegram_chat")
    private Chat chatTelegram;
    private String phone;
    private String address;
    boolean isVolunteer;
    @OneToOne
    @JoinColumn(name = "id_shelter")
    private Shelter shelter;
    @Column(name = "date_last_notification")
    LocalDateTime dateLastNotification;

    public User() {
    }

    public User(Long id, String nameUser, Chat chatTelegram, String phone, String address, boolean isVolunteer, LocalDateTime dateLastNotification) {
        this.id = id;
        this.nameUser = nameUser;
        this.chatTelegram = chatTelegram;
        this.phone = phone;
        this.address = address;
        this.isVolunteer = isVolunteer;
        this.dateLastNotification = dateLastNotification;
    }

    public LocalDateTime getDateLastNotification() {
        return dateLastNotification;
    }

    public void setDateLastNotification(LocalDateTime dateLastNotification) {
        this.dateLastNotification = dateLastNotification;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public Chat getChatTelegram() {
        return chatTelegram;
    }

    public void setChatTelegram(Chat chatTelegram) {
        this.chatTelegram = chatTelegram;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null) {
            this.phone = null;
        } else {
            char[] chars = phone.toCharArray();
            StringBuilder validatePhone = new StringBuilder();
            for (int i = 0; i < chars.length; i++) {
                if (Character.isDigit(chars[i])) {
                    validatePhone.append(chars[i]);
                }
            }
            if (5 > validatePhone.toString().length() || validatePhone.toString().length() > 15) {
                throw new BadPhoneNumberException(phone);
            } else {
                this.phone = validatePhone.toString();
            }
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isVolunteer() {
        return isVolunteer;
    }

    public void setVolunteer(boolean volunteer) {
        isVolunteer = volunteer;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.isVolunteer) {
            sb.append("Volunteer of shelter '");
        } else {
            sb.append("Client of shelter '");
        }
        if (shelter != null) {
            sb.append(shelter.getNameShelter());
            sb.append("' ");
        }
        sb.append(nameUser);
        sb.append(" .\nchatTelegram: " + chatTelegram);
        sb.append("\nphone: " + phone);
        sb.append("\naddress: " + address);
        return sb.toString();
    }
}
