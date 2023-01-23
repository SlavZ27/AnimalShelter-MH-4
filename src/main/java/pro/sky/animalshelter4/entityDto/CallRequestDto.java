package pro.sky.animalshelter4.entityDto;

import java.time.LocalDateTime;
import java.util.Objects;

public class CallRequestDto {

    private Long id;
    private Long idClient;

    private Long idVolunteer;
    private boolean isOpen;
    private LocalDateTime localDateTimeOpen;
    private LocalDateTime localDateTimeClose;

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
        if (o == null || getClass() != o.getClass()) return false;
        CallRequestDto that = (CallRequestDto) o;
        return isOpen == that.isOpen && Objects.equals(id, that.id) && Objects.equals(idClient, that.idClient) && Objects.equals(idVolunteer, that.idVolunteer) && Objects.equals(localDateTimeOpen, that.localDateTimeOpen) && Objects.equals(localDateTimeClose, that.localDateTimeClose);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idClient, idVolunteer, isOpen, localDateTimeOpen, localDateTimeClose);
    }
}
