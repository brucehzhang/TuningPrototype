package com.tuning.tuningprototype.config;

import markets.alpaca.client.AlpacaClient;
import markets.alpaca.client.AlpacaCredentials;
import markets.alpaca.client.TradingApiEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlpacaConfiguration {

    @Bean("getAlpacaClient")
    public AlpacaClient getAlpacaClient() {
        AlpacaCredentials credentials = AlpacaCredentials.fromTradingApiEnvironmentVariables();
        return AlpacaClient.builder(credentials)
                .tradingEnvironment(TradingApiEnvironment.PAPER)
                .build();
    }
}
