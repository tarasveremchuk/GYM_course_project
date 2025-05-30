package com.gym.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void testDefaultConstructor() {
        
        Payment payment = new Payment();
        
        
        assertNull(payment.getId());
        assertNull(payment.getClient());
        assertNull(payment.getAmount());
        assertNull(payment.getPaymentDate());
        assertNull(payment.getMethod());
        assertNull(payment.getNotes());
    }

    @Test
    void testSettersAndGetters() {
        
        Payment payment = new Payment();
        
        
        Client client = new Client();
        client.setId(1L);
        client.setFullName("John Doe");
        
        
        payment.setId(1L);
        payment.setClient(client);
        payment.setAmount(new BigDecimal("500.50"));
        
        LocalDateTime paymentDate = LocalDateTime.of(2025, 5, 30, 14, 30);
        payment.setPaymentDate(paymentDate);
        payment.setMethod("Credit Card");
        payment.setNotes("Monthly membership payment");
        
        
        assertEquals(1L, payment.getId());
        assertEquals(client, payment.getClient());
        assertEquals(new BigDecimal("500.50"), payment.getAmount());
        assertEquals(paymentDate, payment.getPaymentDate());
        assertEquals("Credit Card", payment.getMethod());
        assertEquals("Monthly membership payment", payment.getNotes());
    }

    @Test
    void testAmountPrecision() {
        
        Payment payment = new Payment();
        
        
        BigDecimal amount1 = new BigDecimal("1000.00");
        payment.setAmount(amount1);
        assertEquals(amount1, payment.getAmount());
        
        
        BigDecimal amount2 = new BigDecimal("1000.557");
        payment.setAmount(amount2);
        assertEquals(amount2, payment.getAmount());
    }

    @Test
    void testEqualsAndHashCode() {
        
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setAmount(new BigDecimal("500.00"));
        
        Payment payment2 = new Payment();
        payment2.setId(1L);
        payment2.setAmount(new BigDecimal("500.00"));
        
        Payment payment3 = new Payment();
        payment3.setId(2L);
        payment3.setAmount(new BigDecimal("700.00"));
        
        
        assertEquals(payment1, payment2);
        assertNotEquals(payment1, payment3);
        
        
        assertEquals(payment1.hashCode(), payment2.hashCode());
        assertNotEquals(payment1.hashCode(), payment3.hashCode());
    }

    @Test
    void testToString() {
        
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("500.00"));
        payment.setMethod("Cash");
        
        String toString = payment.toString();
        
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("amount=500.00"));
        assertTrue(toString.contains("method=Cash"));
    }
    
    @Test
    void testDifferentPaymentMethods() {
        
        Payment payment = new Payment();
        
        
        payment.setMethod("Cash");
        assertEquals("Cash", payment.getMethod());
        
        
        payment.setMethod("Credit Card");
        assertEquals("Credit Card", payment.getMethod());
        
        
        payment.setMethod("Bank Transfer");
        assertEquals("Bank Transfer", payment.getMethod());
    }
}
