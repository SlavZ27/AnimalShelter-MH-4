package pro.sky.animalshelter4.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity(name = "need_finish_request_volunteer")
public class NeedFinishRequestVolunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVolunteer;
    private String command;


    public Long getIdVolunteer() {
        return idVolunteer;
    }

    public void setIdVolunteer(Long id) {
        this.idVolunteer = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String name) {
        this.command = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NeedFinishRequestVolunteer)) return false;
        NeedFinishRequestVolunteer that = (NeedFinishRequestVolunteer) o;
        return getIdVolunteer().equals(that.getIdVolunteer()) && getCommand().equals(that.getCommand());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdVolunteer(), getCommand());
    }

    @Override
    public String toString() {
        return "NeedFinishRequestVolunteer{" +
                "id=" + idVolunteer +
                ", name='" + command + '\'' +
                '}';
    }
}
