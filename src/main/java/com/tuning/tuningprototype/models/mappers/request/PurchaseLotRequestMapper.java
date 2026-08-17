package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.PurchaseLot;
import com.tuning.tuningprototype.models.requests.CreatePurchaseLotRequest;
import org.springframework.stereotype.Component;

@Component
public class PurchaseLotRequestMapper {

    /**
     * purchaseDecisionId comes from the Decision just persisted in the same orchestrated call, never from the request itself.
     */
    public PurchaseLot toEntity(CreatePurchaseLotRequest request, Long purchaseDecisionId, Long nowEpochSeconds) {
        return PurchaseLot.builder()
                .purchaseDecisionId(purchaseDecisionId)
                .walletId(request.walletId())
                .ticker(request.ticker())
                .purchasePrice(request.purchasePrice())
                .purchaseQuantity(request.purchaseQuantity())
                .purchaseTime(request.purchaseTime())
                .createdTime(nowEpochSeconds)
                .modifiedTime(nowEpochSeconds)
                .build();
    }
}