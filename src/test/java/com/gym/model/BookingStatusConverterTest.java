package com.gym.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.*;

class BookingStatusConverterTest {

    private BookingStatusConverter converter;

    @BeforeEach
    void setUp() {
        converter = new BookingStatusConverter();
    }

    @ParameterizedTest
    @EnumSource(BookingStatus.class)
    void testConvertToDatabaseColumn(BookingStatus status) {
        
        assertEquals(status.getValue(), converter.convertToDatabaseColumn(status));
    }

    @Test
    void testConvertToDatabaseColumnNull() {
        
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @ParameterizedTest
    @EnumSource(BookingStatus.class)
    void testConvertToEntityAttribute(BookingStatus status) {
        
        String dbValue = status.getValue();
        assertEquals(status, converter.convertToEntityAttribute(dbValue));
    }

    @Test
    void testConvertToEntityAttributeWithUpperCase() {
        
        assertEquals(BookingStatus.BOOKED, converter.convertToEntityAttribute("BOOKED"));
        assertEquals(BookingStatus.CANCELLED, converter.convertToEntityAttribute("CANCELLED"));
        assertEquals(BookingStatus.ATTENDED, converter.convertToEntityAttribute("ATTENDED"));
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
