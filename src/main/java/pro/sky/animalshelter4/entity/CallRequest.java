package pro.sky.animalshelter4.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * This entity is engaged in creating a data model for the ability to make a Call Request.
 * This entity is used in several classes
 * The class must have constructor, getters, setters.
 * Since other classes need them for their functioning and for better data protection.
 */
@Entity(name = "call_request")
public class CallRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_client")
    private User client;
    @ManyToOne
    @JoinColumn(name = "id_volunteer")
    private User volunteer;
    @Column(name = "is_open")
    private boolean isOpen;
    @Column(name = "local_date_time_open")
    private LocalDateTime localDateTimeOpen;
    @Column(name = "local_date_time_close")
    private LocalDateTime localDateTimeClose;
    @OneToOne
    @JoinColumn(name = "id_shelter")
    private Shelter shelter;

    public CallRequest() {
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
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
