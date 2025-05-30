package com.gym.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ClientTrainerTest {

    @Test
    void testDefaultConstructor() {
        
        ClientTrainer clientTrainer = new ClientTrainer();
        
        
        assertNull(clientTrainer.getId());
        assertNull(clientTrainer.getClient());
        assertNull(clientTrainer.getTrainer());
        assertNull(clientTrainer.getAssignedDate());
    }

    @Test
    void testSettersAndGetters() {
        
        ClientTrainer clientTrainer = new ClientTrainer();
        
        
        Client client = new Client();
        client.setId(1L);
        client.setFullName("John Doe");
        
        
        Staff trainer = new Staff();
        trainer.setId(2L);
        trainer.setFullName("Jane Smith");
        trainer.setRole(StaffRole.TRAINER);
        
        
        ClientTrainerId id = new ClientTrainerId(1L, 2L);
        
        
        clientTrainer.setId(id);
        clientTrainer.setClient(client);
        clientTrainer.setTrainer(trainer);
        
        LocalDate assignedDate = LocalDate.of(2025, 5, 1);
        clientTrainer.setAssignedDate(assignedDate);
        
        
        assertEquals(id, clientTrainer.getId());
        assertEquals(client, clientTrainer.getClient());
        assertEquals(trainer, clientTrainer.getTrainer());
        assertEquals(assignedDate, clientTrainer.getAssignedDate());
    }

    @Test
    void testEqualsAndHashCode() {
        
        ClientTrainerId id1 = new ClientTrainerId(1L, 2L);
        ClientTrainer clientTrainer1 = new ClientTrainer();
        clientTrainer1.setId(id1);
        
        ClientTrainerId id2 = new ClientTrainerId(1L, 2L);
        ClientTrainer clientTrainer2 = new ClientTrainer();
        clientTrainer2.setId(id2);
        
        ClientTrainerId id3 = new ClientTrainerId(1L, 3L);
        ClientTrainer clientTrainer3 = new ClientTrainer();
        clientTrainer3.setId(id3);
        
        
        assertEquals(clientTrainer1, clientTrainer2);
        assertNotEquals(clientTrainer1, clientTrainer3);
        
        
        assertEquals(clientTrainer1.hashCode(), clientTrainer2.hashCode());
        assertNotEquals(clientTrainer1.hashCode(), clientTrainer3.hashCode());
    }

    @Test
    void testToString() {
        
        ClientTrainerId id = new ClientTrainerId(1L, 2L);
        ClientTrainer clientTrainer = new ClientTrainer();
        clientTrainer.setId(id);
        
        LocalDate assignedDate = LocalDate.of(2025, 5, 1);
        clientTrainer.setAssignedDate(assignedDate);
        
        String toString = clientTrainer.toString();
        
        
        assertTrue(toString.contains("id="));
        assertTrue(toString.contains("assignedDate=" + assignedDate.toString()));
    }
    
    @Test
    void testClientTrainerIdConstructor() {
        
        ClientTrainerId id = new ClientTrainerId(1L, 2L);
        
        
        try {
            java.lang.reflect.Field clientIdField = ClientTrainerId.class.getDeclaredField("clientId");
            clientIdField.setAccessible(true);
            java.lang.reflect.Field trainerIdField = ClientTrainerId.class.getDeclaredField("trainerId");
            trainerIdField.setAccessible(true);
            
            assertEquals(1L, clientIdField.get(id));
            assertEquals(2L, trainerIdField.get(id));
        } catch (Exception e) {
            fail("Exception occurred during reflection: " + e.getMessage());
        }
    }
    
    @Test
    void testClientTrainerIdEqualsAndHashCode() {
        
        ClientTrainerId id1 = new ClientTrainerId(1L, 2L);
        ClientTrainerId id2 = new ClientTrainerId(1L, 2L);
        ClientTrainerId id3 = new ClientTrainerId(1L, 3L);
        ClientTrainerId id4 = new ClientTrainerId(3L, 2L);
        
        
        assertEquals(id1, id1); 
        assertEquals(id1, id2); 
        assertNotEquals(id1, id3); 
        assertNotEquals(id1, id4); 
        assertNotEquals(id1, null); 
        assertNotEquals(id1, new Object()); 
        
        
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
        assertNotEquals(id1.hashCode(), id4.hashCode());
    }
}
