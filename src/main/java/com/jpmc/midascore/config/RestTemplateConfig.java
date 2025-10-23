package com.jpmc.midascore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplateConfig - Configuration class for REST client beans.
 * 
 * This configuration provides RestTemplate beans for making HTTP calls
 * to external services like the incentive API.
 * 
 * Key Benefits:
 * - Centralized configuration for REST clients
 * - Consistent HTTP client setup across the application
 * - Easy to modify timeouts, interceptors, etc. in one place
 */
@Configuration
public class RestTemplateConfig {
    
    /**
     * Creates a RestTemplate bean for making HTTP calls.
     * 
     * RestTemplate is Spring's synchronous HTTP client that simplifies
     * making REST API calls. It handles JSON serialization/deserialization
     * automatically when used with appropriate message converters.
     * 
     * @return A configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
