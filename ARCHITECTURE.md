ARCHITECTURE.md
목표
프로젝트가 커져도 유지보수와 확장이 가능하도록, 코드의 책임을 명확히 나누고 의존성 방향을 통제한다.

특히 “비즈니스 흐름이 읽히는 코드”를 상위 레이어에 두고, DB/외부연동 등 기술 변화에 민감한 상세 구현은 하위 레이어로 격리한다.

레이어 개요
본 프로젝트는 아래 4개 레이어를 사용한다.

* Presentation Layer
* Business Layer
* Implement Layer
* Data Access Layer 
Presentation Layer
  외부로 “노출/요청/응답”을 담당한다.

예: Controller, Request/Response DTO, API 스펙에 종속되는 검증/바인딩 코드.

Business Layer
유스케이스 중심으로 “비즈니스 흐름(단계)이 읽히는 코드”를 배치한다.

예: 회원가입, 결제, 주문 생성처럼 작업의 큰 단계를 오케스트레이션하며, 상세 구현은 Implement/Data Access로 위임한다.

Implement Layer
Business가 위임한 “상세 구현 로직”을 담당한다.

도메인 규칙을 적용하기 위한 도구/컴포넌트(검증, 조합, 계산, 정책, 변환 등)가 주로 위치하며, 프로젝트에서 클래스 수가 가장 많아지기 쉬운 레이어다.

Data Access Layer
DB/외부 시스템 접근을 담당한다.

예: Repository/DAO, 외부 API client, 메시징/캐시/파일 등 인프라 접근 코드.

의존성(참조) 규칙
레이어의 목적은 “분리”보다 “통제”이며, 아래 규칙을 강제한다.

* 의존성 방향은 위 → 아래만 허용한다. 
* 하위 레이어는 상위 레이어를 참조하면 안 된다. 
* 상위 레이어가 중간 레이어를 건너뛰어 하위 레이어를 직접 참조하면 안 된다. 
* 같은 레이어끼리 참조는 원칙적으로 금지한다. 
* 예외: Implement Layer는 협력/재사용을 위해 Implement 내부 상호 참조를 허용한다. 
설계 의도(왜 이렇게 나누나)
                                                                                                                           Service에 로직이 과도하게 몰리면 “비즈니스 흐름”이 사라지고, Repository/기술 세부사항을 너무 많이 알게 되어 변경에 취약해진다.

그래서 Business는 “흐름”, Implement는 “상세 구현”, Data Access는 “기술 의존성”으로 책임을 나눠 변경 이유를 분리한다.

패키지(또는 모듈) 가이드
아래는 단일 모듈 기준의 패키지 예시다(멀티모듈이면 각 레이어를 모듈로 분리 가능).

* ...presentation
    * ...controller
    * ...dto (request/response)
* ...business
    * ...usecase (또는 service)
* ...implement
    * ...policy, ...validator, ...processor, ...factory 등 “상세 구현 컴포넌트”
* ...dataaccess
    * ...repository (JPA 등)
    * ...client (외부 API)
    * ...config (인프라 설정) 
모듈화(선택)
      Data Access처럼 기술 의존성이 강한 영역은 별도 모듈로 분리하고, Gradle 의존성 설정에서 구현체가 상위로 전파되지 않도록 관리한다.

기술 모듈 의존성이 상위로 새어나가면 상위 레이어가 특정 라이브러리/구현체를 직접 참조하게 되어 레이어 오염이 발생할 수 있다.

코드 리뷰 체크리스트
* 이 코드는 어느 레이어에 있어야 하는가? (외부 입출력/흐름/상세구현/데이터접근) 
* 상위 레이어가 하위를 “건너뛰어” 참조하고 있지 않은가? 
* Data Access/외부 연동 타입(JPA Entity/Repository/Client 등)이 Business에 노출되고 있지 않은가? 
* Implement 내부 상호 참조가 과도해져 “새로운 거대한 레이어”가 되지 않았는가? 
