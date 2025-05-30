package com.gym.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "memberships")
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MembershipType type;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "visits_left")
    private Integer visitsLeft;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;

    @Column(name = "price")
    private Double price;

    public enum MembershipType {
        fixed,
        limited,
        unlimited
    }
} 