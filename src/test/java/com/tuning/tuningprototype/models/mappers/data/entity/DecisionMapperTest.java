package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.AssetSale;
import com.tuning.tuningprototype.models.db.entity.AssetSaleDto;
import com.tuning.tuningprototype.models.db.entity.Decision;
import com.tuning.tuningprototype.models.db.entity.DecisionDto;
import com.tuning.tuningprototype.models.db.entity.PurchaseLot;
import com.tuning.tuningprototype.models.db.entity.PurchaseLotDto;
import com.tuning.tuningprototype.models.enums.DecisionType;
import com.tuning.tuningprototype.testutil.UninitializedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionMapperTest {

    @Mock
    private PurchaseLotMapper purchaseLotMapper;

    @Mock
    private AssetSaleMapper assetSaleMapper;

    private DecisionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DecisionMapper(purchaseLotMapper, assetSaleMapper);
    }

    @Test
    void toDto_returnsNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_mapsScalarFields_andNestedCollections_whenInitialized() {
        PurchaseLot purchaseLot = PurchaseLot.builder().id(10L).build();
        PurchaseLotDto purchaseLotDto = new PurchaseLotDto(10L, 1L, 1L, "AAPL", null, null, null, null, null, null);
        when(purchaseLotMapper.toDto(purchaseLot)).thenReturn(purchaseLotDto);

        AssetSale assetSale = AssetSale.builder().id(20L).build();
        AssetSaleDto assetSaleDto = new AssetSaleDto(20L, 1L, 1L, "AAPL", null, null, null, null, null);
        when(assetSaleMapper.toDto(assetSale)).thenReturn(assetSaleDto);

        List<PurchaseLot> purchaseLots = new ArrayList<>();
        purchaseLots.add(purchaseLot);
        List<AssetSale> assetSales = new ArrayList<>();
        assetSales.add(assetSale);

        Decision entity = Decision.builder()
                .id(1L)
                .sampleId(2L)
                .decisionType(DecisionType.BUY)
                .ticker("AAPL")
                .reasoning("strong earnings")
                .decisionTime(1000L)
                .createdTime(2000L)
                .modifiedTime(3000L)
                .purchaseLots(purchaseLots)
                .assetSales(assetSales)
                .build();

        DecisionDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.sampleId()).isEqualTo(2L);
        assertThat(dto.decisionType()).isEqualTo(DecisionType.BUY);
        assertThat(dto.ticker()).isEqualTo("AAPL");
        assertThat(dto.reasoning()).isEqualTo("strong earnings");
        assertThat(dto.decisionTime()).isEqualTo(1000L);
        assertThat(dto.createdTime()).isEqualTo(2000L);
        assertThat(dto.modifiedTime()).isEqualTo(3000L);
        assertThat(dto.purchaseLots()).containsExactly(purchaseLotDto);
        assertThat(dto.assetSales()).containsExactly(assetSaleDto);
    }

    @Test
    void toDto_nestedCollectionsAreNull_whenUninitialized() {
        Decision entity = Decision.builder()
                .id(1L)
                .purchaseLots(new UninitializedList<>())
                .assetSales(new UninitializedList<>())
                .build();

        DecisionDto dto = mapper.toDto(entity);

        assertThat(dto.purchaseLots()).isNull();
        assertThat(dto.assetSales()).isNull();
        verifyNoInteractions(purchaseLotMapper, assetSaleMapper);
    }

    @Test
    void toEntity_returnsNull_whenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        DecisionDto dto = new DecisionDto(
                1L, 2L, DecisionType.SELL, "AAPL", "reasoning",
                1000L, 2000L, 3000L, List.of(), List.of());

        Decision entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getSampleId()).isEqualTo(2L);
        assertThat(entity.getDecisionType()).isEqualTo(DecisionType.SELL);
        assertThat(entity.getTicker()).isEqualTo("AAPL");
        assertThat(entity.getReasoning()).isEqualTo("reasoning");
        assertThat(entity.getDecisionTime()).isEqualTo(1000L);
        assertThat(entity.getCreatedTime()).isEqualTo(2000L);
        assertThat(entity.getModifiedTime()).isEqualTo(3000L);
    }
}
