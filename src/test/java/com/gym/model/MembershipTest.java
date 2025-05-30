package com.gym.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MembershipTest {

    @Test
    void testDefaultConstructor() {
        
        Membership membership = new Membership();
        
        
        assertNull(membership.getId());
        assertNull(membership.getClient());
        assertNull(membership.getType());
        assertNull(membership.getStartDate());
        assertNull(membership.getEndDate());
        assertNull(membership.getVisitsLeft());
        assertFalse(membership.isPaid());
        assertNull(membership.getPrice());
    }

    @Test
    void testSettersAndGetters() {
        
        Membership membership = new Membership();
        
        
        Client client = new Client();
        client.setId(1L);
        client.setFullName("John Doe");
        
        
        membership.setId(1L);
        membership.setClient(client);
        membership.setType(Membership.MembershipType.unlimited);
        
        LocalDate startDate = LocalDate.of(2025, 5, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 1);
        
        membership.setStartDate(startDate);
        membership.setEndDate(endDate);
        membership.setVisitsLeft(null); 
        membership.setPaid(true);
        membership.setPrice(1000.0);
        
        
        assertEquals(1L, membership.getId());
        assertEquals(client, membership.getClient());
        assertEquals(Membership.MembershipType.unlimited, membership.getType());
        assertEquals(startDate, membership.getStartDate());
        assertEquals(endDate, membership.getEndDate());
        assertNull(membership.getVisitsLeft());
        assertTrue(membership.isPaid());
        assertEquals(1000.0, membership.getPrice());
    }

    @ParameterizedTest
    @EnumSource(Membership.MembershipType.class)
    void testMembershipTypes(Membership.MembershipType type) {
        
        Membership membership = new Membership();
        membership.setType(type);
        assertEquals(type, membership.getType());
    }

    @Test
    void testMembershipTypesExist() {
        
        assertEquals(3, Membership.MembershipType.values().length);
        assertNotNull(Membership.MembershipType.fixed);
        assertNotNull(Membership.MembershipType.limited);
        assertNotNull(Membership.MembershipType.unlimited);
    }

    @Test
    void testLimitedMembership() {
        
        Membership membership = new Membership();
        membership.setType(Membership.MembershipType.limited);
        membership.setVisitsLeft(10);
        
        assertEquals(Membership.MembershipType.limited, membership.getType());
        assertEquals(10, membership.getVisitsLeft());
    }

    @Test
    void testFixedMembership() {
        
        Membership membership = new Membership();
        membership.setType(Membership.MembershipType.fixed);
        
        LocalDate startDate = LocalDate.of(2025, 5, 1);
        LocalDate endDate = LocalDate.of(2025, 5, 31);
        
        membership.setStartDate(startDate);
        membership.setEndDate(endDate);
        
        assertEquals(Membership.MembershipType.fixed, membership.getType());
        assertEquals(startDate, membership.getStartDate());
        assertEquals(endDate, membership.getEndDate());
    }

    @Test
    void testEqualsAndHashCode() {
        
        Membership membership1 = new Membership();
        membership1.setId(1L);
        
        Membership membership2 = new Membership();
        membership2.setId(1L);
        
        Membership membership3 = new Membership();
        membership3.setId(2L);
        
        
        assertEquals(membership1, membership2);
        assertNotEquals(membership1, membership3);
        
        
        assertEquals(membership1.hashCode(), membership2.hashCode());
        assertNotEquals(membership1.hashCode(), membership3.hashCode());
    }

    @Test
    void testToString() {
        
        Membership membership = new Membership();
        membership.setId(1L);
        membership.setType(Membership.MembershipType.unlimited);
        membership.setPaid(true);
        
        String toString = membership.toString();
        
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("type=unlimited"));
        assertTrue(toString.contains("isPaid=true"));
    }
}
