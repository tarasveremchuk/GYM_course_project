package com.gym.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OutstandingPaymentTest {

    @Test
    void testDefaultConstructor() {
        
        OutstandingPayment payment = new OutstandingPayment();
        
        
        assertNull(payment.getClientId());
        assertNull(payment.getClientName());
        assertNull(payment.getMembershipType());
        assertNull(payment.getStartDate());
        assertNull(payment.getEndDate());
    }

    @Test
    void testSettersAndGetters() {
        
        OutstandingPayment payment = new OutstandingPayment();
        
        
        payment.setClientId(1L);
        payment.setClientName("John Doe");
        payment.setMembershipType("unlimited");
        
        LocalDate startDate = LocalDate.of(2025, 5, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 1);
        
        payment.setStartDate(startDate);
        payment.setEndDate(endDate);
        
        
        assertEquals(1L, payment.getClientId());
        assertEquals("John Doe", payment.getClientName());
        assertEquals("unlimited", payment.getMembershipType());
        assertEquals(startDate, payment.getStartDate());
        assertEquals(endDate, payment.getEndDate());
    }

    @Test
    void testEqualsAndHashCode() {
        
        OutstandingPayment payment1 = new OutstandingPayment();
        payment1.setClientId(1L);
        payment1.setClientName("John Doe");
        payment1.setMembershipType("unlimited");
        
        OutstandingPayment payment2 = new OutstandingPayment();
        payment2.setClientId(1L);
        payment2.setClientName("John Doe");
        payment2.setMembershipType("unlimited");
        
        OutstandingPayment payment3 = new OutstandingPayment();
        payment3.setClientId(2L);
        payment3.setClientName("Jane Smith");
        payment3.setMembershipType("limited");
        
        
        assertEquals(payment1, payment2);
        assertNotEquals(payment1, payment3);
        
        
        assertEquals(payment1.hashCode(), payment2.hashCode());
        assertNotEquals(payment1.hashCode(), payment3.hashCode());
    }

    @Test
    void testToString() {
        
        OutstandingPayment payment = new OutstandingPayment();
        payment.setClientId(1L);
        payment.setClientName("John Doe");
        payment.setMembershipType("unlimited");
        
        LocalDate startDate = LocalDate.of(2025, 5, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 1);
        
        payment.setStartDate(startDate);
        payment.setEndDate(endDate);
        
        String toString = payment.toString();
        
        
        assertTrue(toString.contains("clientId=1"));
        assertTrue(toString.contains("clientName=John Doe"));
        assertTrue(toString.contains("membershipType=unlimited"));
        assertTrue(toString.contains("startDate=" + startDate.toString()));
        assertTrue(toString.contains("endDate=" + endDate.toString()));
    }
    
    @Test
    void testDifferentMembershipTypes() {
        
        OutstandingPayment payment = new OutstandingPayment();
        
        
        payment.setMembershipType("unlimited");
        assertEquals("unlimited", payment.getMembershipType());
        
        
        payment.setMembershipType("limited");
        assertEquals("limited", payment.getMembershipType());
        
        
        payment.setMembershipType("fixed");
        assertEquals("fixed", payment.getMembershipType());
    }
}
