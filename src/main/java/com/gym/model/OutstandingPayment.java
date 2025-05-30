package com.gym.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OutstandingPayment {
    private Long clientId;
    private String clientName;
    private String membershipType;
    private LocalDate startDate;
    private LocalDate endDate;
} 