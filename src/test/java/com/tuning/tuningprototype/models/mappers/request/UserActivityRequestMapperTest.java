package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.Session;
import com.tuning.tuningprototype.models.db.entity.User;
import com.tuning.tuningprototype.models.enums.LicenseType;
import com.tuning.tuningprototype.models.requests.SignUpRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserActivityRequestMapperTest {

    private final UserActivityRequestMapper mapper = new UserActivityRequestMapper();

    @Test
    void toUserEntity_mapsRequestFields_andSetsServerControlledFields() {
        SignUpRequest request = new SignUpRequest(
                "Jane", "Q", "Doe", "jdoe", "jdoe@example.com", "raw-password", 2L, LicenseType.PREMIUM);

        User entity = mapper.toUserEntity(request, "hashed-password", 5000L);

        assertThat(entity.getFirstName()).isEqualTo("Jane");
        assertThat(entity.getMiddleName()).isEqualTo("Q");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getUsername()).isEqualTo("jdoe");
        assertThat(entity.getEmail()).isEqualTo("jdoe@example.com");
        assertThat(entity.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(entity.getAccountId()).isEqualTo(2L);
        assertThat(entity.getLicenseType()).isEqualTo(LicenseType.PREMIUM);
        assertThat(entity.getCreatedTime()).isEqualTo(5000L);
        assertThat(entity.getModifiedTime()).isEqualTo(5000L);
    }

    @Test
    void toSessionEntity_mapsAllFields() {
        Session entity = mapper.toSessionEntity(1L, "token-abc", 5000L, 9000L);

        assertThat(entity.getUserId()).isEqualTo(1L);
        assertThat(entity.getSessionToken()).isEqualTo("token-abc");
        assertThat(entity.getCreatedTime()).isEqualTo(5000L);
        assertThat(entity.getExpiresTime()).isEqualTo(9000L);
        assertThat(entity.getModifiedTime()).isEqualTo(5000L);
    }
}
