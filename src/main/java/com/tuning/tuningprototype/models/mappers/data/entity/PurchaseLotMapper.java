package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.PurchaseLot;
import com.tuning.tuningprototype.models.db.entity.PurchaseLotDto;

public interface PurchaseLotMapper {
    PurchaseLotDto toDto(PurchaseLot purchaseLot);
    PurchaseLot toEntity(PurchaseLotDto dto);
}