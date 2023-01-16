package pro.sky.animalshelter4.entity;

import pro.sky.animalshelter4.exception.BadPhoneNumber;

import javax.persistence.*;


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

    public User() {
    }

    public User(Long id, String nameUser, Chat chatTelegram, String phone, String address, boolean isOwner, boolean isVolunteer) {
        this.id = id;
        this.nameUser = nameUser;
        this.chatTelegram = chatTelegram;
        this.phone = phone;
        this.address = address;
        this.isVolunteer = isVolunteer;
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
        char[] chars = phone.toCharArray();
        StringBuilder validatePhone = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isDigit(chars[i])) {
                validatePhone.append(chars[i]);
            }
        }
        if (5 > validatePhone.toString().length() || validatePhone.toString().length() > 15) {
            throw new BadPhoneNumber(phone);
        } else {
            this.phone = validatePhone.toString();
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
}
