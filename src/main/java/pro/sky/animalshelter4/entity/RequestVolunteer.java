package pro.sky.animalshelter4.entity;



import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * This entity is engaged in creating a data model for the ability to make a Request Volunteer.
 * This entity is used in several classes.
 * The class must have constructor, getters, setters.
 * Since other classes need them for their functioning and for better data protection.
 */
@Entity(name = "request_volunteer")
public class RequestVolunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_chat_client")
    private Chat chatClient;
    @ManyToOne
    @JoinColumn(name = "id_chat_volunteer")
    private Chat chatVolunteer;
    private boolean isOpen;
    private LocalDateTime localDateTimeOpen;
    private LocalDateTime localDateTimeClose;

    public RequestVolunteer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Chat getChatClient() {
        return chatClient;
    }

    public void setChatClient(Chat chatClient) {
        this.chatClient = chatClient;
    }

    public Chat getChatVolunteer() {
        return chatVolunteer;
    }

    public void setChatVolunteer(Chat chatVolunteer) {
        this.chatVolunteer = chatVolunteer;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public LocalDateTime getLocalDateTimeOpen() {
        return localDateTimeOpen;
    }

    public void setLocalDateTimeOpen(LocalDateTime localDateTimeOpen) {
        this.localDateTimeOpen = localDateTimeOpen;
    }

    public LocalDateTime getLocalDateTimeClose() {
        return localDateTimeClose;
    }

    public void setLocalDateTimeClose(LocalDateTime localDateTimeClose) {
        this.localDateTimeClose = localDateTimeClose;
    }
}
