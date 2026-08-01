package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Decision;
import com.tuning.tuningprototype.models.PurchaseLot;
import com.tuning.tuningprototype.models.PurchaseLotDto;
import com.tuning.tuningprototype.models.Wallet;

public interface PurchaseLotMapper {
    PurchaseLotDto toDto(PurchaseLot purchaseLot);
    PurchaseLot toEntity(PurchaseLotDto dto, Decision decisionReference, Wallet walletReference);
}