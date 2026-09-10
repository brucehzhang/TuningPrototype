package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.AssetSale;
import com.tuning.tuningprototype.models.db.entity.AssetSaleDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AssetSaleMapperTest {

    private final AssetSaleMapper mapper = new AssetSaleMapper();

    @Test
    void toDto_returnsNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_mapsAllFields() {
        AssetSale entity = AssetSale.builder()
                .id(1L)
                .saleDecisionId(2L)
                .purchaseLotId(3L)
                .ticker("AAPL")
                .salePrice(new BigDecimal("150.25"))
                .saleQuantity(new BigDecimal("10.5"))
                .saleTime(1000L)
                .createdTime(2000L)
                .modifiedTime(3000L)
                .build();

        AssetSaleDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.saleDecisionId()).isEqualTo(2L);
        assertThat(dto.purchaseLotId()).isEqualTo(3L);
        assertThat(dto.ticker()).isEqualTo("AAPL");
        assertThat(dto.salePrice()).isEqualTo(new BigDecimal("150.25"));
        assertThat(dto.saleQuantity()).isEqualTo(new BigDecimal("10.5"));
        assertThat(dto.saleTime()).isEqualTo(1000L);
        assertThat(dto.createdTime()).isEqualTo(2000L);
        assertThat(dto.modifiedTime()).isEqualTo(3000L);
    }

    @Test
    void toEntity_returnsNull_whenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        AssetSaleDto dto = new AssetSaleDto(
                1L, 2L, 3L, "AAPL",
                new BigDecimal("150.25"), new BigDecimal("10.5"),
                1000L, 2000L, 3000L);

        AssetSale entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getSaleDecisionId()).isEqualTo(2L);
        assertThat(entity.getPurchaseLotId()).isEqualTo(3L);
        assertThat(entity.getTicker()).isEqualTo("AAPL");
        assertThat(entity.getSalePrice()).isEqualTo(new BigDecimal("150.25"));
        assertThat(entity.getSaleQuantity()).isEqualTo(new BigDecimal("10.5"));
        assertThat(entity.getSaleTime()).isEqualTo(1000L);
        assertThat(entity.getCreatedTime()).isEqualTo(2000L);
        assertThat(entity.getModifiedTime()).isEqualTo(3000L);
    }
}
