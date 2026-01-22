# Spring Boot 실전 학습 커리큘럼

> 현재 TodoApp 프로젝트 상태 기반 단계별 학습 가이드

---

## 현재 프로젝트 상태

### ✅ 구현 완료
- Todo CRUD API (`TodoController`, `TodoService`)
- Todo 엔티티 및 Repository
- User 엔티티 (email unique 제약조건 포함)
- UserRepository (`findByEmail`, `existsByEmail`)
- 회원가입 요청 검증 (`SignUpRequest`, `@Password`, `@PasswordMatch`)
- GlobalExceptionHandler (기본 예외 처리)
- 통합 테스트 (`TodoIntegrationTest`)

### ❌ 미구현 (학습 대상)
- BaseTimeEntity (JPA Auditing)
- UserService (회원가입 로직)
- AuthController (회원가입 API)
- PasswordEncoderConfig (비밀번호 암호화)
- 커스텀 예외 계층 (ErrorCode, BusinessException)
- 단위 테스트 (Service 레이어)
- 로깅
- 환경 설정 분리
- Spring Security / JWT

---

## Phase 1: 회원가입 기능 완성

### 1.1 비밀번호 암호화 설정

**학습 목표**: Spring Security의 PasswordEncoder 설정

**현재 상태**
- `PasswordEncoderConfig.java`가 빈 클래스로 존재
- `build.gradle`에 `spring-security-crypto` 의존성 없음

**학습 내용**
- BCryptPasswordEncoder란?
- 단방향 해시 함수의 특징
- Salt의 역할

**실습 과제**
1. `build.gradle`에 의존성 추가
   ```groovy
   implementation 'org.springframework.security:spring-security-crypto'
   ```
2. `PasswordEncoderConfig`에 Bean 등록
3. 테스트로 암호화 동작 확인

---

### 1.2 UserService 구현

**학습 목표**: Service 레이어 작성 및 트랜잭션 관리

**현재 상태**
- `UserService.java`가 빈 클래스로 존재
- `UserRepository`는 구현 완료

**학습 내용**
- `@Service`, `@Transactional` 어노테이션
- 의존성 주입 (Constructor Injection)
- 비즈니스 로직에서 예외 처리

**실습 과제**
1. `signUp(SignUpRequest request)` 메서드 구현
   - 이메일 중복 검사
   - 비밀번호 암호화
   - User 엔티티 생성 및 저장
2. 중복 이메일 시 예외 발생시키기

---

### 1.3 AuthController 구현

**학습 목표**: REST API 엔드포인트 작성

**현재 상태**
- `AuthController.java`가 빈 클래스로 존재
- `SignUpResponse.java`가 빈 클래스로 존재

**학습 내용**
- `@RestController`, `@RequestMapping`
- `@Valid`를 통한 요청 검증
- `ResponseEntity` 사용법

**실습 과제**
1. `SignUpResponse` DTO 작성 (id, message 필드)
2. `POST /api/auth/signup` 엔드포인트 구현
3. Postman 또는 curl로 회원가입 테스트

---

### 1.4 DuplicateEmailException 구현

**학습 목표**: 커스텀 예외 클래스 작성

**현재 상태**
- `DuplicateEmailException.java`가 빈 클래스로 존재
- `GlobalExceptionHandler`에서 처리 로직 없음

**학습 내용**
- RuntimeException 상속
- `@ExceptionHandler`로 예외 처리

**실습 과제**
1. `DuplicateEmailException` 구현 (RuntimeException 상속)
2. `GlobalExceptionHandler`에 핸들러 추가
3. HTTP 409 Conflict 응답 반환

---

## Phase 2: 코드 품질 개선

### 2.1 BaseTimeEntity 구현 (JPA Auditing)

**학습 목표**: 엔티티의 생성/수정 시간 자동 관리

**현재 상태**
- `BaseTimeEntity.java`가 빈 클래스로 존재
- `Todo`, `User`가 BaseTimeEntity 상속 중이나 동작 안 함

**학습 내용**
- `@MappedSuperclass`
- `@CreatedDate`, `@LastModifiedDate`
- `@EnableJpaAuditing` 설정

**실습 과제**
1. `BaseTimeEntity`에 createdAt, modifiedAt 필드 추가
2. Application 클래스에 `@EnableJpaAuditing` 추가
3. Todo 생성 시 createdAt 자동 설정 확인

---

### 2.2 커스텀 예외 계층 설계

**학습 목표**: 체계적인 예외 처리 구조 만들기

**현재 상태**
- GlobalExceptionHandler가 ErrorCode를 참조하나 없음
- 예외마다 일관성 없는 처리

**학습 내용**
- ErrorCode Enum 패턴
- 추상 BusinessException 클래스
- 예외 계층 구조 설계

**실습 과제**
1. `ErrorCode` enum 생성 (HTTP 상태, 코드, 메시지)
2. `BusinessException` 추상 클래스 생성
3. `DuplicateEmailException`이 BusinessException 상속하도록 수정
4. GlobalExceptionHandler 개선

---

### 2.3 로깅 추가

**학습 목표**: 효과적인 로깅 전략 수립

**현재 상태**
- TodoService에 로깅 있음
- UserService, AuthController에 로깅 없음

**학습 내용**
- 로그 레벨 (ERROR, WARN, INFO, DEBUG)
- `@Slf4j` 어노테이션
- 적절한 로그 메시지 작성

