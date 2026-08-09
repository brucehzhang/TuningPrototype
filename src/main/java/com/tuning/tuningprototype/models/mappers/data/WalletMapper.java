package com.tuning.tuningprototype.models.mappers.data;

import com.tuning.tuningprototype.models.db.Wallet;
import com.tuning.tuningprototype.models.db.WalletDto;

public interface WalletMapper {
    WalletDto toDto(Wallet wallet);
    Wallet toEntity(WalletDto dto);
}