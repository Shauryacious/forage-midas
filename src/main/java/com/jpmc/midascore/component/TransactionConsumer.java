package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka Consumer for processing incoming transactions.
 * 
 * This class listens to the configured Kafka topic and processes incoming transaction messages.
 * It now integrates with the database to validate and record transactions.
 * 
 * Key concepts:
 * - @KafkaListener: Spring annotation that automatically creates a Kafka consumer
 * - Topic configuration: Uses the topic name from application.yml (general.kafka-topic)
 * - Message deserialization: Automatically deserializes JSON messages to Transaction objects
 * - Service Integration: Uses TransactionService for business logic and database operations
 * - Error handling: Logs any issues with message processing
 */
@Component
public class TransactionConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);
    
    @Autowired
    private TransactionService transactionService;
    
    /**
     * Listens to the configured Kafka topic and processes incoming transaction messages.
     * 
     * How this works:
     * 1. Spring Kafka automatically creates a consumer for this method
     * 2. The consumer subscribes to the topic specified in application.yml
     * 3. When a message arrives, this method is called with the deserialized Transaction object
     * 4. The transaction is validated and processed through TransactionService
     * 5. Database operations are handled atomically with @Transactional
     * 
     * @param transaction The deserialized Transaction object from Kafka
     */
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransaction(Transaction transaction) {
        try {
            // Log the received transaction for debugging purposes
            logger.info("Received transaction: {}", transaction);
            
            // Process the transaction through our service layer
            // This will validate the transaction and update the database if valid
            boolean processed = transactionService.processTransaction(transaction);
            
            if (processed) {
                logger.info("Transaction processed successfully: Sender={}, Recipient={}, Amount={}", 
                           transaction.getSenderId(), 
                           transaction.getRecipientId(), 
                           transaction.getAmount());
            } else {
                logger.warn("Transaction rejected: Sender={}, Recipient={}, Amount={}", 
                           transaction.getSenderId(), 
                           transaction.getRecipientId(), 
                           transaction.getAmount());
            }
            
        } catch (Exception e) {
            // Log any errors that occur during transaction processing
            logger.error("Error processing transaction: {}", transaction, e);
        }
    }
}
