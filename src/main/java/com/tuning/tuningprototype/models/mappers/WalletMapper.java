package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Experiment;
import com.tuning.tuningprototype.models.Wallet;
import com.tuning.tuningprototype.models.WalletDto;

public interface WalletMapper {
    WalletDto toDto(Wallet wallet);
    Wallet toEntity(WalletDto dto, Experiment experimentReference);
}