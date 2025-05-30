package com.gym.model;

import lombok.Data;

@Data
public class TrainingStats {
    private Long trainingId;
    private String trainingName;
    private String trainerName;
    private int totalBookings;
    private int attendedCount;
    
    public double getAttendanceRate() {
        return totalBookings > 0 ? (double) attendedCount / totalBookings * 100 : 0;
    }
} 