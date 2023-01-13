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
    @JoinColumn(name = "id_client")
    private User client;
    @ManyToOne
    @JoinColumn(name = "id_volunteer")
    private User volunteer;
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

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public User getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(User volunteer) {
        this.volunteer = volunteer;
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
