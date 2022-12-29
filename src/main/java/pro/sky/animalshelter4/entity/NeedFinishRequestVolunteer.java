package pro.sky.animalshelter4.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "need_finish_request_volunteer")
public class NeedFinishRequestVolunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_volunteer")
    private Volunteer volunteer;
    private String command;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NeedFinishRequestVolunteer)) return false;
        NeedFinishRequestVolunteer that = (NeedFinishRequestVolunteer) o;
        return getId().equals(that.getId()) && getVolunteer().equals(that.getVolunteer()) && getCommand().equals(that.getCommand());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVolunteer(), getCommand());
    }

    @Override
    public String toString() {
        return "NeedFinishRequestVolunteer{" +
                "id=" + id +
                ", volunteer=" + volunteer +
                ", command='" + command + '\'' +
                '}';
    }
}
