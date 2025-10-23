package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Incentive - Represents the response from the incentive API.
 * 
 * This class mirrors the JSON response structure from the incentive API endpoint.
 * The API returns a JSON object with a single "amount" field representing
 * the incentive amount (>= 0) that should be added to the recipient's balance.
 * 
 * Key Design Points:
 * - @JsonIgnoreProperties(ignoreUnknown = true): Allows the JSON deserializer
 *   to ignore any additional fields in the API response that we don't care about
 * - Simple structure with just an amount field as specified in the requirements
 * - Used for deserializing the REST API response from the incentive service
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Incentive {
    
    private float amount;
    
    /**
     * Default constructor required for JSON deserialization.
     * Jackson needs a no-args constructor to create instances from JSON.
     */
    public Incentive() {
    }
    
    /**
     * Constructor for creating incentive objects.
     * 
     * @param amount The incentive amount (must be >= 0)
     */
    public Incentive(float amount) {
        this.amount = amount;
    }
    
    /**
     * Gets the incentive amount.
     * 
     * @return The incentive amount
     */
    public float getAmount() {
        return amount;
    }
    
    /**
     * Sets the incentive amount.
     * 
     * @param amount The incentive amount (must be >= 0)
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }
    
    @Override
    public String toString() {
        return "Incentive{amount=" + amount + "}";
    }
}
