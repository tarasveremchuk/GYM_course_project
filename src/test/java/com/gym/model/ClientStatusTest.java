package com.gym.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ClientStatusTest {

    @Test
    void testEnumValues() {
        
        assertEquals(3, ClientStatus.values().length);
        assertNotNull(ClientStatus.ACTIVE);
        assertNotNull(ClientStatus.INACTIVE);
        assertNotNull(ClientStatus.SUSPENDED);
    }

    @Test
    void testGetValue() {
        
        assertEquals("active", ClientStatus.ACTIVE.getValue());
        assertEquals("inactive", ClientStatus.INACTIVE.getValue());
        assertEquals("suspended", ClientStatus.SUSPENDED.getValue());
    }

    @ParameterizedTest
    @MethodSource("validStatusProvider")
    void testFromValueValid(String input, ClientStatus expected) {
        
        assertEquals(expected, ClientStatus.fromValue(input));
    }

    @ParameterizedTest
    @NullSource
    void testFromValueNull(String input) {
        
        assertNull(ClientStatus.fromValue(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "unknown", "123"})
    void testFromValueInvalid(String input) {
        
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> ClientStatus.fromValue(input));
        assertEquals("Unknown ClientStatus value: " + input, exception.getMessage());
    }

    
    private static Stream<Arguments> validStatusProvider() {
        return Stream.of(
            
            Arguments.of("ACTIVE", ClientStatus.ACTIVE),
            Arguments.of("INACTIVE", ClientStatus.INACTIVE),
            Arguments.of("SUSPENDED", ClientStatus.SUSPENDED),
            
            Arguments.of("active", ClientStatus.ACTIVE),
            Arguments.of("inactive", ClientStatus.INACTIVE),
            Arguments.of("suspended", ClientStatus.SUSPENDED),
            
            Arguments.of("Active", ClientStatus.ACTIVE),
            Arguments.of("Inactive", ClientStatus.INACTIVE),
            Arguments.of("Suspended", ClientStatus.SUSPENDED)
        );
    }
}
