package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.AssetSale;
import com.tuning.tuningprototype.models.requests.CreateAssetSaleRequest;
import org.springframework.stereotype.Component;

@Component
public class AssetSaleRequestMapper {

    /**
     * saleDecisionId comes from the Decision just persisted in the same orchestrated call, never from the request itself.
     */
    public AssetSale toEntity(CreateAssetSaleRequest request, Long saleDecisionId, Long nowEpochSeconds) {
        return AssetSale.builder()
                .saleDecisionId(saleDecisionId)
                .purchaseLotId(request.purchaseLotId())
                .ticker(request.ticker())
                .salePrice(request.salePrice())
                .saleQuantity(request.saleQuantity())
                .saleTime(request.saleTime())
                .createdTime(nowEpochSeconds)
                .modifiedTime(nowEpochSeconds)
                .build();
    }
}