# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Instruction Files

Always follow the instructions in HELP.md (teaching/explanation style guidelines).

## Build & Run Commands

```bash
./gradlew build                                    # Build the project
./gradlew test                                     # Run all tests
./gradlew clean build                              # Clean build
./gradlew bootRun                                  # Start the application
./gradlew test --tests ClassName.methodName        # Run a specific test
./gradlew test --tests ClassName                   # Run all tests in a class
```

## Architecture

### Package Structure (com.todoapp)

```
com.todoapp/
├── business/service/      # Business logic (@Service, @Transactional)
├── common/
│   ├── config/            # Spring configurations (PasswordEncoderConfig)
│   └── exception/         # ErrorCode enum, BusinessException
├── domain/                # JPA entities (extend BaseTimeEntity)
├── implement/repository/  # Spring Data JPA repositories
├── pressentation/
│   ├── controller/        # REST controllers (@RestController)
│   ├── dto/request/       # Request DTOs with validation
│   ├── dto/response/      # Response DTOs
│   └── exception/         # GlobalExceptionHandler (@RestControllerAdvice)
└── validation/            # Custom validators (PasswordMatch, etc.)
```

### Entity Hierarchy

All entities extend `BaseTimeEntity` which provides:
- `id` (from BaseEntity) - auto-generated Long
- `createdDate`, `updatedDate` (from BaseTimeEntity) - JPA auditing with Instant

### Exception Handling

- `ErrorCode` enum defines all error codes with HttpStatus, code, and message
- `BusinessException` wraps ErrorCode for business logic errors
- `GlobalExceptionHandler` handles validation errors and business exceptions

### Password Validation

Uses regex pattern for validation:
```java
@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,20}$")
```
Passwords are encrypted with BCryptPasswordEncoder.

## Key Technologies

- **Spring Boot 4.0.1** (Java 21)
- **Spring Data JPA** with H2 in-memory database
- **Jakarta Validation** for bean validation
- **Lombok** for boilerplate reduction
- **passay** for password validation rules

## H2 Console

URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## REST API

**Todo API** (`/api/todos`):
- `POST /api/todos` - Create todo
- `GET /api/todos` - Get all todos
- `GET /api/todos/{id}` - Get todo by ID
- `PATCH /api/todos/{id}/complete` - Mark complete
- `DELETE /api/todos/{id}` - Delete todo

**Auth API**:
- `POST /api/auth/signup` - User registration

## Code Writing Principles

When writing code, follow these principles:

1. **Consistency** - Use the same patterns across similar methods within a class. If one method validates existence before operating, all similar methods should do the same.

2. **Defensive Programming** - Always handle edge cases: non-existent resources, invalid inputs, null values. Never assume the happy path.

3. **Explainable Code** - Write code that can answer "why did you do it this way?" in a code review. Every design decision should be justifiable.

4. **Fail Fast** - Validate early and throw meaningful exceptions. Silent failures (like `deleteById` ignoring non-existent IDs) should be avoided.
