package pro.sky.animalshelter4.entity;

import javax.persistence.*;

/**
 * This entity is engaged in creating a data model for the ability to make a Unfinished Request.
 * This entity is used in several classes.
 * The class must have constructor, getters, setters.
 * Since other classes need them for their functioning and for better data protection.
 */
@Entity(name = "unfinished_request_telegram")
public class UnfinishedRequestTelegram {
    @Id
    private Long id;
    @OneToOne
    @JoinColumn(name = "id_chat_telegram")
    private Chat chat;

    private String command;

    public UnfinishedRequestTelegram() {
    }


    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
