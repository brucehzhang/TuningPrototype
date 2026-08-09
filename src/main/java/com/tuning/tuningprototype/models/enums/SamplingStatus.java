package com.tuning.tuningprototype.models.enums;

public enum SamplingStatus {
    // Starting state of the sample
    IN_PROGRESS,
    // State after reading market data and generating market insight for the sample
    DECIDING,
    // State after making all decisions
    COMPLETED,
    // State if any error occurs, can restart using stored market insights
    FAILED
}
