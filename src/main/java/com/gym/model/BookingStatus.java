package com.gym.model;

public enum BookingStatus {
    BOOKED("booked"),
    CANCELLED("cancelled"),
    ATTENDED("attended");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BookingStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        for (BookingStatus status : BookingStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown BookingStatus value: " + value);
    }
} 