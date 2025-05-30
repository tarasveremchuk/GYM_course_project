package com.gym.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.*;

class ClientStatusConverterTest {

    private ClientStatusConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ClientStatusConverter();
    }

    @ParameterizedTest
    @EnumSource(ClientStatus.class)
    void testConvertToDatabaseColumn(ClientStatus status) {
        
        assertEquals(status.getValue(), converter.convertToDatabaseColumn(status));
    }

    @Test
    void testConvertToDatabaseColumnNull() {
        
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @ParameterizedTest
    @EnumSource(ClientStatus.class)
    void testConvertToEntityAttribute(ClientStatus status) {
        
        String dbValue = status.getValue();
        assertEquals(status, converter.convertToEntityAttribute(dbValue));
    }

    @Test
    void testConvertToEntityAttributeWithUpperCase() {
        
        assertEquals(ClientStatus.ACTIVE, converter.convertToEntityAttribute("ACTIVE"));
        assertEquals(ClientStatus.INACTIVE, converter.convertToEntityAttribute("INACTIVE"));
        assertEquals(ClientStatus.SUSPENDED, converter.convertToEntityAttribute("SUSPENDED"));
    }

    @ParameterizedTest
    @NullSource
    void testConvertToEntityAttributeNull(String value) {
        
        assertNull(converter.convertToEntityAttribute(value));
    }

    @Test
    void testConvertToEntityAttributeInvalid() {
        
        assertThrows(IllegalArgumentException.class, 
            () -> converter.convertToEntityAttribute("invalid_status"));
    }
}
