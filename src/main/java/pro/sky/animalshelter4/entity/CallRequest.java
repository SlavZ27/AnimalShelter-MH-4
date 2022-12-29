package pro.sky.animalshelter4.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "call_request")
public class CallRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_chat_client")
    private Chat chatClient;
    @ManyToOne
    @JoinColumn(name = "id_chat_volunteer")
    private Chat chatVolunteer;
    private boolean isOpen;
    private LocalDateTime localDateTimeOpen;
    private LocalDateTime localDateTimeClose;

    public Chat getChatVolunteer() {
        return chatVolunteer;
    }

    public void setChatVolunteer(Chat chatVolunteer) {
        this.chatVolunteer = chatVolunteer;
    }

    public Chat getChatClient() {
        return chatClient;
    }

    public void setChatClient(Chat chatClient) {
        this.chatClient = chatClient;
    }

    public CallRequest() {
    }



}
