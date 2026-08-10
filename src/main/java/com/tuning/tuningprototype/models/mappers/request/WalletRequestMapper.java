package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.Wallet;
import com.tuning.tuningprototype.models.requests.CreateWalletRequest;
import com.tuning.tuningprototype.models.requests.UpdateWalletRequest;

public interface WalletRequestMapper {

    Wallet toEntity(CreateWalletRequest request, Long nowEpochSeconds);
    void applyUpdate(UpdateWalletRequest request, Wallet existing, Long nowEpochSeconds);
}
