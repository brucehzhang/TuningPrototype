# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Java JDK Requirement
This project uses **Java 25**. If Java 25 is not in your default system path, prefix commands with `JAVA_HOME` pointing to the JDK 25 installation (e.g., `JAVA_HOME=/home/bruce/.jdks/corretto-25.0.3`).

### Build & Run Commands
- **Build project:** `./gradlew build`
- **Run application:** `./gradlew bootRun`
- **Clean build:** `./gradlew clean`

### Testing Commands
- **Run all tests:** `./gradlew test`
- **Run a single test class:** `./gradlew test --tests "com.tuning.tuningprototype.TuningPrototypeApplicationTests"`
- **Run a specific test method:** `./gradlew test --tests "com.tuning.tuningprototype.TuningPrototypeApplicationTests.contextLoads"`

---

## Architecture & Codebase Structure

This is a standard **Spring Boot** application configured with Spring AI, Alpaca SDK, and data access.

### Core Stack
- **Framework:** Spring Boot 4.1.0
- **Language:** Java 25
- **AI Integrations (Spring AI 2.0.0):**
  - Model Context Protocol (MCP) Server support (`spring-ai-starter-mcp-server-webmvc`)
- **External APIs / SDKs:**
  - Alpaca Java SDK (`markets.alpaca:alpaca-java:0.1.1`) for market data analysis
- **Database/Data Access:**
  - Spring Data JPA
  - MySQL Runtime Driver (`mysql-connector-j`)
- **Utilities:** Lombok is configured for boilerplates.

### Layout
- **Main Application Entrypoint:** `src/main/java/com/tuning/tuningprototype/TuningPrototypeApplication.java`
- **Application Configuration:** `src/main/resources/application.properties`
- **Controllers:**
  - `ExperimentController.java`: Handles `/experiments` endpoints (health, get experiment).
  - `MarketDataController.java`: Handles `/marketData` endpoints (trades, stock aggregates, news).
- **Services:**
  - `IExperimentService` & `BasicExperimentService`: Manage experiment data retrieval.
  - `IMarketAnalysisService` & `AlpacaMarketAnalysisService`: Connect to Alpaca API to fetch financial markets data.
- **Models & Requests/Responses:** Packages under `src/main/java/com/tuning/tuningprototype/models` for data structures, DTOs, and API requests/responses.
- **Test Context:** `src/test/java/com/tuning/tuningprototype/TuningPrototypeApplicationTests.java`

---

## API & Curl Examples

### Get Experiment by ID
```bash
curl -X GET http://localhost:8080/experiments/1
```

### Health Check
```bash
curl -X GET http://localhost:8080/experiments/health
```

### Fetch Stock Trade Data
```bash
curl -X POST http://localhost:8080/marketData/stocks/trades \
  -H "Content-Type: application/json" \
  -d '{
    "tickers": ["AAPL", "MSFT"],
    "startTime": 1711200000,
    "endTime": 1711200900
  }'
```

### Fetch Stock Aggregate Data
```bash
curl -X POST http://localhost:8080/marketData/stocks/aggregates \
  -H "Content-Type: application/json" \
  -d '{
    "tickers": ["AAPL"],
    "timeframe": "1Day",
    "startTime": 1711200000,
    "endTime": 1711290000
  }'
```
