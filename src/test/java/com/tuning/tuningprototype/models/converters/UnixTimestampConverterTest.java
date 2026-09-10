package com.tuning.tuningprototype.models.converters;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UnixTimestampConverterTest {

    private final UnixTimestampConverter converter = new UnixTimestampConverter();

    @Test
    void convertToDatabaseColumn_returnsNull_whenInputIsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToDatabaseColumn_convertsEpochSecondsToUtcLocalDateTime() {
        LocalDateTime result = converter.convertToDatabaseColumn(1700000000L);

        assertThat(result).isEqualTo(LocalDateTime.of(2023, 11, 14, 22, 13, 20));
    }

    @Test
    void convertToDatabaseColumn_handlesEpoch() {
        LocalDateTime result = converter.convertToDatabaseColumn(0L);

        assertThat(result).isEqualTo(LocalDateTime.of(1970, 1, 1, 0, 0, 0));
    }

    @Test
    void convertToEntityAttribute_returnsNull_whenInputIsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_convertsUtcLocalDateTimeToEpochSeconds() {
        Long result = converter.convertToEntityAttribute(LocalDateTime.of(2023, 11, 14, 22, 13, 20));

        assertThat(result).isEqualTo(1700000000L);
    }

    @Test
    void roundTrip_preservesEpochSeconds() {
        long original = 1234567890L;

        Long result = converter.convertToEntityAttribute(converter.convertToDatabaseColumn(original));

        assertThat(result).isEqualTo(original);
    }
}
