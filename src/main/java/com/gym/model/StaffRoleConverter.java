package com.gym.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StaffRoleConverter implements AttributeConverter<StaffRole, String> {
    @Override
    public String convertToDatabaseColumn(StaffRole role) {
        return role == null ? null : role.getValue();
    }

    @Override
    public StaffRole convertToEntityAttribute(String value) {
        return value == null ? null : StaffRole.fromValue(value);
    }
} 