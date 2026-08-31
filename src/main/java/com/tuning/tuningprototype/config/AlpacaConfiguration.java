package com.tuning.tuningprototype.config;

import markets.alpaca.client.AlpacaClient;
import markets.alpaca.client.AlpacaCredentials;
import markets.alpaca.client.TradingApiEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlpacaConfiguration {

    @Value("${APCA_TRADING_KEY_ID}")
    private String APCA_TRADING_KEY_ID;

    @Value("${APCA_TRADING_SECRET_KEY}")
    private String APCA_TRADING_SECRET_KEY;

    @Bean("getAlpacaClient")
    public AlpacaClient getAlpacaClient() {
        AlpacaCredentials credentials = new AlpacaCredentials(APCA_TRADING_KEY_ID, APCA_TRADING_SECRET_KEY);
        return AlpacaClient.builder(credentials)
                .tradingEnvironment(TradingApiEnvironment.PAPER)
                .build();
    }
}
