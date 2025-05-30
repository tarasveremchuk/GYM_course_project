package com.gym.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrainerStatsTest {

    @Test
    void testDefaultConstructor() {
        
        TrainerStats stats = new TrainerStats();
        
        
        assertNull(stats.getTrainerId());
        assertNull(stats.getTrainerName());
        assertEquals(0, stats.getTotalClients());
        assertEquals(0, stats.getTotalBookings());
        assertEquals(0, stats.getAttendedCount());
        assertEquals(0, stats.getCancelledCount());
        assertEquals(0, stats.getAttendanceRate());
        assertEquals(0, stats.getCancellationRate());
    }

    @Test
    void testSettersAndGetters() {
        
        TrainerStats stats = new TrainerStats();
        
        
        stats.setTrainerId(1L);
        stats.setTrainerName("John Smith");
        stats.setTotalClients(10);
        stats.setTotalBookings(50);
        stats.setAttendedCount(40);
        stats.setCancelledCount(5);
        
        
        assertEquals(1L, stats.getTrainerId());
        assertEquals("John Smith", stats.getTrainerName());
        assertEquals(10, stats.getTotalClients());
        assertEquals(50, stats.getTotalBookings());
        assertEquals(40, stats.getAttendedCount());
        assertEquals(5, stats.getCancelledCount());
    }

    @Test
    void testAttendanceRate() {
        
        TrainerStats stats = new TrainerStats();
        
        
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
    void testCancellationRate() {
        
        TrainerStats stats = new TrainerStats();
        
        
        assertEquals(0, stats.getCancellationRate());
        
        
        stats.setTotalBookings(20);
        stats.setCancelledCount(4);
        assertEquals(20.0, stats.getCancellationRate());
        
        
        stats.setTotalBookings(10);
        stats.setCancelledCount(10);
        assertEquals(100.0, stats.getCancellationRate());
        
        
        stats.setTotalBookings(5);
        stats.setCancelledCount(0);
        assertEquals(0.0, stats.getCancellationRate());
    }

    @Test
    void testEqualsAndHashCode() {
        
        TrainerStats stats1 = new TrainerStats();
        stats1.setTrainerId(1L);
        stats1.setTrainerName("John Smith");
        stats1.setTotalBookings(50);
        
        TrainerStats stats2 = new TrainerStats();
        stats2.setTrainerId(1L);
        stats2.setTrainerName("John Smith");
        stats2.setTotalBookings(50);
        
        TrainerStats stats3 = new TrainerStats();
        stats3.setTrainerId(2L);
        stats3.setTrainerName("Jane Doe");
        stats3.setTotalBookings(30);
        
        
        assertEquals(stats1, stats2);
        assertNotEquals(stats1, stats3);
        
        
        assertEquals(stats1.hashCode(), stats2.hashCode());
        assertNotEquals(stats1.hashCode(), stats3.hashCode());
    }

    @Test
    void testToString() {
        
        TrainerStats stats = new TrainerStats();
        stats.setTrainerId(1L);
        stats.setTrainerName("John Smith");
        stats.setTotalClients(10);
        stats.setTotalBookings(50);
        stats.setAttendedCount(40);
        stats.setCancelledCount(5);
        
        String toString = stats.toString();
        
        
        assertTrue(toString.contains("trainerId=1"));
        assertTrue(toString.contains("trainerName=John Smith"));
        assertTrue(toString.contains("totalClients=10"));
        assertTrue(toString.contains("totalBookings=50"));
        assertTrue(toString.contains("attendedCount=40"));
        assertTrue(toString.contains("cancelledCount=5"));
    }
}
