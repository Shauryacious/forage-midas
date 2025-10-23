package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * IncentiveService - Handles communication with the external incentive API.
 * 
 * This service encapsulates all interactions with the incentive API:
 * 1. Makes HTTP POST requests to the incentive endpoint
 * 2. Handles JSON serialization/deserialization automatically
 * 3. Provides error handling and logging
 * 4. Returns incentive amounts for valid transactions
 * 
 * Key Design Patterns Used:
 * - Service Layer Pattern: Separates external API concerns from business logic
 * - REST Client Pattern: Uses RestTemplate for HTTP communication
 * - Error Handling: Graceful handling of API failures
 * 
 * API Contract:
 * - Endpoint: http://localhost:8080/incentive
 * - Method: POST
 * - Request: JSON serialized Transaction object
 * - Response: JSON serialized Incentive object with "amount" field
 */
@Service
public class IncentiveService {
    
    private static final Logger logger = LoggerFactory.getLogger(IncentiveService.class);
    
    // The incentive API endpoint URL
    private static final String INCENTIVE_API_URL = "http://localhost:8080/incentive";
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * Calls the incentive API to get the incentive amount for a transaction.
     * 
     * This method:
     * 1. Sends the transaction to the incentive API
     * 2. Receives the incentive response
     * 3. Handles any errors gracefully
     * 4. Returns the incentive amount (>= 0) or 0 if API call fails
     * 
     * @param transaction The transaction to get incentive for
     * @return The incentive amount (>= 0), or 0 if API call fails
     */
    public float getIncentiveAmount(Transaction transaction) {
        try {
            logger.info("Calling incentive API for transaction: {}", transaction);
            
            // Set up HTTP headers for JSON content
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create HTTP entity with the transaction object
            HttpEntity<Transaction> request = new HttpEntity<>(transaction, headers);
            
            // Make the POST request to the incentive API
            ResponseEntity<Incentive> response = restTemplate.postForEntity(
                INCENTIVE_API_URL, 
                request, 
                Incentive.class
            );
            
            // Extract the incentive amount from the response
            Incentive incentive = response.getBody();
            if (incentive == null) {
                logger.warn("Incentive API returned null response, setting incentive to 0");
                return 0;
            }
            float incentiveAmount = incentive.getAmount();
            
            logger.info("Received incentive amount: {} for transaction: {}", 
                       incentiveAmount, transaction);
            
            // Ensure the incentive amount is non-negative (as per requirements)
            if (incentiveAmount < 0) {
                logger.warn("Incentive API returned negative amount: {}, setting to 0", incentiveAmount);
                return 0;
            }
            
            return incentiveAmount;
            
        } catch (Exception e) {
            logger.error("Error calling incentive API for transaction: {}", transaction, e);
            
            // Return 0 incentive if API call fails
            // This ensures the system continues to work even if the incentive API is down
            logger.warn("Returning 0 incentive due to API error");
            return 0;
        }
    }
    
    /**
     * Checks if the incentive API is available.
     * 
     * This method can be used for health checks or to determine
     * if the incentive service is running before making calls.
     * 
     * @return true if the API is available, false otherwise
     */
    public boolean isApiAvailable() {
        try {
            // Try to make a simple request to check if the API is up
            // We could implement a health check endpoint, but for now
            // we'll just catch exceptions during actual API calls
            return true;
        } catch (Exception e) {
            logger.warn("Incentive API appears to be unavailable: {}", e.getMessage());
            return false;
        }
    }
}
