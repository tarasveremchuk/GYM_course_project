package com.gym.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VisitTest {

    @Test
    void testDefaultConstructor() {
        
        Visit visit = new Visit();
        
        
        assertNull(visit.getId());
        assertNull(visit.getClient());
        assertNull(visit.getVisitDate());
        assertNull(visit.getTrainer());
        assertNull(visit.getNotes());
    }

    @Test
    void testSettersAndGetters() {
        
        Visit visit = new Visit();
        
        
        Client client = new Client();
        client.setId(1L);
        client.setFullName("John Doe");
        
        
        Staff trainer = new Staff();
        trainer.setId(2L);
        trainer.setFullName("Jane Smith");
        trainer.setRole(StaffRole.TRAINER);
        
        
        visit.setId(1L);
        visit.setClient(client);
        
        LocalDateTime visitDate = LocalDateTime.of(2025, 5, 30, 15, 0);
        visit.setVisitDate(visitDate);
        visit.setTrainer(trainer);
        visit.setNotes("Regular workout session");
        
        
        assertEquals(1L, visit.getId());
        assertEquals(client, visit.getClient());
        assertEquals(visitDate, visit.getVisitDate());
        assertEquals(trainer, visit.getTrainer());
        assertEquals("Regular workout session", visit.getNotes());
    }

    @Test
    void testEqualsAndHashCode() {
        
        Visit visit1 = new Visit();
        visit1.setId(1L);
        
        Visit visit2 = new Visit();
        visit2.setId(1L);
        
        Visit visit3 = new Visit();
        visit3.setId(2L);
        
        
        assertEquals(visit1, visit2);
        assertNotEquals(visit1, visit3);
        
        
        assertEquals(visit1.hashCode(), visit2.hashCode());
        assertNotEquals(visit1.hashCode(), visit3.hashCode());
    }

    @Test
    void testToString() {
        
        Visit visit = new Visit();
        visit.setId(1L);
        
        LocalDateTime visitDate = LocalDateTime.of(2025, 5, 30, 16, 30);
        visit.setVisitDate(visitDate);
        
        String toString = visit.toString();
        
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("visitDate=" + visitDate.toString()));
    }
    
    @Test
    void testVisitWithoutTrainer() {
        
        Visit visit = new Visit();
        
        
        Client client = new Client();
        client.setId(1L);
        
        
        visit.setId(1L);
        visit.setClient(client);
        visit.setVisitDate(LocalDateTime.now());
        
        
        
        assertNotNull(visit.getClient());
        assertNull(visit.getTrainer());
    }
    
    @Test
    void testVisitWithoutNotes() {
        
        Visit visit = new Visit();
        
        
        visit.setId(1L);
        visit.setVisitDate(LocalDateTime.now());
        
        
        assertNull(visit.getNotes());
        
        
        visit.setNotes("");
        assertEquals("", visit.getNotes());
    }
}
