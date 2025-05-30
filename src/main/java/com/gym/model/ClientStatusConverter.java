package com.gym.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ClientStatusConverter implements AttributeConverter<ClientStatus, String> {
    @Override
    public String convertToDatabaseColumn(ClientStatus status) {
        return status == null ? null : status.getValue();
    }

    @Override
    public ClientStatus convertToEntityAttribute(String value) {
        return value == null ? null : ClientStatus.fromValue(value);
    }
} 