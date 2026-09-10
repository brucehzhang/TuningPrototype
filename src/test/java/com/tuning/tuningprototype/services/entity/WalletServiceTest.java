package com.tuning.tuningprototype.services.entity;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.entity.*;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.mappers.data.entity.WalletMapper;
import com.tuning.tuningprototype.models.mappers.request.WalletRequestMapper;
import com.tuning.tuningprototype.models.requests.CreateWalletRequest;
import com.tuning.tuningprototype.models.requests.UpdateWalletRequest;
import com.tuning.tuningprototype.repositories.AssetSaleRepository;
import com.tuning.tuningprototype.repositories.PurchaseLotRepository;
import com.tuning.tuningprototype.repositories.WalletRepository;
import com.tuning.tuningprototype.services.core.ExperimentStateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WalletMapper walletMapper;
    @Mock
    private WalletRequestMapper walletRequestMapper;
    @Mock
    private ExperimentStateValidator experimentStateValidator;
    @Mock
    private PurchaseLotRepository purchaseLotRepository;
    @Mock
    private AssetSaleRepository assetSaleRepository;

    private WalletService service;

    @BeforeEach
    void setUp() {
        service = new WalletService(walletRepository, walletMapper, walletRequestMapper, experimentStateValidator,
                purchaseLotRepository, assetSaleRepository);
    }

    // --- getWalletsByExperiment: checkTime == null ---

    @Test
    void getWalletsByExperiment_nullCheckTime_found_returnsHydratedDto() {
        Wallet wallet = Wallet.builder().id(1L).experimentId(9L).purchaseLots(List.of()).build();
        WalletDto dto = mock(WalletDto.class);
        when(walletRepository.findWithPurchaseLotsByExperimentId(9L)).thenReturn(List.of(wallet));
        when(walletMapper.toDto(wallet)).thenReturn(dto);

        List<WalletDto> result = service.getWalletsByExperiment(9L, null);

        assertThat(result).containsExactly(dto);
        verify(purchaseLotRepository, never()).findByWalletIdInAndPurchaseTimeIsLessThanEqual(any(), any());
    }

    @Test
    void getWalletsByExperiment_nullCheckTime_notFound_returnsEmptyList() {
        when(walletRepository.findWithPurchaseLotsByExperimentId(9L)).thenReturn(List.of());

        List<WalletDto> result = service.getWalletsByExperiment(9L, null);

        assertThat(result).isEmpty();
        verifyNoInteractions(walletMapper);
    }

    @Test
    void getWalletsByExperiment_nullCheckTime_multipleWallets_returnsAllHydratedDtos() {
        Wallet usdWallet = Wallet.builder().id(1L).experimentId(9L).currencyCode("USD").purchaseLots(List.of()).build();
        Wallet eurWallet = Wallet.builder().id(2L).experimentId(9L).currencyCode("EUR").purchaseLots(List.of()).build();
        WalletDto usdDto = mock(WalletDto.class);
        WalletDto eurDto = mock(WalletDto.class);
        when(walletRepository.findWithPurchaseLotsByExperimentId(9L)).thenReturn(List.of(usdWallet, eurWallet));
        when(walletMapper.toDto(usdWallet)).thenReturn(usdDto);
        when(walletMapper.toDto(eurWallet)).thenReturn(eurDto);

        List<WalletDto> result = service.getWalletsByExperiment(9L, null);

        assertThat(result).containsExactly(usdDto, eurDto);
    }

    // --- getWalletsByExperiment: checkTime != null ---

    @Test
    void getWalletsByExperiment_withCheckTime_noWallets_returnsEmptyListWithoutFurtherLookups() {
        when(walletRepository.findByExperimentIdAndOpenedTimeIsLessThanEqual(9L, 500L)).thenReturn(List.of());

        List<WalletDto> result = service.getWalletsByExperiment(9L, 500L);

        assertThat(result).isEmpty();
        verifyNoInteractions(purchaseLotRepository, assetSaleRepository, walletMapper);
    }

    @Test
    void getWalletsByExperiment_withCheckTime_noPurchaseLots_skipsAssetSaleLookup() {
        Wallet wallet = Wallet.builder().id(1L).experimentId(9L).build();
        when(walletRepository.findByExperimentIdAndOpenedTimeIsLessThanEqual(9L, 500L)).thenReturn(List.of(wallet));
        when(purchaseLotRepository.findByWalletIdInAndPurchaseTimeIsLessThanEqual(List.of(1L), 500L)).thenReturn(List.of());
        WalletDto dto = mock(WalletDto.class);
        when(walletMapper.toDto(wallet)).thenReturn(dto);

        List<WalletDto> result = service.getWalletsByExperiment(9L, 500L);

        assertThat(result).containsExactly(dto);
        assertThat(wallet.getPurchaseLots()).isEmpty();
        verifyNoInteractions(assetSaleRepository);
    }

    @Test
    void getWalletsByExperiment_withCheckTime_nestsLotsAndSalesByParent() {
        Wallet walletA = Wallet.builder().id(1L).experimentId(9L).build();
        Wallet walletB = Wallet.builder().id(2L).experimentId(9L).build();
        when(walletRepository.findByExperimentIdAndOpenedTimeIsLessThanEqual(9L, 500L))
                .thenReturn(List.of(walletA, walletB));

        PurchaseLot lotA1 = PurchaseLot.builder().id(10L).walletId(1L).ticker("AAPL").build();
        PurchaseLot lotB1 = PurchaseLot.builder().id(20L).walletId(2L).ticker("TSLA").build();
        when(purchaseLotRepository.findByWalletIdInAndPurchaseTimeIsLessThanEqual(List.of(1L, 2L), 500L))
                .thenReturn(List.of(lotA1, lotB1));

        AssetSale saleForLotA1 = AssetSale.builder().id(100L).purchaseLotId(10L).build();
        when(assetSaleRepository.findByPurchaseLotIdInAndSaleTimeIsLessThanEqual(List.of(10L, 20L), 500L))
                .thenReturn(List.of(saleForLotA1));

        when(walletMapper.toDto(any(Wallet.class))).thenReturn(mock(WalletDto.class));

        service.getWalletsByExperiment(9L, 500L);

        assertThat(walletA.getPurchaseLots()).containsExactly(lotA1);
        assertThat(walletB.getPurchaseLots()).containsExactly(lotB1);
        assertThat(lotA1.getAssetSales()).containsExactly(saleForLotA1);
        assertThat(lotB1.getAssetSales()).isEmpty();
    }

    // --- createDefaultWallet ---

    @Test
    void createDefaultWallet_noExistingWallets_createsWithDefaults() {
        when(walletRepository.findByExperimentId(9L)).thenReturn(List.of());

        service.createDefaultWallet(9L, 500L);

        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(captor.capture());
        Wallet saved = captor.getValue();
        assertThat(saved.getExperimentId()).isEqualTo(9L);
        assertThat(saved.getStartingMoneyAmount()).isEqualByComparingTo(BigDecimal.valueOf(100000));
        assertThat(saved.getOpenedTime()).isEqualTo(500L);
        assertThat(saved.getCurrencyCode()).isEqualTo("USD");
    }

    @Test
    void createDefaultWallet_existingWallets_doesNothing() {
        Wallet existing = Wallet.builder().id(1L).experimentId(9L).build();
        when(walletRepository.findByExperimentId(9L)).thenReturn(List.of(existing));

        service.createDefaultWallet(9L, 500L);

        verify(walletRepository, never()).save(any());
    }

    // --- createWallet ---

    @Test
    void createWallet_experimentInDraft_overridesOpenedTimeWithExperimentStart() {
        CreateWalletRequest request = new CreateWalletRequest(9L, BigDecimal.TEN, 999L, "USD");
        ExperimentDto experimentDto = new ExperimentDto(9L, "name", null, "prompt", null, 700L, 800L,
                ExperimentStatus.DRAFT, 1L, 1L, 1L, List.of(), List.of());
        Wallet mappedWallet = Wallet.builder().experimentId(9L).openedTime(999L).build();
        when(walletRequestMapper.toEntity(eq(request), anyLong())).thenReturn(mappedWallet);
        Wallet savedWallet = Wallet.builder().id(5L).experimentId(9L).openedTime(700L).build();
        when(walletRepository.save(mappedWallet)).thenReturn(savedWallet);
        WalletDto dto = mock(WalletDto.class);
        when(walletMapper.toDto(savedWallet)).thenReturn(dto);

        WalletDto result = service.createWallet(request, experimentDto);

        assertThat(result).isEqualTo(dto);
        assertThat(mappedWallet.getOpenedTime()).isEqualTo(700L);
    }

    @Test
    void createWallet_experimentNotInDraft_keepsRequestedOpenedTime() {
        CreateWalletRequest request = new CreateWalletRequest(9L, BigDecimal.TEN, 999L, "USD");
        ExperimentDto experimentDto = new ExperimentDto(9L, "name", null, "prompt", null, 700L, 800L,
                ExperimentStatus.IN_PROGRESS, 1L, 1L, 1L, List.of(), List.of());
        Wallet mappedWallet = Wallet.builder().experimentId(9L).openedTime(999L).build();
        when(walletRequestMapper.toEntity(eq(request), anyLong())).thenReturn(mappedWallet);
        when(walletRepository.save(mappedWallet)).thenReturn(mappedWallet);
        when(walletMapper.toDto(mappedWallet)).thenReturn(mock(WalletDto.class));

        service.createWallet(request, experimentDto);

        assertThat(mappedWallet.getOpenedTime()).isEqualTo(999L);
    }

    // --- updateStartingWallet ---

    @Test
    void updateStartingWallet_draftState_updatesAndSaves() {
        UpdateWalletRequest request = new UpdateWalletRequest(5L, BigDecimal.valueOf(200), "EUR");
        Wallet existing = Wallet.builder().id(5L).experimentId(9L).build();
        when(walletRepository.getReferenceById(5L)).thenReturn(existing);
        when(experimentStateValidator.getStatus(9L)).thenReturn(ExperimentStatus.DRAFT);
        Wallet saved = Wallet.builder().id(5L).experimentId(9L).currencyCode("EUR").build();
        when(walletRepository.save(existing)).thenReturn(saved);
        WalletDto dto = mock(WalletDto.class);
        when(walletMapper.toDto(saved)).thenReturn(dto);

        WalletDto result = service.updateStartingWallet(request, 9L);

        assertThat(result).isEqualTo(dto);
        verify(walletRequestMapper).applyUpdate(eq(request), eq(existing), anyLong());
    }

    @Test
    void updateStartingWallet_experimentIdMismatch_throwsUserExperimentException() {
        UpdateWalletRequest request = new UpdateWalletRequest(5L, BigDecimal.valueOf(200), "EUR");
        Wallet existing = Wallet.builder().id(5L).experimentId(9L).build();
        when(walletRepository.getReferenceById(5L)).thenReturn(existing);

        assertThatThrownBy(() -> service.updateStartingWallet(request, 1L))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());

        verifyNoInteractions(experimentStateValidator, walletRequestMapper);
        verify(walletRepository, never()).save(any());
    }

    @Test
    void updateStartingWallet_experimentNotDraft_throwsUserExperimentException() {
        UpdateWalletRequest request = new UpdateWalletRequest(5L, BigDecimal.valueOf(200), "EUR");
        Wallet existing = Wallet.builder().id(5L).experimentId(9L).build();
        when(walletRepository.getReferenceById(5L)).thenReturn(existing);
        when(experimentStateValidator.getStatus(9L)).thenReturn(ExperimentStatus.IN_PROGRESS);

        assertThatThrownBy(() -> service.updateStartingWallet(request, 9L))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());

        verifyNoInteractions(walletRequestMapper);
        verify(walletRepository, never()).save(any());
    }

    // --- updateStartingWalletOpenDates ---

    @Test
    void updateStartingWalletOpenDates_walletsPresent_updatesAndSavesAll() {
        Wallet wallet1 = Wallet.builder().id(1L).experimentId(9L).openedTime(100L).build();
        Wallet wallet2 = Wallet.builder().id(2L).experimentId(9L).openedTime(100L).build();
        when(walletRepository.findByExperimentId(9L)).thenReturn(List.of(wallet1, wallet2));

        service.updateStartingWalletOpenDates(9L, 700L);

        assertThat(wallet1.getOpenedTime()).isEqualTo(700L);
        assertThat(wallet2.getOpenedTime()).isEqualTo(700L);
        verify(walletRepository).saveAll(List.of(wallet1, wallet2));
    }

    @Test
    void updateStartingWalletOpenDates_nullWalletsList_doesNotSave() {
        when(walletRepository.findByExperimentId(9L)).thenReturn(null);

        service.updateStartingWalletOpenDates(9L, 700L);

        verify(walletRepository, never()).saveAll(any());
    }

    @Test
    void updateStartingWalletOpenDates_emptyWalletsList_savesEmptyList() {
        when(walletRepository.findByExperimentId(9L)).thenReturn(List.of());

        service.updateStartingWalletOpenDates(9L, 700L);

        verify(walletRepository).saveAll(List.of());
    }
}
