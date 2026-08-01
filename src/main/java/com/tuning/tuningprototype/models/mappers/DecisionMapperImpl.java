package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Decision;
import com.tuning.tuningprototype.models.DecisionDto;
import com.tuning.tuningprototype.models.Sample;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DecisionMapperImpl implements DecisionMapper {

    @Lazy
    private final SampleMapper sampleMapper;
    @Lazy
    private final PurchaseLotMapper purchaseLotMapper;
    @Lazy
    private final AssetSaleMapper assetSaleMapper;

    @Override
    public DecisionDto toDto(Decision decision) {
        if (decision == null) return null;

        Sample sample = decision.getSample();

        return new DecisionDto(
                decision.getId(),
                sample != null ? sample.getId() : null,
                Hibernate.isInitialized(sample) ? sampleMapper.toDto(sample) : null,
                decision.getDecisionType(),
                decision.getTicker(),
                decision.getReasoning(),
                decision.getDecisionTime(),
                decision.getCreatedTime(),
                decision.getModifiedTime(),
                Hibernate.isInitialized(decision.getPurchaseLots())
                        ? decision.getPurchaseLots().stream().map(purchaseLotMapper::toDto).toList() : null,
                Hibernate.isInitialized(decision.getAssetSales())
                        ? decision.getAssetSales().stream().map(assetSaleMapper::toDto).toList() : null
        );
    }

    @Override
    public Decision toEntity(DecisionDto dto, Sample sampleReference) {
        if (dto == null) return null;

        return Decision.builder()
                .id(dto.id())
                .sample(sampleReference)
                .decisionType(dto.decisionType())
                .ticker(dto.ticker())
                .reasoning(dto.reasoning())
                .decisionTime(dto.decisionTime())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}