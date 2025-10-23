package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Test to verify that our TransactionService works correctly.
 * This test will help us see the waldorf user balance without the infinite loop.
 */
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class TransactionServiceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceTest.class);
    
    @Autowired
    private KafkaProducer kafkaProducer;
    
    @Autowired
    private UserPopulator userPopulator;
    
    @Autowired
    private FileLoader fileLoader;
    
    @Autowired
    private TransactionService transactionService;
    
    @Test
    void testTransactionProcessing() throws InterruptedException {
        logger.info("Starting transaction processing test...");
        
        // Step 1: Populate users (including waldorf with 444.55)
        logger.info("Populating users...");
        userPopulator.populate();
        
        // Step 2: Load and send transactions
        logger.info("Loading and sending transactions...");
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");
        for (String transactionLine : transactionLines) {
            logger.info("Sending transaction: {}", transactionLine);
            kafkaProducer.send(transactionLine);
            
            // Small delay to ensure proper processing
            Thread.sleep(100);
        }
        
        // Step 3: Wait for all transactions to be processed
        logger.info("Waiting for transactions to be processed...");
        Thread.sleep(3000);
        
        // Step 4: Check waldorf's final balance
        Float waldorfBalance = transactionService.getUserBalanceByName("waldorf");
        if (waldorfBalance != null) {
            logger.info("Waldorf's final balance: {}", waldorfBalance);
            logger.info("Waldorf's balance rounded down: {}", Math.floor(waldorfBalance));
        } else {
            logger.error("Could not find waldorf user!");
        }
        
        logger.info("Test completed. Waldorf's balance: {}", waldorfBalance);
    }
}
