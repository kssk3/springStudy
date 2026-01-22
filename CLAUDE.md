Always follow the instructions in plan.md.

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

**Package Structure**: All code resides in the `test` package (`src/main/java/test`). The codebase follows Spring Boot layered architecture:
- **Controller**: `TodoController` - REST API endpoints for todo operations
- **Service**: `TodoService` - Business logic layer
- **Repository**: `TodoRepository` - JPA data access (extends `JpaRepository`)
- **Entity**: `Todo` - JPA entity with Lombok annotations
- **DTOs**: `TodoCreateRequest`, `TodoResponse` - Request/response objects
- **Exception Handling**: `GlobalExceptionHandler` - Centralized `@RestControllerAdvice` for validation and error handling

**Key Technologies**:
- **Spring Boot 4.0.1**: Framework (requires Java 21)
- **Spring Data JPA**: ORM and database access
- **Thymeleaf**: Server-side templating
- **H2 Database**: In-memory database (check `application.yml` for configuration)
- **Lombok**: Automatic generation of getters, setters, constructors via annotations
- **Jakarta Validation**: Bean validation with `@Valid` annotations

**Configuration**: `src/main/resources/application.yml` contains Spring configuration.

**H2 Console**: Accessible at `http://localhost:8080/h2-console` when the application is running
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

**REST API Endpoints**: The application provides a Todo REST API at `/api/todos`:
- `POST /api/todos` - Create a new todo (requires `title` and `description`)
- `GET /api/todos` - Get all todos
- `GET /api/todos/{id}` - Get a specific todo by ID
- `PATCH /api/todos/{id}/complete` - Mark a todo as complete
- `DELETE /api/todos/{id}` - Delete a todo

## Development Notes

- Java 21 is required (configured via Gradle toolchain)
- Lombok is configured; use annotations like `@Data`, `@Getter`, `@Setter`, `@AllArgsConstructor` to reduce boilerplate
- H2 console is enabled in `application.yml` at `/h2-console` (useful for database inspection during development)
- The project uses JUnit 5 (Jupiter) for testing
- Validation is handled via `@Valid` annotations on request DTOs; `GlobalExceptionHandler` provides centralized error handling with structured error responses
- JPA is configured with `ddl-auto: create` (recreates tables on startup) and `show-sql: true` for SQL logging
