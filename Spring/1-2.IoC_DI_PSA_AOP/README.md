# IoC/DI, PSA, AOP

> - 세 요소 모두 관심사의 분리(Separation of Concerns)라는 한 목적 — 객체와 기술 간 결합도 낮추기
> - IoC/DI: 객체 생성·조립을 컨테이너가 담당
> - PSA: 외부 기술(JDBC/JPA, 캐시, 메시징) 추상화
> - AOP: 횡단 관심사(트랜잭션·보안·로깅)를 프록시 패턴으로 분리

Spring이 흔히 "3대 요소"로 거론하는 IoC/DI, PSA, AOP는 모두 객체와 기술 사이의 결합도를 낮춰, 변경과 테스트가 쉬운 코드를 만든다는 것이다.

|   요소   |       한 줄 정의        |         추구하는 것         |
|:------:|:-------------------:|:----------------------:|
| IoC/DI | 객체 생성과 조립을 컨테이너가 담당 | 객체 간 결합도 ↓, 테스트 용이성 ↑  |
|  PSA   | 외부 기술을 일관된 추상으로 감쌈  | 기술 종속성 ↓, 구현체 교체 비용 ↓  |
|  AOP   |   횡단 관심사를 프록시로 분리   | 중복 제거, 핵심 로직과 부가 로직 분리 |

## IoC / DI

객체의 생명주기와 의존성 조립을 개발자가 아닌 컨테이너가 담당하는 방식이다.

- IoC(Inversion of Control, 제어의 역전): 객체가 직접 의존성을 만들지 않고 외부에 맡기는 방식
- DI(Dependency Injection, 의존성 주입): 생성자/세터/필드로 의존성을 외부에서 주입

```java
// 일반적 객체 생성 - 호출자가 의존성을 직접 생성
class OrderService {

    private final PaymentClient paymentClient = new PaymentClient(...);
}

// IoC/DI - 컨테이너가 주입
class OrderService {

    private final PaymentClient paymentClient;

    OrderService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }
}
```

- 객체가 만들어지는 시점과 사용되는 시점 분리
- 인터페이스에 의존하므로 구현체 교체가 자유로움
- 테스트에서 Mock 구현체를 주입해 외부 의존 없이 검증 가능

## PSA

PSA(Portable Service Abstraction)의 핵심 단어는 Portable(이식 가능)이다.

```java
// @Transactional은 JDBC, JPA, Hibernate, JTA 어디서나 동일하게 동작
@Service
class OrderService {

    @Transactional
    void place(Order order) {
        // 데이터 접근 기술이 무엇이든 같은 트랜잭션 의미가 보장됨
    }
}
```

대표적인 PSA 추상화로는 당므과 같은 것이 존재한다.

- 트랜잭션: `PlatformTransactionManager`
    - `DataSourceTransactionManager`
    - `JpaTransactionManager`
    - `JtaTransactionManager`
- 캐시: `CacheManager`
    - `ConcurrentMapCacheManager`
    - `RedisCacheManager`
    - `CaffeineCacheManager`

결과적으로 다음과 같은 것을 얻게 된다.

- 애플리케이션 코드가 특정 벤더의 API에 의존하지 않음
- 기술 교체 시 구성만 바꾸는 방식으로 교체 (예: Hibernate → MyBatis)
- 표준이 없는 도메인에 표준을 만들어 일관성 확보

## AOP

핵심 비즈니스 로직과 무관하지만 여러 곳에서 반복되는 횡단 관심사(Cross-Cutting Concern)를 별도 모듈로 분리하는 기법이다.

```java

@Aspect
@Component
class TimingAspect {

    @Around("execution(* com.example.service..*(..))")
    Object measure(ProceedingJoinPoint pjp) throws Throwable {
        long t = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            log.info("{} took {}ms", pjp.getSignature(), System.currentTimeMillis() - t);
        }
    }
}
```

대표적인 적용 대상으로 트랜잭션, 보안, 로깅, 캐시, 재시도, 메트릭 수집이 있으며, AOP로 분리하면 다음과 같은 장점이 있다.

- 부가 로직과 핵심 로직의 분리 → 핵심 로직이 비즈니스 규칙만 표현
- 동일 부가 기능의 중복 제거
- 선언적 프로그래밍 (`@Transactional` 한 줄로 트랜잭션 경계 정의)

## 세 요소가 함께 작동하는 흐름

```mermaid
flowchart LR
    A[Controller] -->|호출| B["Service Proxy\n(AOP)"]
    B -->|@Transactional 위임\n = PSA| C["TransactionManager\n(JPA 구현체)"]
    B -->|핵심 로직 호출| D[Service Bean]
    D -->|DI로 주입| E[Repository Bean]
    classDef di fill: #4a9eff,color: #000
    classDef aop fill: #f5a623,color: #000
    classDef psa fill: #50c878,color: #000
    class D,E di
    class B aop
    class C psa
```

- IoC/DI가 객체들을 조립
- 그 위에 AOP 프록시가 씌워져 부가 로직을 가로챔
- PSA가 외부 기술을 일관 API로 감싸므로 부가 로직이 기술과 무관
- 결과적으로 핵심 로직은 비즈니스 규칙만 담고, 나머지는 모두 외부 협력으로 위임
