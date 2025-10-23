package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.TransactionRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * TransactionRecordRepository - Spring Data JPA repository for TransactionRecord entities.
 * 
 * This interface extends CrudRepository, which provides basic CRUD operations:
 * - save(TransactionRecord) - Save a new transaction record
 * - findById(Long) - Find transaction by ID
 * - findAll() - Get all transactions
 * - delete(TransactionRecord) - Delete a transaction
 * 
 * Spring Data JPA automatically implements this interface at runtime.
 * We don't need to write any implementation code - Spring handles it!
 * 
 * Key Benefits:
 * - No boilerplate code for basic database operations
 * - Type-safe method signatures
 * - Automatic transaction management
 * - Built-in pagination and sorting support
 */
@Repository
public interface TransactionRecordRepository extends CrudRepository<TransactionRecord, Long> {
    
    // We can add custom query methods here if needed in the future
    // For example:
    // List<TransactionRecord> findBySenderId(Long senderId);
    // List<TransactionRecord> findByRecipientId(Long recipientId);
    // List<TransactionRecord> findByAmountGreaterThan(Float amount);
}
