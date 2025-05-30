package com.gym.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "client_trainers")
public class ClientTrainer {
    @EmbeddedId
    private ClientTrainerId id;

    @ManyToOne
    @MapsId("clientId")
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @MapsId("trainerId")
    @JoinColumn(name = "trainer_id")
    private Staff trainer;

    @Column(name = "assigned_date")
    private LocalDate assignedDate;
}

@Embeddable
class ClientTrainerId implements java.io.Serializable {
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "trainer_id")
    private Long trainerId;

    
    protected ClientTrainerId() {}

    public ClientTrainerId(Long clientId, Long trainerId) {
        this.clientId = clientId;
        this.trainerId = trainerId;
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientTrainerId that = (ClientTrainerId) o;
        return clientId.equals(that.clientId) && trainerId.equals(that.trainerId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(clientId, trainerId);
    }
} 