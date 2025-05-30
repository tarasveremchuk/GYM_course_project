package com.gym.model;

public enum ClientStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    SUSPENDED("suspended");

    private final String value;

    ClientStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ClientStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        String upperValue = value.toUpperCase();
        for (ClientStatus status : ClientStatus.values()) {
            if (status.name().equals(upperValue) || status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ClientStatus value: " + value);
    }
} 