**실습 과제**
1. UserService에 `@Slf4j` 추가
2. 회원가입 요청/완료/실패 로그 추가
3. application.yml에 로깅 설정

---

## Phase 3: 테스트 작성

### 3.1 Service 단위 테스트

**학습 목표**: Mockito를 활용한 단위 테스트 작성

**현재 상태**
- 통합 테스트만 존재
- Service 레이어 단위 테스트 없음

**학습 내용**
- `@ExtendWith(MockitoExtension.class)`
- `@Mock`, `@InjectMocks`
- `given().willReturn()` 패턴

**실습 과제**
1. `UserServiceTest` 클래스 생성
2. 회원가입 성공 케이스 테스트
3. 중복 이메일 실패 케이스 테스트

---

### 3.2 Controller 테스트

**학습 목표**: MockMvc를 활용한 API 테스트

**현재 상태**
- `AuthControllerTest.java`가 빈 클래스로 존재

**학습 내용**
- `@WebMvcTest` vs `@SpringBootTest`
- `MockMvc` 사용법
- JSON 요청/응답 검증

**실습 과제**
1. `AuthControllerTest` 구현
2. 회원가입 성공 테스트
3. 유효성 검증 실패 테스트
4. 중복 이메일 테스트

---

## Phase 4: 환경 설정

### 4.1 환경별 설정 분리 (Profile)

**학습 목표**: 개발/운영 환경 분리

**현재 상태**
- application.yml 하나만 존재
- ddl-auto: create (운영에 부적합)

**학습 내용**
- Spring Profile 개념
- application-{profile}.yml
- 환경별 설정 차이

**실습 과제**
1. `application-dev.yml` 생성 (H2, SQL 로그)
2. `application-prod.yml` 생성 (보안 설정)
3. Profile 활성화 방법 실습

---

## Phase 5: 보안 (선택)

### 5.1 Spring Security 기초

**학습 목표**: 인증/인가 기본 개념 이해

**학습 내용**
- SecurityFilterChain 설정
- URL별 접근 권한
- CSRF 설정

**실습 과제**
1. spring-boot-starter-security 의존성 추가
2. SecurityConfig 작성
3. 회원가입 API는 허용, 나머지는 인증 필요

---

### 5.2 JWT 인증 구현

**학습 목표**: 토큰 기반 인증 구현

**학습 내용**
- JWT 구조 (Header.Payload.Signature)
- 토큰 생성/검증
- JwtAuthenticationFilter

**실습 과제**
1. 로그인 API 구현
2. JwtTokenProvider 구현
3. 인증 필터 적용

---

## Phase 6: 고급 주제 (선택)

### 6.1 API 문서화 (Swagger)

**실습 과제**
1. springdoc-openapi 의존성 추가
2. Controller에 `@Operation`, `@Tag` 어노테이션
3. Swagger UI 접속 확인

---

### 6.2 공통 응답 형식

**실습 과제**
1. `ApiResponse<T>` 제네릭 클래스 생성
2. 모든 응답을 ApiResponse로 래핑

---

## 학습 순서 권장

| 순서 | Phase | 난이도 | 예상 소요 |
|:----:|-------|:------:|:--------:|
| 1 | 1.1 비밀번호 암호화 | ★☆☆☆☆ | 30분 |
| 2 | 1.2 UserService | ★★☆☆☆ | 1시간 |
| 3 | 1.3 AuthController | ★★☆☆☆ | 30분 |
| 4 | 1.4 DuplicateEmailException | ★☆☆☆☆ | 30분 |
| 5 | 2.1 BaseTimeEntity | ★★☆☆☆ | 30분 |
| 6 | 2.2 커스텀 예외 계층 | ★★★☆☆ | 1시간 |
| 7 | 2.3 로깅 | ★☆☆☆☆ | 30분 |
| 8 | 3.1 Service 단위 테스트 | ★★★☆☆ | 1시간 |
| 9 | 3.2 Controller 테스트 | ★★☆☆☆ | 1시간 |
| 10 | 4.1 환경 설정 분리 | ★☆☆☆☆ | 30분 |
| 11 | 5.1 Security 기초 | ★★★☆☆ | 2시간 |
| 12 | 5.2 JWT 인증 | ★★★★☆ | 3시간 |

---

## 체크리스트

### Phase 1: 회원가입 기능 완성
- [ ] 1.1 비밀번호 암호화 설정
- [ ] 1.2 UserService 구현
- [ ] 1.3 AuthController 구현
- [ ] 1.4 DuplicateEmailException 구현

### Phase 2: 코드 품질 개선
- [ ] 2.1 BaseTimeEntity (JPA Auditing)
- [ ] 2.2 커스텀 예외 계층 설계
- [ ] 2.3 로깅 추가

### Phase 3: 테스트 작성
- [ ] 3.1 Service 단위 테스트
- [ ] 3.2 Controller 테스트

### Phase 4: 환경 설정
- [ ] 4.1 환경별 설정 분리

### Phase 5: 보안 (선택)
- [ ] 5.1 Spring Security 기초
- [ ] 5.2 JWT 인증 구현

### Phase 6: 고급 주제 (선택)
- [ ] 6.1 API 문서화
- [ ] 6.2 공통 응답 형식

---

## 참고 자료

- [Spring Boot 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA 레퍼런스](https://docs.spring.io/spring-data/jpa/reference/jpa.html)
- [Baeldung Spring 튜토리얼](https://www.baeldung.com/spring-tutorial)
