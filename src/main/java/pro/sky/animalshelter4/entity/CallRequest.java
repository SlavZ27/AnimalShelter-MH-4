package pro.sky.animalshelter4.entity;

import pro.sky.animalshelter4.exception.AnimalOwnershipBadParameterException;
import pro.sky.animalshelter4.exception.CallRequestBadParameterException;

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
        if (shelter == null || shelter.getId() == null) {
            throw new IllegalArgumentException();
        }
        if (getClient() != null &&
                getClient().getShelter() != null && getClient().getShelter().getId() != null &&
                !getClient().getShelter().getId().equals(shelter.getId())) {
            throw new CallRequestBadParameterException();
        }
        if (getVolunteer() != null &&
                getVolunteer().getShelter() != null && getVolunteer().getShelter().getId() != null &&
                !getVolunteer().getShelter().getId().equals(shelter.getId())) {
            throw new CallRequestBadParameterException();
        }
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
        if (client == null || client.getId() == null ||
                client.getShelter() == null || client.getShelter().getId() == null) {
            throw new IllegalArgumentException();
        }
        if (getShelter() != null && getShelter().getId() != null &&
                !getShelter().getId().equals(client.getShelter().getId())) {
            throw new CallRequestBadParameterException();
        }
        if (getVolunteer() != null &&
                getVolunteer().getShelter() != null && getVolunteer().getShelter().getId() != null &&
                !getVolunteer().getShelter().getId().equals(client.getShelter().getId())) {
            throw new CallRequestBadParameterException();
        }
        this.client = client;
    }

    public User getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(User volunteer) {
        if (volunteer == null || volunteer.getId() == null ||
                volunteer.getShelter() == null || volunteer.getShelter().getId() == null) {
            throw new IllegalArgumentException();
        }
        if (getShelter() != null && getShelter().getId() != null &&
                !getShelter().getId().equals(volunteer.getShelter().getId())) {
            throw new CallRequestBadParameterException();
        }
        if (getClient() != null &&
                getClient().getShelter() != null && getClient().getShelter().getId() != null &&
                !getClient().getShelter().getId().equals(volunteer.getShelter().getId())) {
            throw new CallRequestBadParameterException();
        }
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
