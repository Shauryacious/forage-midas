package com.jpmc.midascore;

import com.jpmc.midascore.component.TransactionConsumer;
import com.jpmc.midascore.foundation.Transaction;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test to verify that our Kafka consumer is working correctly.
 * This test will help us see the transaction amounts without the infinite loop.
 */
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class TransactionConsumerTest {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionConsumerTest.class);
    
    @Autowired
    private KafkaProducer kafkaProducer;
    
    @Autowired
    private FileLoader fileLoader;
    
    @Test
    void testTransactionConsumer() throws InterruptedException {
        logger.info("Starting transaction consumer test...");
        
        // Load the first few transactions from the test data
        String[] transactionLines = fileLoader.loadStrings("/test_data/poiuytrewq.uiop");
        
        // Send only the first 4 transactions as requested
        for (int i = 0; i < Math.min(4, transactionLines.length); i++) {
            String transactionLine = transactionLines[i];
            logger.info("Sending transaction: {}", transactionLine);
            kafkaProducer.send(transactionLine);
            
            // Small delay to ensure proper processing
            Thread.sleep(100);
        }
        
        // Wait a bit to ensure all messages are processed
        Thread.sleep(2000);
        
        logger.info("Test completed. Check the logs above for transaction amounts.");
    }
}
