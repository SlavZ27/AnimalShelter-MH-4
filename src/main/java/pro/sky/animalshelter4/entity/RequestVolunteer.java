package pro.sky.animalshelter4.entity;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "request_volunteer")
public class RequestVolunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idClient;
    private Long idVolunteer;
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

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public Long getIdVolunteer() {
        return idVolunteer;
    }

    public void setIdVolunteer(Long idVolunteer) {
        this.idVolunteer = idVolunteer;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestVolunteer)) return false;
        RequestVolunteer that = (RequestVolunteer) o;
        return isOpen() == that.isOpen() && getId().equals(that.getId()) && getIdClient().equals(that.getIdClient()) && getIdVolunteer().equals(that.getIdVolunteer()) && getLocalDateTimeOpen().equals(that.getLocalDateTimeOpen()) && getLocalDateTimeClose().equals(that.getLocalDateTimeClose());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIdClient(), getIdVolunteer(), isOpen(), getLocalDateTimeOpen(), getLocalDateTimeClose());
    }

    @Override
    public String toString() {
        return "RequestVolunteer{" +
                "id=" + id +
                ", idClient=" + idClient +
                ", idVolunteer=" + idVolunteer +
                ", isOpen=" + isOpen +
                ", localDateTimeOpen=" + localDateTimeOpen +
                ", localDateTimeClose=" + localDateTimeClose +
                '}';
    }
}
