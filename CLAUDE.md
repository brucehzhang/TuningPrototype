# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Java JDK Requirement
This project uses **Java 25**. If Java 25 is not in your default system path, prefix commands with `JAVA_HOME` pointing to the JDK 25 installation (e.g., `JAVA_HOME=/home/bruce/.jdks/corretto-25.0.3`).

### Build & Run Commands
- **Build project:** `./gradlew build` (or `JAVA_HOME=/home/bruce/.jdks/corretto-25.0.3 ./gradlew build`)
- **Run application:** `./gradlew bootRun`
- **Clean build:** `./gradlew clean`

### Testing Commands
- **Run all tests:** `./gradlew test`
- **Run a single test class:** `./gradlew test --tests "com.tuning.tuningprototype.TuningPrototypeApplicationTests"`
- **Run a specific test method:** `./gradlew test --tests "com.tuning.tuningprototype.TuningPrototypeApplicationTests.contextLoads"`

---

## Architecture & Codebase Structure

This is a standard **Spring Boot** application configured with Spring AI and data access.

### Core Stack
- **Framework:** Spring Boot 4.1.0
- **Language:** Java 25
- **AI Integrations (Spring AI 2.0.0):**
  - Anthropic Chat Model support (`spring-ai-starter-model-anthropic`)
  - OpenAI Chat Model support (`spring-ai-starter-model-openai`)
  - Model Context Protocol (MCP) Server support (`spring-ai-starter-mcp-server-webmvc`)
- **Database/Data Access:**
  - Spring Data JPA
  - MySQL Runtime Driver (`mysql-connector-j`)
- **Utilities:** Lombok is configured for boilerplates.

### Layout
- **Main Application Entrypoint:** `src/main/java/com/tuning/tuningprototype/TuningPrototypeApplication.java`
- **Application Configuration:** `src/main/resources/application.properties`
- **Test Context:** `src/test/java/com/tuning/tuningprototype/TuningPrototypeApplicationTests.java`
