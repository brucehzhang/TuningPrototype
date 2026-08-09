package com.tuning.tuningprototype.models.mappers.data;

import com.tuning.tuningprototype.models.db.Decision;
import com.tuning.tuningprototype.models.db.DecisionDto;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class DecisionMapperImpl implements DecisionMapper {

    private final PurchaseLotMapper _purchaseLotMapper;
    private final AssetSaleMapper _assetSaleMapper;

    public DecisionMapperImpl(@Lazy PurchaseLotMapper purchaseLotMapper, @Lazy AssetSaleMapper assetSaleMapper) {
        _purchaseLotMapper = purchaseLotMapper;
        _assetSaleMapper = assetSaleMapper;
    }

    @Override
    public DecisionDto toDto(Decision decision) {
        if (decision == null) return null;

        return new DecisionDto(
                decision.getId(),
                decision.getSampleId(),
                decision.getDecisionType(),
                decision.getTicker(),
                decision.getReasoning(),
                decision.getDecisionTime(),
                decision.getCreatedTime(),
                decision.getModifiedTime(),
                Hibernate.isInitialized(decision.getPurchaseLots())
                        ? decision.getPurchaseLots().stream().map(_purchaseLotMapper::toDto).toList() : null,
                Hibernate.isInitialized(decision.getAssetSales())
                        ? decision.getAssetSales().stream().map(_assetSaleMapper::toDto).toList() : null
        );
    }

    @Override
    public Decision toEntity(DecisionDto dto) {
        if (dto == null) return null;

        return Decision.builder()
                .id(dto.id())
                .sampleId(dto.sampleId())
                .decisionType(dto.decisionType())
                .ticker(dto.ticker())
                .reasoning(dto.reasoning())
                .decisionTime(dto.decisionTime())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}