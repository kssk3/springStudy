# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 4.0.1 web application using Java 21, built with Gradle. This is a standard Spring MVC application with JPA persistence, Thymeleaf templating, and H2 database.

## Build & Run Commands

### Build & Test
```bash
./gradlew build          # Build the project
./gradlew test           # Run all tests
./gradlew clean build    # Clean build
```

### Run Application
```bash
./gradlew bootRun        # Start the Spring Boot application
```

### Development
```bash
./gradlew test --tests ClassName.methodName    # Run a specific test
./gradlew test --tests ClassName               # Run all tests in a class
./gradlew bootRun --args='--spring.profiles.active=dev'  # Run with profile
```

## Architecture

**Package Structure**: All code currently resides in the `test` package. When adding new features, follow Spring Boot layered architecture:
- Controllers: Handle HTTP requests
- Services: Business logic
- Repositories: Data access (JPA)
- Models/Entities: JPA entities
- DTOs: Data transfer objects if needed

**Key Technologies**:
- **Spring Boot 4.0.1**: Framework (requires Java 21)
- **Spring Data JPA**: ORM and database access
- **Thymeleaf**: Server-side templating
- **H2 Database**: In-memory database (check `application.properties` for configuration)
- **Lombok**: Automatic generation of getters, setters, constructors via annotations

**Configuration**: `src/main/resources/application.properties` contains Spring configuration.

## Development Notes

- Java 21 is required (configured via Gradle toolchain)
- Lombok is configured; use annotations like `@Data`, `@Getter`, `@Setter`, `@AllArgsConstructor` to reduce boilerplate
- H2 console may be available if enabled in application.properties (useful for database inspection during development)
- The project uses JUnit 5 (Jupiter) for testing
