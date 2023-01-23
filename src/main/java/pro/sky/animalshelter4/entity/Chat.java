package pro.sky.animalshelter4.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "telegram_chat")
public class Chat {
    @Id
    private Long id;
    private String userNameTelegram;
    private String firstNameUser;
    private String lastNameUser;
    private LocalDateTime last_activity;

    public Chat() {
    }

    public Chat(Long id, String userNameTelegram, String firstNameUser, String lastNameUser, LocalDateTime last_activity) {
        this.id = id;
        this.userNameTelegram = userNameTelegram;
        this.firstNameUser = firstNameUser;
        this.lastNameUser = lastNameUser;
        this.last_activity = last_activity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserNameTelegram() {
        return userNameTelegram;
    }

    public void setUserNameTelegram(String userNameTelegram) {
        this.userNameTelegram = userNameTelegram;
    }

    public String getFirstNameUser() {
        return firstNameUser;
    }

    public void setFirstNameUser(String firstNameUser) {
        this.firstNameUser = firstNameUser;
    }

    public String getLastNameUser() {
        return lastNameUser;
    }

    public void setLastNameUser(String lastNameUser) {
        this.lastNameUser = lastNameUser;
    }

    public LocalDateTime getLast_activity() {
        return last_activity;
    }

    public void setLast_activity(LocalDateTime last_activity) {
        this.last_activity = last_activity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Telegram chat");
        sb.append("\nid: " + id);
        if (userNameTelegram != null) {
            sb.append("\nuser name: " + userNameTelegram);
        }
        if (firstNameUser != null) {
            sb.append("\nfirst name: " + firstNameUser);
        }
        if (lastNameUser != null) {
            sb.append("\nlast name: " + lastNameUser);
        }
        if (last_activity != null) {
            sb.append("\nlast activity: " + last_activity);
        }
        return sb.toString();
    }
}
