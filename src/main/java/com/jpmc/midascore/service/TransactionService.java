package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TransactionService - Handles business logic for transaction validation and processing.
 * 
 * This service encapsulates all the business rules for processing financial transactions:
 * 1. Validates transaction data (sender/recipient existence, sufficient balance)
 * 2. Records valid transactions to the database
 * 3. Updates user balances atomically
 * 4. Handles error cases gracefully
 * 
 * Key Design Patterns Used:
 * - Service Layer Pattern: Separates business logic from data access
 * - Transaction Management: Ensures data consistency with @Transactional
 * - Repository Pattern: Abstracts database operations
 */
@Service
public class TransactionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRecordRepository transactionRecordRepository;
    
    @Autowired
    private IncentiveService incentiveService;
    
    /**
     * Processes a transaction by validating it and updating the database.
     * 
     * This method implements the core business logic:
     * 1. Validates that both sender and recipient exist
     * 2. Checks that sender has sufficient balance
     * 3. Creates a TransactionRecord in the database
     * 4. Updates both user balances atomically
     * 
     * @Transactional ensures that either ALL operations succeed or ALL fail.
     * This prevents partial updates that could corrupt financial data.
     * 
     * @param transaction The transaction to process
     * @return true if transaction was processed successfully, false if validation failed
     */
    @Transactional
    public boolean processTransaction(Transaction transaction) {
        try {
            logger.info("Processing transaction: Sender={}, Recipient={}, Amount={}", 
                       transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());
            
            // Step 1: Validate sender exists and has sufficient balance
            UserRecord sender = userRepository.findById(transaction.getSenderId()).orElse(null);
            if (sender == null) {
                logger.warn("Transaction rejected: Sender ID {} does not exist", transaction.getSenderId());
                return false;
            }
            
            // Step 2: Validate recipient exists
            UserRecord recipient = userRepository.findById(transaction.getRecipientId()).orElse(null);
            if (recipient == null) {
                logger.warn("Transaction rejected: Recipient ID {} does not exist", transaction.getRecipientId());
                return false;
            }
            
            // Step 3: Check if sender has sufficient balance
            if (sender.getBalance() < transaction.getAmount()) {
                logger.warn("Transaction rejected: Sender {} has insufficient balance. Required: {}, Available: {}", 
                           sender.getName(), transaction.getAmount(), sender.getBalance());
                return false;
            }
            
            // Step 4: All validations passed - process the transaction
            logger.info("Transaction validation passed. Processing...");
            
            // Step 5: Get incentive amount from the incentive API
            float incentiveAmount = incentiveService.getIncentiveAmount(transaction);
            logger.info("Received incentive amount: {} for transaction: {}", incentiveAmount, transaction);
            
            // Create and save the transaction record with incentive
            TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
            transactionRecordRepository.save(transactionRecord);
            
            // Store original balances for logging
            float originalSenderBalance = sender.getBalance();
            float originalRecipientBalance = recipient.getBalance();
            
            // Update sender balance (subtract amount)
            float newSenderBalance = sender.getBalance() - transaction.getAmount();
            sender.setBalance(newSenderBalance);
            userRepository.save(sender);
            
            // Update recipient balance (add amount + incentive)
            float newRecipientBalance = recipient.getBalance() + transaction.getAmount() + incentiveAmount;
            recipient.setBalance(newRecipientBalance);
            userRepository.save(recipient);
            
            logger.info("Transaction processed successfully. Sender {} balance: {} -> {}, Recipient {} balance: {} -> {} (incentive: {})", 
                       sender.getName(), originalSenderBalance, sender.getBalance(),
                       recipient.getName(), originalRecipientBalance, recipient.getBalance(), incentiveAmount);
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error processing transaction: {}", transaction, e);
            // The @Transactional annotation will automatically rollback the transaction
            // if an exception is thrown, ensuring data consistency
            throw e;
        }
    }
    
    /**
     * Gets the current balance of a user by their ID.
     * 
     * This method is useful for debugging and verification.
     * 
     * @param userId The ID of the user
     * @return The user's current balance, or null if user doesn't exist
     */
    public Float getUserBalance(Long userId) {
        UserRecord user = userRepository.findById(userId).orElse(null);
        return user != null ? user.getBalance() : null;
    }
    
    /**
     * Gets the current balance of a user by their name.
     * 
     * This method is useful for finding users by name (like "waldorf").
     * 
     * @param userName The name of the user
     * @return The user's current balance, or null if user doesn't exist
     */
    public Float getUserBalanceByName(String userName) {
        UserRecord user = userRepository.findByName(userName);
        return user != null ? user.getBalance() : null;
    }
}
