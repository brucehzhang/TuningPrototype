package com.tuning.tuningprototype.models.requests;

import java.math.BigDecimal;

// Request for creating a new experiment
// Starting money amount is always set to current money amount on create
public record CreateWalletRequest(Long experimentId,
                                  BigDecimal startingMoneyAmount, // starting balance at wallet creation
                                  Long openedTime, // defaults to start time of experiment if in DRAFT, but can happen in the middle due to forex trading
                                  String currencyCode) {
}