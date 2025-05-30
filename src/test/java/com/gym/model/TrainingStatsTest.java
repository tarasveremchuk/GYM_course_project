package com.gym.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrainingStatsTest {

    @Test
    void testDefaultConstructor() {
        
        TrainingStats stats = new TrainingStats();
        
        
        assertNull(stats.getTrainingId());
        assertNull(stats.getTrainingName());
        assertNull(stats.getTrainerName());
        assertEquals(0, stats.getTotalBookings());
        assertEquals(0, stats.getAttendedCount());
        assertEquals(0, stats.getAttendanceRate());
    }

    @Test
    void testSettersAndGetters() {
        
        TrainingStats stats = new TrainingStats();
        
        
        stats.setTrainingId(1L);
        stats.setTrainingName("Yoga Class");
        stats.setTrainerName("John Smith");
        stats.setTotalBookings(30);
        stats.setAttendedCount(25);
        
        
        assertEquals(1L, stats.getTrainingId());
        assertEquals("Yoga Class", stats.getTrainingName());
        assertEquals("John Smith", stats.getTrainerName());
        assertEquals(30, stats.getTotalBookings());
        assertEquals(25, stats.getAttendedCount());
    }

    @Test
    void testAttendanceRate() {
        
        TrainingStats stats = new TrainingStats();
        
        
        assertEquals(0, stats.getAttendanceRate());
        
        
        stats.setTotalBookings(20);
        stats.setAttendedCount(15);
        assertEquals(75.0, stats.getAttendanceRate());
        
        
        stats.setTotalBookings(10);
        stats.setAttendedCount(10);
        assertEquals(100.0, stats.getAttendanceRate());
        
        
        stats.setTotalBookings(5);
        stats.setAttendedCount(0);
        assertEquals(0.0, stats.getAttendanceRate());
    }

    @Test
    void testEqualsAndHashCode() {
        
        TrainingStats stats1 = new TrainingStats();
        stats1.setTrainingId(1L);
        stats1.setTrainingName("Pilates");
        stats1.setTrainerName("Jane Doe");
        
        TrainingStats stats2 = new TrainingStats();
        stats2.setTrainingId(1L);
        stats2.setTrainingName("Pilates");
        stats2.setTrainerName("Jane Doe");
        
        TrainingStats stats3 = new TrainingStats();
        stats3.setTrainingId(2L);
        stats3.setTrainingName("CrossFit");
        stats3.setTrainerName("John Smith");
        
        
        assertEquals(stats1, stats2);
        assertNotEquals(stats1, stats3);
        
        
        assertEquals(stats1.hashCode(), stats2.hashCode());
        assertNotEquals(stats1.hashCode(), stats3.hashCode());
    }

    @Test
    void testToString() {
        
        TrainingStats stats = new TrainingStats();
        stats.setTrainingId(1L);
        stats.setTrainingName("HIIT Training");
        stats.setTrainerName("Alex Johnson");
        stats.setTotalBookings(40);
        stats.setAttendedCount(35);
        
        String toString = stats.toString();
        
        
        assertTrue(toString.contains("trainingId=1"));
        assertTrue(toString.contains("trainingName=HIIT Training"));
        assertTrue(toString.contains("trainerName=Alex Johnson"));
        assertTrue(toString.contains("totalBookings=40"));
        assertTrue(toString.contains("attendedCount=35"));
    }
}
