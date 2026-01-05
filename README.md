# Midas Core

A Spring Boot application for processing financial transactions with incentive management, built for the JPMorgan Chase Advanced Software Engineering Forage program.

## Overview

Midas Core is a transaction processing system that:
- Consumes transaction messages from Kafka
- Validates transactions (sender/recipient existence, sufficient balance)
- Integrates with an external incentive API to calculate transaction incentives
- Maintains user balances in a database
- Records all transaction history

## Features

- **Kafka Integration**: Consumes transaction messages from a Kafka topic
- **Transaction Validation**: Validates sender/recipient existence and sufficient balance
- **Incentive Management**: Integrates with external incentive API to calculate rewards
- **Database Persistence**: Stores user records and transaction history using H2 database
- **Atomic Operations**: Ensures data consistency with transactional processing
- **REST API Integration**: Communicates with external incentive service via REST

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Kafka 3.1.4** - For message consumption
- **Spring Data JPA** - For database operations
- **H2 Database** - In-memory database for development
- **Maven** - Build and dependency management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Kafka (running on localhost:9092 by default)
- External Incentive API (running on localhost:8080 by default)

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd forage-midas
```

### 2. Start Kafka

Ensure Kafka is running on `localhost:9092`. You can use Docker:

```bash
docker run -p 9092:9092 apache/kafka:latest
```

Or use your local Kafka installation.

### 3. Start the Incentive API

The application expects an incentive API service running on `http://localhost:8080/incentive`. Make sure this service is running before starting Midas Core.

### 4. Build the Project

```bash
./mvnw clean install
```

### 5. Run the Application

```bash
./mvnw spring-boot:run
```

Or using the JAR file:

```bash
java -jar target/midas-core-1.0.0.jar
```

## Configuration

The application configuration is in `application.yml`:

- **Database**: H2 in-memory database (JDBC URL: `jdbc:h2:mem:testdb`)
- **Kafka**: Configured to connect to `localhost:9092`
- **Kafka Topic**: `transactions` (configurable via `general.kafka-topic`)
- **Incentive API**: `http://localhost:8080/incentive`

### H2 Console

The H2 console is enabled for development. Access it at:
```
http://localhost:8080/h2-console
```

Connection details:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Architecture

### Components

1. **TransactionConsumer**: Kafka listener that consumes transaction messages
2. **TransactionService**: Core business logic for transaction processing
3. **IncentiveService**: Handles communication with external incentive API
4. **DatabaseConduit**: Database initialization and setup utilities
5. **Repositories**: Data access layer for User and Transaction entities

### Transaction Flow

1. Transaction message arrives via Kafka
2. `TransactionConsumer` receives and deserializes the message
3. `TransactionService` validates the transaction:
   - Checks sender exists
   - Checks recipient exists
   - Verifies sufficient balance
4. `IncentiveService` calls external API to get incentive amount
5. Transaction is recorded in database
6. User balances are updated atomically

## Project Structure

```
src/
├── main/
│   ├── java/com/jpmc/midascore/
│   │   ├── component/          # Kafka consumers and components
│   │   ├── config/              # Spring configuration
│   │   ├── entity/              # JPA entities
│   │   ├── foundation/          # Domain models
│   │   ├── repository/          # Data access layer
│   │   ├── service/             # Business logic
│   │   └── MidasCoreApplication.java
│   └── resources/
│       └── application.yml      # Application configuration
└── test/
    └── java/com/jpmc/midascore/ # Test classes
```

## Testing

Run tests with:

```bash
./mvnw test
```

The test suite includes:
- Unit tests for services
- Integration tests with TestContainers for Kafka
- Transaction validation tests

## API Endpoints

The application primarily consumes from Kafka, but you can interact with the database through the H2 console or by extending the application with REST endpoints.

## Development

### Adding New Features

1. Follow the existing service layer pattern
2. Use `@Transactional` for database operations requiring consistency
3. Add appropriate logging using SLF4J
4. Write tests for new functionality

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Maintain consistent formatting

## Troubleshooting

### Kafka Connection Issues

- Ensure Kafka is running on `localhost:9092`
- Check Kafka topic exists: `transactions`
- Verify consumer group configuration

### Database Issues

- H2 database is in-memory and resets on restart
- Check H2 console for data verification
- Review logs for SQL errors

### Incentive API Issues

- Verify the incentive API is running on `http://localhost:8080/incentive`
- Check network connectivity
- Review application logs for API call errors

## License

This project is part of the JPMorgan Chase Advanced Software Engineering Forage program.

## Contributing

This is a Forage program project. Contributions should follow the program guidelines.
