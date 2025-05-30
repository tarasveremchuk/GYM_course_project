package com.gym.model;

import lombok.Data;

@Data
public class TrainerStats {
    private Long trainerId;
    private String trainerName;
    private int totalClients;
    private int totalBookings;
    private int attendedCount;
    private int cancelledCount;
    
    public double getAttendanceRate() {
        return totalBookings > 0 ? (double) attendedCount / totalBookings * 100 : 0;
    }
    
    public double getCancellationRate() {
        return totalBookings > 0 ? (double) cancelledCount / totalBookings * 100 : 0;
    }
} 