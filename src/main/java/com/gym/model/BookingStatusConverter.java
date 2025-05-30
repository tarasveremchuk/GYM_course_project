package com.gym.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BookingStatusConverter implements AttributeConverter<BookingStatus, String> {
    @Override
    public String convertToDatabaseColumn(BookingStatus status) {
        return status == null ? null : status.getValue();
    }

    @Override
    public BookingStatus convertToEntityAttribute(String value) {
        return value == null ? null : BookingStatus.fromValue(value);
    }
} 