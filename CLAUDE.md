# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

- Backend API project using Spring Boot (Java 21)
- Primary goal: **production-quality code** (readable, testable, efficient), not just "works"

## Build & Run Commands

```bash
./gradlew build                                    # Build the project
./gradlew test                                     # Run all tests
./gradlew clean build                              # Clean build
./gradlew bootRun                                  # Start the application
./gradlew test --tests ClassName.methodName        # Run a specific test
./gradlew test --tests ClassName                   # Run all tests in a class
```

## Non-negotiable Rules (MUST)

1. Do not output "working-only" code. Always optimize for clarity + maintainability + predictable performance.
2. Avoid unnecessary work: no redundant loops, redundant allocations, redundant conversions, redundant branching, or repeated getter calls without justification.
3. Keep error handling consistent: use ErrorCode for code/message/status; no hardcoded literals.
4. If you change behavior/contract, call it out explicitly and ask before proceeding.
5. Prefer deterministic code paths: parsing/validation/formatting should be plain code, not "clever" patterns that hide cost.

## Refactor Standard (default expectation)

Whenever you edit existing code, you MUST:
1. Provide the refactored code.
2. Explain: what was wasteful or risky, and how you removed it (2-5 bullets).
3. Provide a small verification plan: tests to add or how to run existing tests.

## Performance Checklist

When writing or refactoring code, check these (in order):
- Remove repeated computation / repeated access (e.g., calling the same method multiple times)
- Avoid pre-check patterns that duplicate work (e.g., `hasXxx()` then `getXxx()`) unless you prove benefit
- Minimize passes over collections: aim for one pass when building maps/lists
- Avoid intermediate collections unless they improve clarity AND cost is negligible
- For hot paths, prefer simple loops; for cold paths, prioritize readability but still avoid obvious waste

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
- For validation errors: build error map in single transformation, define key-collision policy explicitly

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
- `POST /api/auth/login` - User login

## Code Writing Principles

1. **Consistency** - Use the same patterns across similar methods within a class. If one method validates existence before operating, all similar methods should do the same.

2. **Defensive Programming** - Always handle edge cases: non-existent resources, invalid inputs, null values. Never assume the happy path.

3. **Explainable Code** - Write code that can answer "why did you do it this way?" in a code review. Every design decision should be justifiable.

4. **Fail Fast** - Validate early and throw meaningful exceptions. Silent failures (like `deleteById` ignoring non-existent IDs) should be avoided.

5. **No Hardcoding** - If a value is defined in an enum, constant, or config, always use that reference. Never duplicate values as string literals or magic numbers.

6. **Avoid unnecessary work** - Don’t add redundant pre-checks. Because getFieldErrors()/getGlobalErrors() generally yield empty lists rather than null, default to a single-pass transformation. Use hasFieldErrors()/hasGlobalErrors() only when it clearly reduces work (e.g., avoiding an expensive transformation) or materially improves readability.

7. **Extract Repeated Calls** - If calling the same method multiple times (e.g., `e.getBindingResult()`), extract it to a local variable.

8. **Production-Ready from Start** - Write code as if it will be deployed immediately. No "quick fix now, improve later" mindset.

## Java Style Preferences

- Prefer small pure functions for transformations (e.g., `BindingResult -> Map<String,String>`)
- Prefer immutability by default (`final`), unless mutation improves clarity/performance
- Keep methods short and named by intent (what/why, not how)
- Spring Boot 어노테이션 사용 시 해당 어노테이션의 역할을 한 줄로 간략히 설명할 것

## Testing Expectations

- **테스트 우선 워크플로우**: 구현 코드보다 테스트 코드를 먼저 제안하고, 사용자가 검토/승인한 뒤에 구현 코드를 작성한다
- If you introduce or modify logic, propose unit tests (JUnit5)
- For exception handlers, include tests for: field errors only, global errors only, mixed, duplicate keys policy
- Run `./gradlew test` to verify all tests pass before committing

## Code Explanation Guidelines

코드를 설명할 때 반드시 아래 순서를 따른다:

1. **비유로 시작** - 일상생활의 무언가에 비유하여 핵심 개념을 잡는다
2. **다이어그램** - ASCII art로 흐름, 구조, 관계를 시각화한다
3. **코드 워크스루** - 단계별로 무슨 일이 일어나는지 설명한다
4. **주의점(gotcha)** - 흔한 실수나 오해하기 쉬운 부분을 짚는다

- 설명은 대화체로 작성하고, 복잡한 개념은 여러 비유를 활용한다

## Interaction Protocol

- **완성 코드를 바로 제공하지 않는다** - 사용자가 명시적으로 요청하기 전까지 완성된 구현 코드를 주지 않는다
- If the task is ambiguous, ask 1-3 clarifying questions before coding
- Otherwise, implement the smallest correct change first, then propose improvements as optional follow-ups
- When you propose a "better" solution, include trade-offs (readability vs allocations vs extensibility)
