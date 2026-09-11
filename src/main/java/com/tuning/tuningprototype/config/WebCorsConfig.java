package com.tuning.tuningprototype.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Mirrors the Access-Control-Allow-Origin already configured on API Gateway. Needed because
 * /users/signup, /users/login, /users/logout, and POST /experiments are wired as HTTP_PROXY
 * integrations on the gateway — those pass the backend's response through untouched, so the
 * header has to originate here for the browser to be allowed to read the response. Every
 * other route (non-proxy HTTP integration) already gets the header injected at the gateway,
 * so this filter is redundant-but-harmless there.
 *
 * A CorsFilter (rather than a WebMvcConfigurer#addCorsMappings mapping) is used so the header
 * is still applied to error responses returned via ResponseEntity.badRequest()/
 * internalServerError() in the controllers' catch blocks, not just 200s.
 */
@Configuration(proxyBeanMethods = false)
public class WebCorsConfig {

    @Bean
    public CorsFilter corsFilter(@Value("${app.cors.allowed-origins:*}") List<String> allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        // No cookies/credentialed requests from the frontend (session token is sent as a
        // bearer-style Authorization header), so this stays false — required anyway for
        // Access-Control-Allow-Origin: * to be valid per the CORS spec.
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }
}
