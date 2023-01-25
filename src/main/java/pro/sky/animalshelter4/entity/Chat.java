package pro.sky.animalshelter4.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "telegram_chat")
public class Chat {
    @Id
    private Long id;
    private String userNameTelegram;
    private String firstNameUser;
    private String lastNameUser;
    @Column(name= "last_activity")
    private LocalDateTime lastActivity;

    public Chat() {
    }

    public Chat(Long id, String userNameTelegram, String firstNameUser, String lastNameUser, LocalDateTime lastActivity) {
        this.id = id;
        this.userNameTelegram = userNameTelegram;
        this.firstNameUser = firstNameUser;
        this.lastNameUser = lastNameUser;
        this.lastActivity = lastActivity;
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

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
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
        if (lastActivity != null) {
            sb.append("\nlast activity: " + lastActivity);
        }
        return sb.toString();
    }
}
