package com.gym.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StaffRoleTest {

    @Test
    void testEnumValues() {
        
        assertEquals(3, StaffRole.values().length);
        assertNotNull(StaffRole.TRAINER);
        assertNotNull(StaffRole.ADMIN);
        assertNotNull(StaffRole.CLEANER);
    }

    @Test
    void testGetValue() {
        
        assertEquals("trainer", StaffRole.TRAINER.getValue());
        assertEquals("admin", StaffRole.ADMIN.getValue());
        assertEquals("cleaner", StaffRole.CLEANER.getValue());
    }

    @ParameterizedTest
    @MethodSource("validRoleProvider")
    void testFromValueValid(String input, StaffRole expected) {
        
        assertEquals(expected, StaffRole.fromValue(input));
    }

    @ParameterizedTest
    @NullSource
    void testFromValueNull(String input) {
        
        assertNull(StaffRole.fromValue(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "unknown", "123", "manager"})
    void testFromValueInvalid(String input) {
        
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> StaffRole.fromValue(input));
        assertEquals("Unknown StaffRole value: " + input, exception.getMessage());
    }

    
    private static Stream<Arguments> validRoleProvider() {
        return Stream.of(
            
            Arguments.of("TRAINER", StaffRole.TRAINER),
            Arguments.of("ADMIN", StaffRole.ADMIN),
            Arguments.of("CLEANER", StaffRole.CLEANER),
            
            Arguments.of("trainer", StaffRole.TRAINER),
            Arguments.of("admin", StaffRole.ADMIN),
            Arguments.of("cleaner", StaffRole.CLEANER),
            
            Arguments.of("Trainer", StaffRole.TRAINER),
            Arguments.of("Admin", StaffRole.ADMIN),
            Arguments.of("Cleaner", StaffRole.CLEANER)
        );
    }
}
