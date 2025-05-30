package com.gym.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BookingStatusTest {

    @Test
    void testEnumValues() {
        
        assertEquals(3, BookingStatus.values().length);
        assertNotNull(BookingStatus.BOOKED);
        assertNotNull(BookingStatus.CANCELLED);
        assertNotNull(BookingStatus.ATTENDED);
    }

    @Test
    void testGetValue() {
        
        assertEquals("booked", BookingStatus.BOOKED.getValue());
        assertEquals("cancelled", BookingStatus.CANCELLED.getValue());
        assertEquals("attended", BookingStatus.ATTENDED.getValue());
    }

    @ParameterizedTest
    @MethodSource("validStatusProvider")
    void testFromValueValid(String input, BookingStatus expected) {
        
        assertEquals(expected, BookingStatus.fromValue(input));
    }

    @ParameterizedTest
    @NullSource
    void testFromValueNull(String input) {
        
        assertNull(BookingStatus.fromValue(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "unknown", "123"})
    void testFromValueInvalid(String input) {
        
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> BookingStatus.fromValue(input));
        assertEquals("Unknown BookingStatus value: " + input, exception.getMessage());
    }

    
    private static Stream<Arguments> validStatusProvider() {
        return Stream.of(
            Arguments.of("booked", BookingStatus.BOOKED),
            Arguments.of("BOOKED", BookingStatus.BOOKED),
            Arguments.of("Booked", BookingStatus.BOOKED),
            Arguments.of("cancelled", BookingStatus.CANCELLED),
            Arguments.of("CANCELLED", BookingStatus.CANCELLED),
            Arguments.of("attended", BookingStatus.ATTENDED),
            Arguments.of("ATTENDED", BookingStatus.ATTENDED)
        );
    }
}
