package com.tuning.tuningprototype.services.core;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.entity.*;
import com.tuning.tuningprototype.models.mappers.data.entity.DecisionMapper;
import com.tuning.tuningprototype.models.mappers.request.AssetSaleRequestMapper;
import com.tuning.tuningprototype.models.mappers.request.DecisionRequestMapper;
import com.tuning.tuningprototype.models.mappers.request.PurchaseLotRequestMapper;
import com.tuning.tuningprototype.models.requests.CreateAssetSaleRequest;
import com.tuning.tuningprototype.models.requests.MakeDecisionsRequest;
import com.tuning.tuningprototype.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DecisionMakingService {

    private final AssetSaleRepository _assetSaleRepository;
    private final AssetSaleRequestMapper _assetSaleRequestMapper;
    private final DecisionRepository _decisionRepository;
    private final DecisionMapper _decisionMapper;
    private final DecisionRequestMapper _decisionRequestMapper;
    private final PurchaseLotRepository _purchaseLotRepository;
    private final PurchaseLotRequestMapper _purchaseLotRequestMapper;
    private final SampleRepository _sampleRepository;
    private final WalletRepository _walletRepository;

    public DecisionMakingService(AssetSaleRepository assetSaleRepository, AssetSaleRequestMapper assetSaleRequestMapper, DecisionRepository decisionRepository, DecisionMapper decisionMapper, DecisionRequestMapper decisionRequestMapper, PurchaseLotRepository purchaseLotRepository, PurchaseLotRequestMapper purchaseLotRequestMapper, SampleRepository sampleRepository, WalletRepository walletRepository) {
        _assetSaleRepository = assetSaleRepository;
        _assetSaleRequestMapper = assetSaleRequestMapper;
        _decisionRepository = decisionRepository;
        _decisionMapper = decisionMapper;
        _decisionRequestMapper = decisionRequestMapper;
        _purchaseLotRepository = purchaseLotRepository;
        _purchaseLotRequestMapper = purchaseLotRequestMapper;
        _sampleRepository = sampleRepository;
        _walletRepository = walletRepository;
    }


    /**
     * Creates Decisions and the respective purchase lots and asset sales depending on decision type.
     *
     * @param makeDecisionsRequest Request DTO containing request to make decisions
     * @param sampleId Sample Id used to validate that the provided decisions belong to the same sample.
     * @return List of DTOs for the decisions.
     */
    @Transactional
    public List<DecisionDto> makeDecisions(MakeDecisionsRequest makeDecisionsRequest, long sampleId) {
        long now = Instant.now().getEpochSecond();
        return makeDecisionsRequest.individualDecisions().stream()
                .map(individualDecision -> makeDecision(individualDecision, sampleId, now))
                .toList();
    }

    // Saving a singular decision and associated purchases and sales
    private DecisionDto makeDecision(MakeDecisionsRequest.IndividualDecision individualDecision, long sampleId, long now) {
        // First validate that the decision is valid
        if (individualDecision.decisionRequest().sampleId() != sampleId) {
            throw new ExperimentException("Attempted to make a decision with sampleId %s that does not match the sampleId %s"
                    .formatted(individualDecision.decisionRequest().sampleId(), sampleId), true);
        }
        // Validate depending on if we are selling, holding, or buying.
        switch (individualDecision.decisionRequest().decisionType()) {
            case SELL -> {
                if (individualDecision.assetSaleRequests() == null || individualDecision.assetSaleRequests().isEmpty()) {
                    throw new IllegalArgumentException("SELL decision requires at least one assetSales entry");
                }
                assertSellable(individualDecision.assetSaleRequests());
            }
            case HOLD -> {
                    assertHoldable(individualDecision.decisionRequest().sampleId(), individualDecision.decisionRequest().ticker());
            }
            case BUY -> {
                // TODO:: Validate that we are not spending more money that possible
            }
        }

        Decision savedDecision = _decisionRepository.save(
                _decisionRequestMapper.toEntity(individualDecision.decisionRequest(), now));

        switch (savedDecision.getDecisionType()) {
            case BUY -> {
                if (individualDecision.purchaseLotRequest() == null) {
                    throw new IllegalArgumentException("BUY decision requires a purchaseLot request.");
                }
                _purchaseLotRepository.save(
                        _purchaseLotRequestMapper.toEntity(individualDecision.purchaseLotRequest(), savedDecision.getId(), now));
            }
            case SELL -> {
                if (individualDecision.assetSaleRequests().isEmpty()) {
                    throw new IllegalArgumentException("SELL decision requires at least one assetSales entry");
                }
                individualDecision.assetSaleRequests().forEach(saleRequest ->
                        _assetSaleRepository.save(
                                _assetSaleRequestMapper.toEntity(saleRequest, savedDecision.getId(), now)));
            }
            case HOLD -> {
                // no-op
            }
        }

        return _decisionMapper.toDto(savedDecision);
    }

    // Checks to make sure that the assets being sold does not exceed the amount of assets that the purchase lot has
    private void assertSellable(List<CreateAssetSaleRequest> assetSaleRequests) {
        // Tracks cumulative quantity claimed against each lot as we walk the batch,
        // so a second request against the same lot sees the first request's claim.
        Map<Long, BigDecimal> claimedInBatch = new HashMap<>();

        for (CreateAssetSaleRequest request : assetSaleRequests) {
            Long lotId = request.purchaseLotId();

            PurchaseLot lot = _purchaseLotRepository.findById(lotId)
                    .orElseThrow(() -> new EntityNotFoundException("PurchaseLot not found: " + lotId));

            BigDecimal alreadySold = _assetSaleRepository.sumSaleQuantityByPurchaseLotId(lotId);
            BigDecimal alreadyClaimedThisBatch = claimedInBatch.getOrDefault(lotId, BigDecimal.ZERO);

            BigDecimal remaining = lot.getPurchaseQuantity()
                    .subtract(alreadySold)
                    .subtract(alreadyClaimedThisBatch);

            if (request.saleQuantity().compareTo(remaining) > 0) {
                throw new ExperimentException(
                        "Cannot sell %s of lot %d (ticker %s) — only %s remaining (purchased %s, already sold %s%s)"
                                .formatted(
                                        request.saleQuantity(), lotId, lot.getTicker(),
                                        remaining, lot.getPurchaseQuantity(), alreadySold,
                                        alreadyClaimedThisBatch.compareTo(BigDecimal.ZERO) > 0
                                                ? ", claimed earlier in this request: " + alreadyClaimedThisBatch
                                                : ""), true);
            }

            // The sale's ticker should match the lot's ticker, since purchaseLotId is client-supplied and could reference the wrong lot.
            if (!lot.getTicker().equals(request.ticker())) {
                throw new IllegalArgumentException(
                        "Ticker mismatch: sale request ticker '%s' does not match purchase lot %d ticker '%s'"
                                .formatted(request.ticker(), lotId, lot.getTicker()));
            }

            claimedInBatch.put(lotId, alreadyClaimedThisBatch.add(request.saleQuantity()));
        }
    }

    // Checks to make sure the ticker is owned before attempting to issue a HOLD decision.
    private void assertHoldable(Long sampleId, String ticker) {
        Sample sample = _sampleRepository.findById(sampleId)
                .orElseThrow(() -> new EntityNotFoundException("Sample not found: " + sampleId));

        List<Long> walletIds = _walletRepository.findByExperimentId(sample.getExperimentId())
                .stream().map(Wallet::getId).toList();

        if (walletIds.isEmpty()) {
            throw new ExperimentException(
                    "Cannot HOLD %s — experiment %d has no wallets".formatted(ticker, sample.getExperimentId()), true);
        }

        List<PurchaseLot> lots = _purchaseLotRepository.findByWalletIdInAndTicker(walletIds, ticker);

        BigDecimal totalRemaining = lots.stream()
                .map(lot -> {
                    BigDecimal sold = _assetSaleRepository.sumSaleQuantityByPurchaseLotId(lot.getId());
                    return lot.getPurchaseQuantity().subtract(sold);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalRemaining.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ExperimentException(
                    "Cannot HOLD %s — no open position exists for this ticker (remaining quantity: %s)"
                            .formatted(ticker, totalRemaining), true);
        }
    }
}
