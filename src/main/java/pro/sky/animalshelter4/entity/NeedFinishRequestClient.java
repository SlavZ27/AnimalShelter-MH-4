package pro.sky.animalshelter4.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "need_finish_request_client")
public class NeedFinishRequestClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;
    private String command;

    public NeedFinishRequestClient() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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
        if (!(o instanceof NeedFinishRequestClient)) return false;
        NeedFinishRequestClient that = (NeedFinishRequestClient) o;
        return getId().equals(that.getId()) && getClient().equals(that.getClient()) && getCommand().equals(that.getCommand());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getClient(), getCommand());
    }

    @Override
    public String toString() {
        return "NeedFinishRequestClient{" +
                "id=" + id +
                ", client=" + client +
                ", command='" + command + '\'' +
                '}';
    }
}
