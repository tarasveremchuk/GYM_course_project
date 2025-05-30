package com.gym.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void testDefaultConstructor() {
        Client client = new Client();
        
        assertNull(client.getId());
        assertNull(client.getFullName());
        assertNull(client.getBirthDate());
        assertNull(client.getPhone());
        assertNull(client.getEmail());
        assertNull(client.getMedicalNotes());
        assertNull(client.getPhoto());
        assertEquals(ClientStatus.ACTIVE, client.getStatus());
        assertNull(client.getPhotoUrl());
        assertNull(client.getUser());
        
        assertNotNull(client.getBookings());
        assertTrue(client.getBookings().isEmpty());
        assertNotNull(client.getMemberships());
        assertTrue(client.getMemberships().isEmpty());
        assertNotNull(client.getVisits());
        assertTrue(client.getVisits().isEmpty());
        
        assertNotNull(client.fullNameProperty());
        assertNotNull(client.birthDateProperty());
        assertNotNull(client.phoneProperty());
        assertNotNull(client.emailProperty());
        assertNotNull(client.statusProperty());
    }

    @Test
    void testParameterizedConstructor() {
        String fullName = "John Doe";
        LocalDate birthDate = LocalDate.of(1990, 1, 15);
        String phone = "+380501234567";
        String email = "john.doe@example.com";
        ClientStatus status = ClientStatus.ACTIVE;
        
        Client client = new Client(fullName, birthDate, phone, email, status);
        
        assertEquals(fullName, client.getFullName());
        assertEquals(birthDate, client.getBirthDate());
        assertEquals(phone, client.getPhone());
        assertEquals(email, client.getEmail());
        assertEquals(status, client.getStatus());
        
        assertNotNull(client.getBookings());
        assertTrue(client.getBookings().isEmpty());
        assertNotNull(client.getMemberships());
        assertTrue(client.getMemberships().isEmpty());
        assertNotNull(client.getVisits());
        assertTrue(client.getVisits().isEmpty());
        
        assertEquals(fullName, client.fullNameProperty().get());
        assertEquals(birthDate, client.birthDateProperty().get());
        assertEquals(phone, client.phoneProperty().get());
        assertEquals(email, client.emailProperty().get());
        assertEquals(status, client.statusProperty().get());
    }

    @Test
    void testSettersAndGetters() {
        Client client = new Client();
        
        client.setId(1L);
        client.setFullName("Jane Smith");
        LocalDate birthDate = LocalDate.of(1985, 5, 20);
        client.setBirthDate(birthDate);
        client.setPhone("+380671234567");
        client.setEmail("jane.smith@example.com");
        client.setMedicalNotes("No allergies");
        byte[] photo = {1, 2, 3, 4, 5};
        client.setPhoto(photo);
        client.setStatus(ClientStatus.INACTIVE);
        client.setPhotoUrl("http://example.com/photo.jpg");
        
        User user = new User();
        user.setId(2L);
        client.setUser(user);
        
        assertEquals(1L, client.getId());
        assertEquals("Jane Smith", client.getFullName());
        assertEquals(birthDate, client.getBirthDate());
        assertEquals("+380671234567", client.getPhone());
        assertEquals("jane.smith@example.com", client.getEmail());
        assertEquals("No allergies", client.getMedicalNotes());
        assertArrayEquals(photo, client.getPhoto());
        assertEquals(ClientStatus.INACTIVE, client.getStatus());
        assertEquals("http://example.com/photo.jpg", client.getPhotoUrl());
        assertEquals(user, client.getUser());
    }

    @Test
    void testJavaFxPropertySetters() {
        Client client = new Client();
        
        client.setFullName("Alex Johnson");
        client.setBirthDate(LocalDate.of(1995, 10, 5));
        client.setPhone("+380991234567");
        client.setEmail("alex.johnson@example.com");
        client.setStatus(ClientStatus.SUSPENDED);
        
        assertEquals("Alex Johnson", client.fullNameProperty().get());
        assertEquals(LocalDate.of(1995, 10, 5), client.birthDateProperty().get());
        assertEquals("+380991234567", client.phoneProperty().get());
        assertEquals("alex.johnson@example.com", client.emailProperty().get());
        assertEquals(ClientStatus.SUSPENDED, client.statusProperty().get());
    }

    @Test
    void testJavaFxPropertyListeners() {
        Client client = new Client();
        
        client.fullNameProperty().set("Maria Garcia");
        client.birthDateProperty().set(LocalDate.of(2000, 3, 15));
        client.phoneProperty().set("+380631234567");
        client.emailProperty().set("maria.garcia@example.com");
        client.statusProperty().set(ClientStatus.INACTIVE);
        
        assertEquals("Maria Garcia", client.getFullName());
        assertEquals(LocalDate.of(2000, 3, 15), client.getBirthDate());
        assertEquals("+380631234567", client.getPhone());
        assertEquals("maria.garcia@example.com", client.getEmail());
        assertEquals(ClientStatus.INACTIVE, client.getStatus());
    }
    
    @Test
    void testCollectionManagement() {
        Client client = new Client();
        
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setClient(client);
        
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setClient(client);
        
        Membership membership1 = new Membership();
        membership1.setId(1L);
        membership1.setClient(client);
        
        Visit visit1 = new Visit();
        visit1.setId(1L);
        visit1.setClient(client);
        
        client.getBookings().add(booking1);
        client.getBookings().add(booking2);
        client.getMemberships().add(membership1);
        client.getVisits().add(visit1);
        
        assertEquals(2, client.getBookings().size());
        assertTrue(client.getBookings().contains(booking1));
        assertTrue(client.getBookings().contains(booking2));
        
        assertEquals(1, client.getMemberships().size());
        assertTrue(client.getMemberships().contains(membership1));
        
        assertEquals(1, client.getVisits().size());
        assertTrue(client.getVisits().contains(visit1));
        
        client.getBookings().remove(booking1);
        client.getMemberships().remove(membership1);
        
        assertEquals(1, client.getBookings().size());
        assertFalse(client.getBookings().contains(booking1));
        assertTrue(client.getBookings().contains(booking2));
        
        assertEquals(0, client.getMemberships().size());
        assertEquals(1, client.getVisits().size());
    }
}
