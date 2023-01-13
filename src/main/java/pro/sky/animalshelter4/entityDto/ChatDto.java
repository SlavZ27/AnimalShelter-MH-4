package pro.sky.animalshelter4.entityDto;

import java.time.LocalDateTime;

public class ChatDto {
    private Long id;
    private String userNameTelegram;
    private String firstNameUser;
    private String lastNameUser;
    private LocalDateTime last_activity;

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
}