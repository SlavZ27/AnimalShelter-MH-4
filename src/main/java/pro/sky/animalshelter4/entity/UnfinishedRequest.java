package pro.sky.animalshelter4.entity;

import javax.persistence.*;

/**
 * This entity is engaged in creating a data model for the ability to make a Unfinished Request.
 * This entity is used in several classes.
 * The class must have constructor, getters, setters.
 * Since other classes need them for their functioning and for better data protection.
 */
@Entity(name = "unfinished_request")
public class UnfinishedRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_chat")
    private Chat chat;
    private String command;

    public UnfinishedRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
