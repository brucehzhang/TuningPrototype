package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.PurchaseLot;
import com.tuning.tuningprototype.models.db.entity.PurchaseLotDto;
import com.tuning.tuningprototype.models.db.entity.Wallet;
import com.tuning.tuningprototype.models.db.entity.WalletDto;
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
class WalletMapperTest {

    @Mock
    private PurchaseLotMapper purchaseLotMapper;

    private WalletMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new WalletMapper(purchaseLotMapper);
    }

    @Test
    void toDto_returnsNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_mapsScalarFields_andPurchaseLots_whenInitialized() {
        PurchaseLot purchaseLot = PurchaseLot.builder().id(7L).build();
        PurchaseLotDto purchaseLotDto = new PurchaseLotDto(7L, 1L, 1L, "AAPL", null, null, null, null, null, null);
        when(purchaseLotMapper.toDto(purchaseLot)).thenReturn(purchaseLotDto);

        List<PurchaseLot> purchaseLots = new ArrayList<>();
        purchaseLots.add(purchaseLot);

        Wallet entity = Wallet.builder()
                .id(1L)
                .experimentId(2L)
                .startingMoneyAmount(new BigDecimal("1000.00"))
                .openedTime(1000L)
                .currencyCode("USD")
                .createdTime(2000L)
                .modifiedTime(3000L)
                .purchaseLots(purchaseLots)
                .build();

        WalletDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.experimentId()).isEqualTo(2L);
        assertThat(dto.startingMoneyAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(dto.openedTime()).isEqualTo(1000L);
        assertThat(dto.currencyCode()).isEqualTo("USD");
        assertThat(dto.createdTime()).isEqualTo(2000L);
        assertThat(dto.modifiedTime()).isEqualTo(3000L);
        assertThat(dto.purchaseLots()).containsExactly(purchaseLotDto);
    }

    @Test
    void toDto_purchaseLotsIsNull_whenCollectionUninitialized() {
        Wallet entity = Wallet.builder()
                .id(1L)
                .purchaseLots(new UninitializedList<>())
                .build();

        WalletDto dto = mapper.toDto(entity);

        assertThat(dto.purchaseLots()).isNull();
        verifyNoInteractions(purchaseLotMapper);
    }

    @Test
    void toEntity_returnsNull_whenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        WalletDto dto = new WalletDto(
                1L, 2L, new BigDecimal("1000.00"), 1000L, "USD", 2000L, 3000L, List.of());

        Wallet entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getExperimentId()).isEqualTo(2L);
        assertThat(entity.getStartingMoneyAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(entity.getOpenedTime()).isEqualTo(1000L);
        assertThat(entity.getCurrencyCode()).isEqualTo("USD");
        assertThat(entity.getCreatedTime()).isEqualTo(2000L);
        assertThat(entity.getModifiedTime()).isEqualTo(3000L);
    }
}
