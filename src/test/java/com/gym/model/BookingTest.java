package com.gym.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void testBookingCreation() {
        
        Booking booking = new Booking();
        assertNull(booking.getId());
        assertNull(booking.getTraining());
        assertNull(booking.getClient());
        assertNull(booking.getBookingTime());
        assertNull(booking.getStatus());
    }

    @Test
    void testBookingSettersAndGetters() {
        
        Booking booking = new Booking();
        
        
        Training training = new Training();
        training.setId(1L);
        
        Client client = new Client();
        client.setId(2L);
        
        LocalDateTime bookingTime = LocalDateTime.of(2025, 5, 30, 10, 0);
        
        
        booking.setId(3L);
        booking.setTraining(training);
        booking.setClient(client);
        booking.setBookingTime(bookingTime);
        booking.setStatus(BookingStatus.BOOKED);
        
        
        assertEquals(3L, booking.getId());
        assertEquals(training, booking.getTraining());
        assertEquals(client, booking.getClient());
        assertEquals(bookingTime, booking.getBookingTime());
        assertEquals(BookingStatus.BOOKED, booking.getStatus());
    }

    @Test
    void testEqualsAndHashCode() {
        
        Booking booking1 = new Booking();
        booking1.setId(1L);
        
        Booking booking2 = new Booking();
        booking2.setId(1L);
        
        Booking booking3 = new Booking();
        booking3.setId(2L);
        
        
        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
        
        
        assertEquals(booking1.hashCode(), booking2.hashCode());
        assertNotEquals(booking1.hashCode(), booking3.hashCode());
    }

    @Test
    void testToString() {
        
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.BOOKED);
        
        String toString = booking.toString();
        
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("status=BOOKED"));
    }
    
    @Test
    void testStatusTransitions() {
        
        Booking booking = new Booking();
        
        
        booking.setStatus(BookingStatus.BOOKED);
        assertEquals(BookingStatus.BOOKED, booking.getStatus());
        
        
        booking.setStatus(BookingStatus.CANCELLED);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        
        
        booking.setStatus(BookingStatus.ATTENDED);
        assertEquals(BookingStatus.ATTENDED, booking.getStatus());
    }
}
