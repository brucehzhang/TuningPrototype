package com.tuning.tuningprototype.models.requests;
import java.math.BigDecimal;

// Request dto for updating an existing wallet. All fields nullable/optional —
// PATCH semantics, only non-null fields are applied.
//
// startingMoneyAmount and currency code are only editable while the parent
// experiment is in DRAFT status — once an experiment starts, its wallet becomes
// a pure ledger and these should only change as a side effect of PurchaseLot/
// AssetSale creation (debit on buy, credit on sell), never via direct client edit.
// The DRAFT-status check is enforced in the service layer, not here — this DTO
// doesn't know about Experiment at all.
public record UpdateWalletRequest(
        Long id,
        BigDecimal startingMoneyAmount,
        String currencyCode) {}
