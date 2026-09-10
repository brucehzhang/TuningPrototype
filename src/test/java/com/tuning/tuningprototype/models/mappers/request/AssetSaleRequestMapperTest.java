package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.AssetSale;
import com.tuning.tuningprototype.models.requests.CreateAssetSaleRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AssetSaleRequestMapperTest {

    private final AssetSaleRequestMapper mapper = new AssetSaleRequestMapper();

    @Test
    void toEntity_mapsRequestFields_andSetsServerControlledFields() {
        CreateAssetSaleRequest request = new CreateAssetSaleRequest(
                10L, "AAPL", new BigDecimal("150.25"), new BigDecimal("5.0"), 1000L);

        AssetSale entity = mapper.toEntity(request, 99L, 5000L);

        assertThat(entity.getSaleDecisionId()).isEqualTo(99L);
        assertThat(entity.getPurchaseLotId()).isEqualTo(10L);
        assertThat(entity.getTicker()).isEqualTo("AAPL");
        assertThat(entity.getSalePrice()).isEqualTo(new BigDecimal("150.25"));
        assertThat(entity.getSaleQuantity()).isEqualTo(new BigDecimal("5.0"));
        assertThat(entity.getSaleTime()).isEqualTo(1000L);
        assertThat(entity.getCreatedTime()).isEqualTo(5000L);
        assertThat(entity.getModifiedTime()).isEqualTo(5000L);
    }
}
