package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.Wallet;
import com.tuning.tuningprototype.models.requests.CreateWalletRequest;
import com.tuning.tuningprototype.models.requests.UpdateWalletRequest;
import org.springframework.stereotype.Component;

@Component
public class WalletRequestMapper {

    /**
     * Builds a new Wallet from a create request.
     */
    public Wallet toEntity(CreateWalletRequest request, Long nowEpochSeconds) {
        return Wallet.builder()
                .experimentId(request.experimentId())
                .startingMoneyAmount(request.startingMoneyAmount())
                .openedTime(request.openedTime()) // starts equal to starting amount
                .currencyCode(request.currencyCode())
                .createdTime(nowEpochSeconds)  // server-controlled
                .modifiedTime(nowEpochSeconds) // server-controlled
                .build();
    }

    /**
     * Applies a partial update onto an already-loaded, managed Wallet entity.
     * Mutates in place so fields the client didn't touch are preserved untouched.
     */
    public void applyUpdate(UpdateWalletRequest request, Wallet existing, Long nowEpochSeconds) {
        if (request.startingMoneyAmount() != null) {
            existing.setStartingMoneyAmount(request.startingMoneyAmount());
        }
        if (request.currencyCode() != null) {
            existing.setCurrencyCode(request.currencyCode());
        }
        existing.setModifiedTime(nowEpochSeconds);
    }
}
