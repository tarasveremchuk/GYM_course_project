package com.gym.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "visits")
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "visit_date")
    private LocalDateTime visitDate;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Staff trainer;

    private String notes;
} 