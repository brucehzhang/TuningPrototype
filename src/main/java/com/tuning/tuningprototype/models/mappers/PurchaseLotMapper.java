package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.db.PurchaseLot;
import com.tuning.tuningprototype.models.db.PurchaseLotDto;

public interface PurchaseLotMapper {
    PurchaseLotDto toDto(PurchaseLot purchaseLot);
    PurchaseLot toEntity(PurchaseLotDto dto);
}