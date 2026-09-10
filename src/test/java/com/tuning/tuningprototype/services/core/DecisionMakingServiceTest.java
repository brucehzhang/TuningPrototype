package com.tuning.tuningprototype.services.core;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.entity.*;
import com.tuning.tuningprototype.models.enums.DecisionType;
import com.tuning.tuningprototype.models.mappers.data.entity.DecisionMapper;
import com.tuning.tuningprototype.models.mappers.request.AssetSaleRequestMapper;
import com.tuning.tuningprototype.models.mappers.request.DecisionRequestMapper;
import com.tuning.tuningprototype.models.mappers.request.PurchaseLotRequestMapper;
import com.tuning.tuningprototype.models.requests.CreateAssetSaleRequest;
import com.tuning.tuningprototype.models.requests.CreateDecisionRequest;
import com.tuning.tuningprototype.models.requests.CreatePurchaseLotRequest;
import com.tuning.tuningprototype.models.requests.MakeDecisionsRequest;
import com.tuning.tuningprototype.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecisionMakingServiceTest {

    @Mock
    private AssetSaleRepository assetSaleRepository;
    @Mock
    private AssetSaleRequestMapper assetSaleRequestMapper;
    @Mock
    private DecisionRepository decisionRepository;
    @Mock
    private DecisionMapper decisionMapper;
    @Mock
    private DecisionRequestMapper decisionRequestMapper;
    @Mock
    private PurchaseLotRepository purchaseLotRepository;
    @Mock
    private PurchaseLotRequestMapper purchaseLotRequestMapper;
    @Mock
    private SampleRepository sampleRepository;
    @Mock
    private WalletRepository walletRepository;

    private DecisionMakingService service;

    @BeforeEach
    void setUp() {
        service = new DecisionMakingService(assetSaleRepository, assetSaleRequestMapper, decisionRepository,
                decisionMapper, decisionRequestMapper, purchaseLotRepository, purchaseLotRequestMapper,
                sampleRepository, walletRepository);
    }

    private MakeDecisionsRequest.IndividualDecision buyDecision(long sampleId, CreatePurchaseLotRequest purchaseLotRequest) {
        return new MakeDecisionsRequest.IndividualDecision(
                new CreateDecisionRequest(sampleId, DecisionType.BUY, "AAPL", "reasoning", 100L),
                purchaseLotRequest, null);
    }

    private MakeDecisionsRequest.IndividualDecision sellDecision(long sampleId, List<CreateAssetSaleRequest> saleRequests) {
        return new MakeDecisionsRequest.IndividualDecision(
                new CreateDecisionRequest(sampleId, DecisionType.SELL, "AAPL", "reasoning", 100L),
                null, saleRequests);
    }

    private MakeDecisionsRequest.IndividualDecision holdDecision(long sampleId, String ticker) {
        return new MakeDecisionsRequest.IndividualDecision(
                new CreateDecisionRequest(sampleId, DecisionType.HOLD, ticker, "reasoning", 100L),
                null, null);
    }

    // --- sampleId mismatch ---

    @Test
    void makeDecisions_sampleIdMismatch_throwsUserExperimentException() {
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(buyDecision(999L, null)));

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());

        verifyNoInteractions(decisionRepository, purchaseLotRepository, assetSaleRepository);
    }

    // --- BUY ---

    @Test
    void makeDecisions_buy_success_savesPurchaseLot() {
        CreatePurchaseLotRequest purchaseLotRequest = new CreatePurchaseLotRequest(5L, "AAPL", BigDecimal.TEN, BigDecimal.ONE, 100L);
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(buyDecision(1L, purchaseLotRequest)));

        Wallet wallet = Wallet.builder().id(5L).startingMoneyAmount(BigDecimal.valueOf(1000)).build();
        when(walletRepository.findById(5L)).thenReturn(Optional.of(wallet));
        when(purchaseLotRepository.findByWalletId(5L)).thenReturn(List.of());

        Decision savedDecision = Decision.builder().id(50L).decisionType(DecisionType.BUY).build();
        when(decisionRequestMapper.toEntity(any(), anyLong())).thenReturn(Decision.builder().build());
        when(decisionRepository.save(any())).thenReturn(savedDecision);
        DecisionDto decisionDto = new DecisionDto(50L, 1L, DecisionType.BUY, "AAPL", "reasoning", 100L, 1L, 1L, null, null);
        when(decisionMapper.toDto(savedDecision)).thenReturn(decisionDto);

        List<DecisionDto> result = service.makeDecisions(request, 1L);

        assertThat(result).containsExactly(decisionDto);
        verify(purchaseLotRepository).save(any());
        verify(assetSaleRepository, never()).save(any());
    }

    @Test
    void makeDecisions_buy_nullPurchaseLotRequest_throwsIllegalArgumentException() {
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(buyDecision(1L, null)));

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(decisionRepository, walletRepository);
    }

    @Test
    void makeDecisions_buy_walletNotFound_throwsEntityNotFoundException() {
        CreatePurchaseLotRequest purchaseLotRequest = new CreatePurchaseLotRequest(5L, "AAPL", BigDecimal.TEN, BigDecimal.ONE, 100L);
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(buyDecision(1L, purchaseLotRequest)));
        when(walletRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(decisionRepository);
    }

    @Test
    void makeDecisions_buy_insufficientFunds_throwsUserExperimentException() {
        CreatePurchaseLotRequest purchaseLotRequest = new CreatePurchaseLotRequest(5L, "AAPL", BigDecimal.valueOf(100), BigDecimal.TEN, 100L);
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(buyDecision(1L, purchaseLotRequest)));

        Wallet wallet = Wallet.builder().id(5L).startingMoneyAmount(BigDecimal.valueOf(500)).build();
        when(walletRepository.findById(5L)).thenReturn(Optional.of(wallet));
        when(purchaseLotRepository.findByWalletId(5L)).thenReturn(List.of());

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());

        verifyNoInteractions(decisionRepository);
    }

    @Test
    void makeDecisions_buy_accountsForPriorPurchasesAndSales_whenComputingAvailableMoney() {
        CreatePurchaseLotRequest purchaseLotRequest = new CreatePurchaseLotRequest(5L, "AAPL", BigDecimal.valueOf(50), BigDecimal.ONE, 100L);
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(buyDecision(1L, purchaseLotRequest)));

        Wallet wallet = Wallet.builder().id(5L).startingMoneyAmount(BigDecimal.valueOf(100)).build();
        when(walletRepository.findById(5L)).thenReturn(Optional.of(wallet));
        PurchaseLot priorLot = PurchaseLot.builder().id(1L).purchasePrice(BigDecimal.valueOf(80)).purchaseQuantity(BigDecimal.ONE).build();
        when(purchaseLotRepository.findByWalletId(5L)).thenReturn(List.of(priorLot));
        AssetSale priorSale = AssetSale.builder().salePrice(BigDecimal.valueOf(60)).saleQuantity(BigDecimal.ONE).build();
        when(assetSaleRepository.findByPurchaseLotId(1L)).thenReturn(List.of(priorSale));
        // available = 100 - 80 + 60 = 80, purchase cost = 50, should succeed

        when(decisionRequestMapper.toEntity(any(), anyLong())).thenReturn(Decision.builder().build());
        Decision savedDecision = Decision.builder().id(50L).decisionType(DecisionType.BUY).build();
        when(decisionRepository.save(any())).thenReturn(savedDecision);
        DecisionDto decisionDto = new DecisionDto(50L, 1L, DecisionType.BUY, "AAPL", "reasoning", 100L, 1L, 1L, null, null);
        when(decisionMapper.toDto(savedDecision)).thenReturn(decisionDto);

        List<DecisionDto> result = service.makeDecisions(request, 1L);

        assertThat(result).containsExactly(decisionDto);
        verify(purchaseLotRepository).save(any());
    }

    // --- SELL ---

    @Test
    void makeDecisions_sell_success_savesAssetSales() {
        CreateAssetSaleRequest saleRequest = new CreateAssetSaleRequest(10L, "AAPL", BigDecimal.TEN, BigDecimal.ONE, 100L);
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(sellDecision(1L, List.of(saleRequest))));

        PurchaseLot lot = PurchaseLot.builder().id(10L).ticker("AAPL").purchaseQuantity(BigDecimal.TEN).build();
        when(purchaseLotRepository.findById(10L)).thenReturn(Optional.of(lot));
        when(assetSaleRepository.sumSaleQuantityByPurchaseLotId(10L)).thenReturn(BigDecimal.ZERO);

        when(decisionRequestMapper.toEntity(any(), anyLong())).thenReturn(Decision.builder().build());
        Decision savedDecision = Decision.builder().id(60L).decisionType(DecisionType.SELL).build();
        when(decisionRepository.save(any())).thenReturn(savedDecision);
        DecisionDto decisionDto = new DecisionDto(60L, 1L, DecisionType.SELL, "AAPL", "reasoning", 100L, 1L, 1L, null, null);
        when(decisionMapper.toDto(savedDecision)).thenReturn(decisionDto);

        List<DecisionDto> result = service.makeDecisions(request, 1L);

        assertThat(result).containsExactly(decisionDto);
        verify(assetSaleRepository).save(any());
        verify(purchaseLotRepository, never()).save(any());
    }

    @Test
    void makeDecisions_sell_nullAssetSaleRequests_throwsIllegalArgumentException() {
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(sellDecision(1L, null)));

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void makeDecisions_sell_emptyAssetSaleRequests_throwsIllegalArgumentException() {
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(sellDecision(1L, List.of())));

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void makeDecisions_sell_lotNotFound_throwsEntityNotFoundException() {
        CreateAssetSaleRequest saleRequest = new CreateAssetSaleRequest(10L, "AAPL", BigDecimal.TEN, BigDecimal.ONE, 100L);
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(sellDecision(1L, List.of(saleRequest))));
        when(purchaseLotRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void makeDecisions_sell_quantityExceedsRemaining_throwsUserExperimentException() {
        CreateAssetSaleRequest saleRequest = new CreateAssetSaleRequest(10L, "AAPL", BigDecimal.TEN, BigDecimal.valueOf(5), 100L);
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(sellDecision(1L, List.of(saleRequest))));

        PurchaseLot lot = PurchaseLot.builder().id(10L).ticker("AAPL").purchaseQuantity(BigDecimal.valueOf(3)).build();
        when(purchaseLotRepository.findById(10L)).thenReturn(Optional.of(lot));
        when(assetSaleRepository.sumSaleQuantityByPurchaseLotId(10L)).thenReturn(BigDecimal.ZERO);

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());
    }

    @Test
    void makeDecisions_sell_tickerMismatch_throwsIllegalArgumentException() {
        CreateAssetSaleRequest saleRequest = new CreateAssetSaleRequest(10L, "TSLA", BigDecimal.TEN, BigDecimal.ONE, 100L);
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(sellDecision(1L, List.of(saleRequest))));

        PurchaseLot lot = PurchaseLot.builder().id(10L).ticker("AAPL").purchaseQuantity(BigDecimal.TEN).build();
        when(purchaseLotRepository.findById(10L)).thenReturn(Optional.of(lot));
        when(assetSaleRepository.sumSaleQuantityByPurchaseLotId(10L)).thenReturn(BigDecimal.ZERO);

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void makeDecisions_sell_multipleSalesAgainstSameLot_accumulatesClaimedQuantity() {
        // Lot has 10 available; two sale requests of 6 each against the same lot in one batch should fail on the second.
        CreateAssetSaleRequest first = new CreateAssetSaleRequest(10L, "AAPL", BigDecimal.TEN, BigDecimal.valueOf(6), 100L);
        CreateAssetSaleRequest second = new CreateAssetSaleRequest(10L, "AAPL", BigDecimal.TEN, BigDecimal.valueOf(6), 100L);
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(sellDecision(1L, List.of(first, second))));

        PurchaseLot lot = PurchaseLot.builder().id(10L).ticker("AAPL").purchaseQuantity(BigDecimal.TEN).build();
        when(purchaseLotRepository.findById(10L)).thenReturn(Optional.of(lot));
        when(assetSaleRepository.sumSaleQuantityByPurchaseLotId(10L)).thenReturn(BigDecimal.ZERO);

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());
    }

    // --- HOLD ---

    @Test
    void makeDecisions_hold_success() {
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(holdDecision(1L, "AAPL")));

        Sample sample = Sample.builder().id(1L).experimentId(9L).build();
        when(sampleRepository.findById(1L)).thenReturn(Optional.of(sample));
        Wallet wallet = Wallet.builder().id(5L).build();
        when(walletRepository.findByExperimentId(9L)).thenReturn(List.of(wallet));
        PurchaseLot lot = PurchaseLot.builder().id(20L).ticker("AAPL").purchaseQuantity(BigDecimal.TEN).build();
        when(purchaseLotRepository.findByWalletIdInAndTicker(List.of(5L), "AAPL")).thenReturn(List.of(lot));
        when(assetSaleRepository.sumSaleQuantityByPurchaseLotId(20L)).thenReturn(BigDecimal.ONE);

        when(decisionRequestMapper.toEntity(any(), anyLong())).thenReturn(Decision.builder().build());
        Decision savedDecision = Decision.builder().id(70L).decisionType(DecisionType.HOLD).build();
        when(decisionRepository.save(any())).thenReturn(savedDecision);
        DecisionDto decisionDto = new DecisionDto(70L, 1L, DecisionType.HOLD, "AAPL", "reasoning", 100L, 1L, 1L, null, null);
        when(decisionMapper.toDto(savedDecision)).thenReturn(decisionDto);

        List<DecisionDto> result = service.makeDecisions(request, 1L);

        assertThat(result).containsExactly(decisionDto);
        verify(purchaseLotRepository, never()).save(any());
        verify(assetSaleRepository, never()).save(any());
    }

    @Test
    void makeDecisions_hold_sampleNotFound_throwsEntityNotFoundException() {
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(holdDecision(1L, "AAPL")));
        when(sampleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void makeDecisions_hold_noWallets_throwsUserExperimentException() {
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(holdDecision(1L, "AAPL")));
        Sample sample = Sample.builder().id(1L).experimentId(9L).build();
        when(sampleRepository.findById(1L)).thenReturn(Optional.of(sample));
        when(walletRepository.findByExperimentId(9L)).thenReturn(List.of());

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());
    }

    @Test
    void makeDecisions_hold_noOpenPosition_throwsUserExperimentException() {
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(holdDecision(1L, "AAPL")));
        Sample sample = Sample.builder().id(1L).experimentId(9L).build();
        when(sampleRepository.findById(1L)).thenReturn(Optional.of(sample));
        Wallet wallet = Wallet.builder().id(5L).build();
        when(walletRepository.findByExperimentId(9L)).thenReturn(List.of(wallet));
        PurchaseLot lot = PurchaseLot.builder().id(20L).ticker("AAPL").purchaseQuantity(BigDecimal.TEN).build();
        when(purchaseLotRepository.findByWalletIdInAndTicker(List.of(5L), "AAPL")).thenReturn(List.of(lot));
        when(assetSaleRepository.sumSaleQuantityByPurchaseLotId(20L)).thenReturn(BigDecimal.TEN);

        assertThatThrownBy(() -> service.makeDecisions(request, 1L))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());
    }

    // --- multiple decisions in one request ---

    @Test
    void makeDecisions_multipleIndividualDecisions_returnsInOrder() {
        MakeDecisionsRequest.IndividualDecision hold1 = holdDecision(1L, "AAPL");
        MakeDecisionsRequest.IndividualDecision hold2 = holdDecision(1L, "TSLA");
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of(hold1, hold2));

        Sample sample = Sample.builder().id(1L).experimentId(9L).build();
        when(sampleRepository.findById(1L)).thenReturn(Optional.of(sample));
        Wallet wallet = Wallet.builder().id(5L).build();
        when(walletRepository.findByExperimentId(9L)).thenReturn(List.of(wallet));

        PurchaseLot aaplLot = PurchaseLot.builder().id(20L).ticker("AAPL").purchaseQuantity(BigDecimal.TEN).build();
        when(purchaseLotRepository.findByWalletIdInAndTicker(List.of(5L), "AAPL")).thenReturn(List.of(aaplLot));
        when(assetSaleRepository.sumSaleQuantityByPurchaseLotId(20L)).thenReturn(BigDecimal.ZERO);

        PurchaseLot tslaLot = PurchaseLot.builder().id(21L).ticker("TSLA").purchaseQuantity(BigDecimal.ONE).build();
        when(purchaseLotRepository.findByWalletIdInAndTicker(List.of(5L), "TSLA")).thenReturn(List.of(tslaLot));
        when(assetSaleRepository.sumSaleQuantityByPurchaseLotId(21L)).thenReturn(BigDecimal.ZERO);

        when(decisionRequestMapper.toEntity(any(), anyLong())).thenReturn(Decision.builder().build());
        Decision savedAapl = Decision.builder().id(70L).decisionType(DecisionType.HOLD).build();
        Decision savedTsla = Decision.builder().id(71L).decisionType(DecisionType.HOLD).build();
        when(decisionRepository.save(any())).thenReturn(savedAapl, savedTsla);
        DecisionDto aaplDto = new DecisionDto(70L, 1L, DecisionType.HOLD, "AAPL", "reasoning", 100L, 1L, 1L, null, null);
        DecisionDto tslaDto = new DecisionDto(71L, 1L, DecisionType.HOLD, "TSLA", "reasoning", 100L, 1L, 1L, null, null);
        when(decisionMapper.toDto(savedAapl)).thenReturn(aaplDto);
        when(decisionMapper.toDto(savedTsla)).thenReturn(tslaDto);

        List<DecisionDto> result = service.makeDecisions(request, 1L);

        assertThat(result).containsExactly(aaplDto, tslaDto);
    }
}
