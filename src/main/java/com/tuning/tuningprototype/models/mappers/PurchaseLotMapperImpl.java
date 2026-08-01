package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Decision;
import com.tuning.tuningprototype.models.PurchaseLot;
import com.tuning.tuningprototype.models.PurchaseLotDto;
import com.tuning.tuningprototype.models.Wallet;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseLotMapperImpl implements PurchaseLotMapper {

    @Lazy
    private final DecisionMapper decisionMapper;
    @Lazy
    private final WalletMapper walletMapper;
    @Lazy
    private final AssetSaleMapper assetSaleMapper;

    @Override
    public PurchaseLotDto toDto(PurchaseLot purchaseLot) {
        if (purchaseLot == null) return null;

        Decision purchaseDecision = purchaseLot.getPurchaseDecision();
        Wallet wallet = purchaseLot.getWallet();

        return new PurchaseLotDto(
                purchaseLot.getId(),
                purchaseDecision != null ? purchaseDecision.getId() : null,
                Hibernate.isInitialized(purchaseDecision) ? decisionMapper.toDto(purchaseDecision) : null,
                wallet != null ? wallet.getId() : null,
                Hibernate.isInitialized(wallet) ? walletMapper.toDto(wallet) : null,
                purchaseLot.getTicker(),
                purchaseLot.getPurchasePrice(),
                purchaseLot.getPurchaseAmount(),
                purchaseLot.getCreatedTime(),
                purchaseLot.getModifiedTime(),
                Hibernate.isInitialized(purchaseLot.getAssetSales())
                        ? purchaseLot.getAssetSales().stream().map(assetSaleMapper::toDto).toList() : null
        );
    }

    @Override
    public PurchaseLot toEntity(PurchaseLotDto dto, Decision decisionReference, Wallet walletReference) {
        if (dto == null) return null;

        return PurchaseLot.builder()
                .id(dto.id())
                .purchaseDecision(decisionReference)
                .wallet(walletReference)
                .ticker(dto.ticker())
                .purchasePrice(dto.purchasePrice())
                .purchaseAmount(dto.purchaseAmount())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}