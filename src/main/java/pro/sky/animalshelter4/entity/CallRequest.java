package pro.sky.animalshelter4.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "call_request")
public class CallRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idClient;
    private boolean isOpen;
    private LocalDateTime localDateTimeOpen;
    private LocalDateTime localDateTimeClose;

    public CallRequest() {
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
        if (!(o instanceof CallRequest)) return false;
        CallRequest that = (CallRequest) o;
        return isOpen() == that.isOpen() && getId().equals(that.getId()) && getIdClient().equals(that.getIdClient()) && getLocalDateTimeOpen().equals(that.getLocalDateTimeOpen()) && getLocalDateTimeClose().equals(that.getLocalDateTimeClose());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIdClient(), isOpen(), getLocalDateTimeOpen(), getLocalDateTimeClose());
    }

    @Override
    public String toString() {
        return "CallRequest{" +
                "id=" + id +
                ", idClient=" + idClient +
                ", isOpen=" + isOpen +
                ", localDateTimeOpen=" + localDateTimeOpen +
                ", localDateTimeClose=" + localDateTimeClose +
                '}';
    }
}
