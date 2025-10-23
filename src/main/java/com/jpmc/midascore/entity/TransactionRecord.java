package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * TransactionRecord Entity - Represents a validated and recorded transaction in the database.
 * 
 * This entity maintains a many-to-one relationship with UserRecord entities for both
 * sender and recipient. This allows us to:
 * 1. Track which users were involved in each transaction
 * 2. Maintain referential integrity in the database
 * 3. Enable efficient queries for transaction history
 * 
 * JPA is a Java Persistence API that provides a way to map Java objects to database tables.
 * It is a standard for ORM (Object-Relational Mapping) that allows us to interact with the database using Java objects.
 * 
 * Key JPA Concepts Used:
 * - @Entity: Marks this class as a JPA entity that maps to a database table
 * - @Id + @GeneratedValue: Creates an auto-incrementing primary key
 * - @ManyToOne: Establishes many-to-one relationship with UserRecord
 * - @JoinColumn: Specifies the foreign key column name
 * - @Column: Maps fields to database columns with specific constraints
 */
@Entity
@Table(name = "transaction_records")
public class TransactionRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Many-to-one relationship with the sender UserRecord.
     * 
     * How this works:
     * - Many transactions can have the same sender (one user can send multiple transactions)
     * - Each transaction has exactly one sender
     * - JPA automatically creates a foreign key column "sender_id" in the transaction_records table
     * - This maintains referential integrity - we can't have transactions with non-existent users
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;
    
    /**
     * Many-to-one relationship with the recipient UserRecord.
     * 
     * Similar to sender relationship:
     * - Many transactions can have the same recipient
     * - Each transaction has exactly one recipient
     * - Foreign key "recipient_id" links to the users table
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;
    
    /**
     * Transaction amount - stored as a decimal in the database.
     * 
     * Note: For Float types, we don't specify precision/scale as Hibernate
     * handles floating-point types differently than decimal types.
     * For production financial applications, consider using BigDecimal
     * with proper precision/scale for exact decimal arithmetic.
     */
    @Column(name = "amount", nullable = false)
    private Float amount;
    
    /**
     * Timestamp when the transaction was processed.
     * 
     * Why we track this:
     * - Audit trail for financial transactions
     * - Helps with debugging and compliance
     * - Enables time-based queries and reporting
     */
    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;
    
    /**
     * Default constructor required by JPA.
     * JPA needs a no-args constructor to create entity instances.
     */
    protected TransactionRecord() {
    }
    
    /**
     * Constructor for creating new transaction records.
     * 
     * @param sender The user who sent the transaction
     * @param recipient The user who received the transaction  
     * @param amount The transaction amount
     */
    public TransactionRecord(UserRecord sender, UserRecord recipient, Float amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.processedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    // JPA requires getters and setters for all persistent fields
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public UserRecord getSender() {
        return sender;
    }
    
    public void setSender(UserRecord sender) {
        this.sender = sender;
    }
    
    public UserRecord getRecipient() {
        return recipient;
    }
    
    public void setRecipient(UserRecord recipient) {
        this.recipient = recipient;
    }
    
    public Float getAmount() {
        return amount;
    }
    
    public void setAmount(Float amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
    
    @Override
    public String toString() {
        return String.format("TransactionRecord{id=%d, sender=%s, recipient=%s, amount=%.2f, processedAt=%s}", 
                           id, sender.getName(), recipient.getName(), amount, processedAt);
    }
}
