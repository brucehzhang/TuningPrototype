package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.AssetSale;
import com.tuning.tuningprototype.models.db.entity.AssetSaleDto;
import com.tuning.tuningprototype.models.db.entity.PurchaseLot;
import com.tuning.tuningprototype.models.db.entity.PurchaseLotDto;
import com.tuning.tuningprototype.testutil.UninitializedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseLotMapperTest {

    @Mock
    private AssetSaleMapper assetSaleMapper;

    private PurchaseLotMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PurchaseLotMapper(assetSaleMapper);
    }

    @Test
    void toDto_returnsNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_mapsScalarFields_andAssetSales_whenInitialized() {
        AssetSale assetSale = AssetSale.builder().id(9L).build();
        AssetSaleDto assetSaleDto = new AssetSaleDto(9L, 1L, 1L, "AAPL", null, null, null, null, null);
        when(assetSaleMapper.toDto(assetSale)).thenReturn(assetSaleDto);

        List<AssetSale> assetSales = new ArrayList<>();
        assetSales.add(assetSale);

        PurchaseLot entity = PurchaseLot.builder()
                .id(1L)
                .purchaseDecisionId(2L)
                .walletId(3L)
                .ticker("AAPL")
                .purchasePrice(new BigDecimal("100.00"))
                .purchaseQuantity(new BigDecimal("5.0"))
                .purchaseTime(1000L)
                .createdTime(2000L)
                .modifiedTime(3000L)
                .assetSales(assetSales)
                .build();

        PurchaseLotDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.purchaseDecisionId()).isEqualTo(2L);
        assertThat(dto.walletId()).isEqualTo(3L);
        assertThat(dto.ticker()).isEqualTo("AAPL");
        assertThat(dto.purchasePrice()).isEqualTo(new BigDecimal("100.00"));
        assertThat(dto.purchaseQuantity()).isEqualTo(new BigDecimal("5.0"));
        assertThat(dto.purchaseTime()).isEqualTo(1000L);
        assertThat(dto.createdTime()).isEqualTo(2000L);
        assertThat(dto.modifiedTime()).isEqualTo(3000L);
        assertThat(dto.assetSales()).containsExactly(assetSaleDto);
    }

    @Test
    void toDto_assetSalesIsNull_whenCollectionUninitialized() {
        PurchaseLot entity = PurchaseLot.builder()
                .id(1L)
                .assetSales(new UninitializedList<>())
                .build();

        PurchaseLotDto dto = mapper.toDto(entity);

        assertThat(dto.assetSales()).isNull();
        verifyNoInteractions(assetSaleMapper);
    }

    @Test
    void toEntity_returnsNull_whenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        PurchaseLotDto dto = new PurchaseLotDto(
                1L, 2L, 3L, "AAPL",
                new BigDecimal("100.00"), new BigDecimal("5.0"),
                1000L, 2000L, 3000L, List.of());

        PurchaseLot entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getPurchaseDecisionId()).isEqualTo(2L);
        assertThat(entity.getWalletId()).isEqualTo(3L);
        assertThat(entity.getTicker()).isEqualTo("AAPL");
        assertThat(entity.getPurchasePrice()).isEqualTo(new BigDecimal("100.00"));
        assertThat(entity.getPurchaseQuantity()).isEqualTo(new BigDecimal("5.0"));
        assertThat(entity.getPurchaseTime()).isEqualTo(1000L);
        assertThat(entity.getCreatedTime()).isEqualTo(2000L);
        assertThat(entity.getModifiedTime()).isEqualTo(3000L);
    }
}
