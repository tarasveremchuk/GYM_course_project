package com.gym.model;

public enum StaffRole {
    TRAINER("trainer"),
    ADMIN("admin"),
    CLEANER("cleaner");

    private final String value;

    StaffRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StaffRole fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        String upperValue = value.toUpperCase();
        for (StaffRole role : StaffRole.values()) {
            if (role.name().equals(upperValue) || role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown StaffRole value: " + value);
    }
} 