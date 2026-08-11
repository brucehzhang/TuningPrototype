package com.tuning.tuningprototype.models.requests;
import java.math.BigDecimal;

// Request dto for updating an existing wallet. All fields nullable/optional —
// PATCH semantics, only non-null fields are applied.
//
// startingMoneyAmount and currency code, are only editable while the parent
// experiment is in DRAFT status.
// The DRAFT-status check is enforced in the service layer, not here — this DTO
// doesn't know about Experiment at all.
public record UpdateWalletRequest(
        Long id,
        BigDecimal startingMoneyAmount,
        String currencyCode) {}
