package com.tuning.tuningprototype.models.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Converter
public class UnixTimestampConverter implements AttributeConverter<Long, LocalDateTime> {

    private static final ZoneOffset ZONE = ZoneOffset.UTC;

    @Override
    public LocalDateTime convertToDatabaseColumn(Long unixTimeSeconds) {
        return unixTimeSeconds == null
                ? null
                : LocalDateTime.ofEpochSecond(unixTimeSeconds, 0, ZONE);
    }

    @Override
    public Long convertToEntityAttribute(LocalDateTime dbData) {
        return dbData == null
                ? null
                : dbData.toEpochSecond(ZONE);
    }
}