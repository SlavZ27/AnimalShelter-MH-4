package pro.sky.animalshelter4.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity(name = "need_finish_request_client")
public class NeedFinishRequestClient {
    @Id
    private Long idClient;
    private String command;

    public NeedFinishRequestClient() {
    }

    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long id) {
        this.idClient = id;
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
        if (!(o instanceof NeedFinishRequestClient)) return false;
        NeedFinishRequestClient that = (NeedFinishRequestClient) o;
        return getIdClient().equals(that.getIdClient()) && getCommand().equals(that.getCommand());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdClient(), getCommand());
    }

    @Override
    public String toString() {
        return "NeedFinishRequestClient{" +
                "id=" + idClient +
                ", name='" + command + '\'' +
                '}';
    }
}
