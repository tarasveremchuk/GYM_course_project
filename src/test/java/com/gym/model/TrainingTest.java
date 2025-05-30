package com.gym.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTest {

    @Test
    void testDefaultConstructor() {
        
        Training training = new Training();
        
        
        assertNull(training.getId());
        assertNull(training.getName());
        assertNull(training.getDescription());
        assertNull(training.getCapacity());
        assertNull(training.getTrainer());
        assertNull(training.getScheduledAt());
        assertNull(training.getDurationMinutes());
    }

    @Test
    void testSettersAndGetters() {
        
        Training training = new Training();
        
        
        Staff trainer = new Staff();
        trainer.setId(1L);
        trainer.setFullName("John Smith");
        trainer.setRole(StaffRole.TRAINER);
        
        
        training.setId(1L);
        training.setName("Yoga Class");
        training.setDescription("Beginner-friendly yoga session");
        training.setCapacity(15);
        training.setTrainer(trainer);
        
        LocalDateTime scheduledAt = LocalDateTime.of(2025, 6, 1, 10, 0);
        training.setScheduledAt(scheduledAt);
        training.setDurationMinutes(60);
        
        
        assertEquals(1L, training.getId());
        assertEquals("Yoga Class", training.getName());
        assertEquals("Beginner-friendly yoga session", training.getDescription());
        assertEquals(15, training.getCapacity());
        assertEquals(trainer, training.getTrainer());
        assertEquals(scheduledAt, training.getScheduledAt());
        assertEquals(60, training.getDurationMinutes());
    }

    @Test
    void testEqualsAndHashCode() {
        
        Training training1 = new Training();
        training1.setId(1L);
        training1.setName("Pilates");
        
        Training training2 = new Training();
        training2.setId(1L);
        training2.setName("Pilates");
        
        Training training3 = new Training();
        training3.setId(2L);
        training3.setName("CrossFit");
        
        
        assertEquals(training1, training2);
        assertNotEquals(training1, training3);
        
        
        assertEquals(training1.hashCode(), training2.hashCode());
        assertNotEquals(training1.hashCode(), training3.hashCode());
    }

    @Test
    void testToString() {
        
        Training training = new Training();
        training.setId(1L);
        training.setName("HIIT Training");
        training.setCapacity(10);
        training.setDurationMinutes(45);
        
        String toString = training.toString();
        
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=HIIT Training"));
        assertTrue(toString.contains("capacity=10"));
        assertTrue(toString.contains("durationMinutes=45"));
    }
    
    @Test
    void testTrainingScheduling() {
        
        Training training = new Training();
        
        
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        training.setScheduledAt(tomorrow);
        
        
        training.setDurationMinutes(90);
        
        
        assertEquals(tomorrow, training.getScheduledAt());
        assertEquals(90, training.getDurationMinutes());
    }
    
    @Test
    void testCapacityLimits() {
        
        Training training = new Training();
        
        
        training.setCapacity(5);
        assertEquals(5, training.getCapacity());
        
        
        training.setCapacity(15);
        assertEquals(15, training.getCapacity());
        
        
        training.setCapacity(30);
        assertEquals(30, training.getCapacity());
    }
}
