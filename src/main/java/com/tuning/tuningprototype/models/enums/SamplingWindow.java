package com.tuning.tuningprototype.models.enums;

// The window for the experiment to check market data. More aggressive windows mean more potential decisions, but more expensive cost.
public enum SamplingWindow {
    // Not enabled for now.
    STREAMING(-1),
    // To be used for custom CRON expressions
    CUSTOM_CRON(-1),
    MINUTES_15(900),
    MINUTES_30(1800),
    HOURS_1(3600),
    HOURS_2(7200),
    HOURS_3(10800),
    HOURS_4(14400),
    DAYS_1(86400);

    public final long intervalLength;

    SamplingWindow(long i) {
        intervalLength = i;
    }
}
