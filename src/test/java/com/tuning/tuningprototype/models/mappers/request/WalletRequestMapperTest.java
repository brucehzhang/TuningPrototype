package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.Wallet;
import com.tuning.tuningprototype.models.requests.CreateWalletRequest;
import com.tuning.tuningprototype.models.requests.UpdateWalletRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class WalletRequestMapperTest {

    private final WalletRequestMapper mapper = new WalletRequestMapper();

    @Test
    void toEntity_mapsRequestFields_andSetsServerControlledFields() {
        CreateWalletRequest request = new CreateWalletRequest(1L, new BigDecimal("1000.00"), 1000L, "USD");

        Wallet entity = mapper.toEntity(request, 5000L);

        assertThat(entity.getExperimentId()).isEqualTo(1L);
        assertThat(entity.getStartingMoneyAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(entity.getOpenedTime()).isEqualTo(1000L);
        assertThat(entity.getCurrencyCode()).isEqualTo("USD");
        assertThat(entity.getCreatedTime()).isEqualTo(5000L);
        assertThat(entity.getModifiedTime()).isEqualTo(5000L);
    }

    private Wallet existingWallet() {
        return Wallet.builder()
                .id(1L)
                .experimentId(2L)
                .startingMoneyAmount(new BigDecimal("1000.00"))
                .openedTime(1000L)
                .currencyCode("USD")
                .createdTime(2000L)
                .modifiedTime(3000L)
                .build();
    }

    @Test
    void applyUpdate_overwritesFields_whenRequestFieldsAreNonNull() {
        Wallet existing = existingWallet();
        UpdateWalletRequest request = new UpdateWalletRequest(1L, new BigDecimal("2000.00"), "EUR");

        mapper.applyUpdate(request, existing, 9000L);

        assertThat(existing.getStartingMoneyAmount()).isEqualTo(new BigDecimal("2000.00"));
        assertThat(existing.getCurrencyCode()).isEqualTo("EUR");
        assertThat(existing.getModifiedTime()).isEqualTo(9000L);
        // untouched field remains
        assertThat(existing.getExperimentId()).isEqualTo(2L);
    }

    @Test
    void applyUpdate_leavesExistingValues_whenRequestFieldsAreNull() {
        Wallet existing = existingWallet();
        UpdateWalletRequest request = new UpdateWalletRequest(1L, null, null);

        mapper.applyUpdate(request, existing, 9000L);

        assertThat(existing.getStartingMoneyAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(existing.getCurrencyCode()).isEqualTo("USD");
        assertThat(existing.getModifiedTime()).isEqualTo(9000L);
    }
}
