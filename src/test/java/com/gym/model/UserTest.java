package com.gym.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;


import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPasswordHash());
        assertNull(user.getRole());
        assertNull(user.getClientId());
    }

    @Test
    void testUserSettersAndGetters() {
        
        User user = new User();
        
        
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("hashedpassword123");
        user.setRole(User.UserRole.client);
        user.setClientId(5L);
        
        
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpassword123", user.getPasswordHash());
        assertEquals(User.UserRole.client, user.getRole());
        assertEquals(5L, user.getClientId());
    }

    @ParameterizedTest
    @EnumSource(User.UserRole.class)
    void testUserRoleEnum(User.UserRole role) {
        
        User user = new User();
        user.setRole(role);
        assertEquals(role, user.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        
        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("user1");
        
        User user3 = new User();
        user3.setId(2L);
        user3.setUsername("user2");
        
        
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        
        
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testToString() {
        
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("hash123");
        user.setRole(User.UserRole.admin);
        
        String toString = user.toString();
        
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("username=testuser"));
        assertTrue(toString.contains("passwordHash=hash123"));
        assertTrue(toString.contains("role=admin"));
    }
    
    @Test
    void testUserRoleValues() {
        
        assertEquals(3, User.UserRole.values().length);
        assertNotNull(User.UserRole.admin);
        assertNotNull(User.UserRole.trainer);
        assertNotNull(User.UserRole.client);
    }
}
