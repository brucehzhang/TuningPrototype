package com.tuning.tuningprototype.models.enums;

// The window for the experiment to check market data. More aggressive windows mean more potential decisions, but more expensive cost.
public enum SamplingWindow {
    // Not enabled for now.
    STREAMING,
    // To be used for custom CRON expressions
    CUSTOM_CRON,
    MINUTES_15,
    MINUTES_30,
    HOURS_1,
    HOURS_2,
    HOURS_3,
    HOURS_4,
    DAYS_1
}
