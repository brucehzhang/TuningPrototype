package com.tuning.tuningprototype.models.requests;

import java.util.List;

// Request for the agent's "create decision + resulting trades" tool call.
// Exactly one of purchaseLot / assetSales should be populated depending on
// decisionType (BUY -> purchaseLot, SELL -> assetSales, HOLD -> neither) — validated
// in the service, not enforced by the type system here.
public record MakeDecisionsRequest(
        List<IndividualDecision> individualDecisions
) {
    public record IndividualDecision(
            CreateDecisionRequest decisionRequest,
            CreatePurchaseLotRequest purchaseLotRequest, // null unless decisionType == BUY
            List<CreateAssetSaleRequest> assetSaleRequests // null/empty unless decisionType == SELL
    ) {}
}
