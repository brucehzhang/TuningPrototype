package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.PurchaseLot;
import com.tuning.tuningprototype.models.requests.CreatePurchaseLotRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseLotRequestMapperTest {

    private final PurchaseLotRequestMapper mapper = new PurchaseLotRequestMapper();

    @Test
    void toEntity_mapsRequestFields_andSetsServerControlledFields() {
        CreatePurchaseLotRequest request = new CreatePurchaseLotRequest(
                20L, "AAPL", new BigDecimal("100.00"), new BigDecimal("3.0"), 1000L);

        PurchaseLot entity = mapper.toEntity(request, 77L, 5000L);

        assertThat(entity.getPurchaseDecisionId()).isEqualTo(77L);
        assertThat(entity.getWalletId()).isEqualTo(20L);
        assertThat(entity.getTicker()).isEqualTo("AAPL");
        assertThat(entity.getPurchasePrice()).isEqualTo(new BigDecimal("100.00"));
        assertThat(entity.getPurchaseQuantity()).isEqualTo(new BigDecimal("3.0"));
        assertThat(entity.getPurchaseTime()).isEqualTo(1000L);
        assertThat(entity.getCreatedTime()).isEqualTo(5000L);
        assertThat(entity.getModifiedTime()).isEqualTo(5000L);
    }
}
