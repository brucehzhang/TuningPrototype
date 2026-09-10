package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.Session;
import com.tuning.tuningprototype.models.db.entity.SessionDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionMapperTest {

    private final SessionMapper mapper = new SessionMapper();

    @Test
    void toDto_returnsNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_mapsAllFields() {
        Session entity = Session.builder()
                .id(1L)
                .userId(2L)
                .sessionToken("token-abc")
                .createdTime(1000L)
                .expiresTime(2000L)
                .modifiedTime(3000L)
                .build();

        SessionDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.userId()).isEqualTo(2L);
        assertThat(dto.sessionToken()).isEqualTo("token-abc");
        assertThat(dto.createdTime()).isEqualTo(1000L);
        assertThat(dto.expiresTime()).isEqualTo(2000L);
        assertThat(dto.modifiedTime()).isEqualTo(3000L);
    }

    @Test
    void toEntity_returnsNull_whenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        SessionDto dto = new SessionDto(1L, 2L, "token-abc", 1000L, 2000L, 3000L);

        Session entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(2L);
        assertThat(entity.getSessionToken()).isEqualTo("token-abc");
        assertThat(entity.getCreatedTime()).isEqualTo(1000L);
        assertThat(entity.getExpiresTime()).isEqualTo(2000L);
        assertThat(entity.getModifiedTime()).isEqualTo(3000L);
    }
}
