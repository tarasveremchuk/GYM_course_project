package com.gym.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.*;

class StaffRoleConverterTest {

    private StaffRoleConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StaffRoleConverter();
    }

    @ParameterizedTest
    @EnumSource(StaffRole.class)
    void testConvertToDatabaseColumn(StaffRole role) {
        
        assertEquals(role.getValue(), converter.convertToDatabaseColumn(role));
    }

    @Test
    void testConvertToDatabaseColumnNull() {
        
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @ParameterizedTest
    @EnumSource(StaffRole.class)
    void testConvertToEntityAttribute(StaffRole role) {
        
        String dbValue = role.getValue();
        assertEquals(role, converter.convertToEntityAttribute(dbValue));
    }

    @Test
    void testConvertToEntityAttributeWithUpperCase() {
        
        assertEquals(StaffRole.TRAINER, converter.convertToEntityAttribute("TRAINER"));
        assertEquals(StaffRole.ADMIN, converter.convertToEntityAttribute("ADMIN"));
        assertEquals(StaffRole.CLEANER, converter.convertToEntityAttribute("CLEANER"));
    }

    @ParameterizedTest
    @NullSource
    void testConvertToEntityAttributeNull(String value) {
        
        assertNull(converter.convertToEntityAttribute(value));
    }

    @Test
    void testConvertToEntityAttributeInvalid() {
        
        assertThrows(IllegalArgumentException.class, 
            () -> converter.convertToEntityAttribute("invalid_role"));
    }
}
