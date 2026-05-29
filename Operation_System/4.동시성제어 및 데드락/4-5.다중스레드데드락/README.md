## Spring Data JPA 환경에서 다중 스레드가 동일한 DB 레코드들을 동시에 업데이트할 때 발생하는 데드락 상황을 방지하기 위한 애플리케이션 레벨의 전략은 무엇인가요?
> 다중 스레드 환경에서 동일한 DB 레코드들을 업데이트할 때는 락을 오래 잡지 않게 하고, 락 획득 순서를 통일하는 것이 핵심입니다.
>
>먼저 트랜잭션 범위를 줄여야 합니다. DB row를 수정한 뒤 외부 API 호출이나 알림 발송까지 같은 트랜잭션 안에서 처리하면 락 유지 시간이 길어져 데드락 가능성이 커집니다.
>
>두 번째로, 여러 레코드를 수정할 때는 항상 같은 순서로 접근해야 합니다. 예를 들어 상품 ID를 오름차순으로 정렬한 뒤 업데이트하면 트랜잭션마다 락을 잡는 순서가 같아져 순환 대기를 줄일 수 있습니다.
>
>마지막으로 데드락은 완전히 막기 어렵기 때문에, 발생 시 Spring Retry로 짧은 backoff 후 재시도하도록 처리합니다. 다만 외부 API처럼 중복 실행되면 안 되는 로직은 재시도 범위에서 분리해야 합니다.
>
>MSA처럼 여러 서버에서 동시에 접근한다면 Redis 분산 락이나 메시지 큐로 같은 자원에 대한 요청만 순차 처리하는 방식도 사용할 수 있습니다.
 
### 1. 올바른 트랜잭션 설정 및 스레드 정책
![](https://oliveyoung.tech/static/79aec3125b29bd4c798c7da6b8d99afc/28d16/multi-thread2.webp)
```java
public class SomeService {
    // @Transactional 없음
    public void processBusinessLogic() {
        // 비즈니스 로직
    }
}
```

멀티 스레드 환경에서 CallerRunsPolicy 정책 설정: 메인 스레드에서 작업을 수행

![](https://oliveyoung.tech/static/d9c39068157816a70e489ade6e23648e/a9c0c/multi-thread3.webp)
신규 스레드가 메인 스레드에서 아직 커밋하지 않은 데이터를 수정하려고 하면, 메인 스레드가 커밋할때까지 대기

그러나 메인 스레드는 모든 신규 스레드가 종료되어야만 커밋이 가능하므로, 신규 스레드(작업)가 하나라도 살아 있는 이상 절대로 커밋할 수 없음

=> Propagation.REQUIRES_NEW로 제대로 분리해 설정 필요 or 메인 스레드 작업 방지를 위한 스레드 수 조정 및 큐 길이 재설정

#### 트랜잭션 범위 축소
트랜잭션 범위가 줄어들면 DB 락을 잡고 있는 시간이 줄어들게 됨!

```java
@Transactional
public void order() {
    Order order = orderRepository.save(order);

    stockService.decreaseStock(order);

    paymentClient.requestPayment(order); // 외부 API 호출

    notificationService.send(order);
}
```

product 수정 후 외부 PG API 호출하면 트랜잭션이 끝나기 전까지 product row의 락이 계속 유지

이렇게 락이 필요 이상으로 오래 유지괴면 다른 row와 얽히면서 데드락 가능성 증가

```java
public void order() {
    Order order = createOrder();

    paymentClient.requestPayment(order);

    sendNotification(order);
}

@Transactional
public Order createOrder() {
    Order order = orderRepository.save(order);
    stockService.decreaseStock(order);
    return order;
}
```
### 2. 자원 접근 순서 동일화
여러 트랜잭션이 여러 개의 DB 레코드나 테이블을 수정할 때, 항상 같은 순서로 접근하도록 만드는 전략

```java
List<Long> sortedProductIds = productIds.stream()
        .distinct()
        .sorted()
        .toList();
```
어떤 요청이 들어와도 항상 같은 순서로 락을 잡게 됨

이 방식은 여러 row를 동시에 수정하는 경우 효과 좋음

그러나...

단일 row에 트래픽이 몰리거나 여러 테이블 함께 수정하는 경우, 너무 많은 row가 잠기게 되는 경우, 데드락이 드물게 발생하는 경우 추가 전략 고안 필요

### 3. 데드락 재시도 로직
DB가 데드락을 감지해서 한 트랜잭션을 롤백시켰을 때, 애플리케이션에서 안전하게 다시 실행하는 복구 전략

데드락을 완전히 0으로 만들기 어렵기 때문에 복구를 위한 전략으로 사용

#### Spring Retry
cf) spring batch는 이미 spring retry 포함하고 있어서 의존성 추가 필요 없음
```gradle
implementation 'org.springframework.retry:spring-retry'
implementation 'org.springframework:spring-aspects'
```

```java
@Service
@RequiredArgsConstructor
public class StockRetryFacade {

    private final StockService stockService;

    @Retryable(
            retryFor = {
                    DeadlockLoserDataAccessException.class,
                    CannotAcquireLockException.class,
                    PessimisticLockingFailureException.class,
                    OptimisticLockingFailureException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2, random = true)
    )
    public void decreaseStock(Long productId, int quantity) {
        stockService.decreaseStock(productId, quantity);
    }

    @Recover
    public void recover(Exception e, Long productId, int quantity) {
        throw new IllegalStateException("재고 차감 재시도 실패", e);
    }
}
```
1. retryFor

    이 예외들이 발생하면 재시도하겠다는 뜻 / Spring Retry의 @Retryable은 retryFor로 재시도 대상 예외 타입을 지정 가능
2. maxAttempts = 3

    최대 시도 횟수 = (최초 실행 1번 + 재시도 최대 2번)
3. backoff = @Backoff(...)

    delay = 100은 100ms 기다림
    
    multiplier = 2는 재시도할 때마다 대기 시간이 2배씩 증가

    random = true는 대기 시간에 랜덤 요소 추가해서 여러 트랜잭션이 동시에 재시도하는 상황에서 충돌 가능성 줄임
```java
@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;

    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow();

        product.decreaseStock(quantity);
    }
}
```
- 주의사항
    
    데드락 때문에 DB 저장이 실패해서 retry 했는데 외부 API가 또 호출되면서 중복으로 외부 API가 실행되는 상황 발생할 수 있음
    
    그래서 retry할 대상을 적절히... 선택하는 것 중요!

### 4. 분산 락 활용
여러 WAS 인스턴스나 여러 서버에서 동시에 같은 자원에 접근할 때, Redis 같은 외부 저장소를 기준으로 락을 잡아 하나의 요청만 처리하게 만드는 방식

```text
분산 락은 DB에 동시에 진입하는 요청 수를 줄여서
DB row lock 경합과 데드락 가능성을 낮추는 전략이다.

하지만 DB 내부에서 발생하는 모든 데드락을 근본적으로 제거하는 방법은 아님!!
```

여러 스레드가 동시에 여러 자원을 요청한다고 했을 때, 요청 순서에 따라 데드락 발생 가능

이때 분산락을 사용하면 하나의 스레드만 DB 트랜잭션에 진입할 수 있게 됨


=> 같은 key에 대한 처리에 한해서 병렬 처리 장점 줄어 듦...

#### 메시지 큐 / 순차 처리 방식
동시에 들어오는 요청을 바로 DB에 업데이트하지 않고, 큐에 넣은 뒤 Consumer가 순서대로 처리하는 방식

메시지 큐를 쓰면 같은 자원에 대한 업데이트를 하나씩 처리 가능

=> 같은 key만 순차 처리

같은 자원에 대해서만 병렬성을 일부 포기하고 정합성을 얻는 방식

-----------------------

### MySQL의 잠금 메커니즘과 JPA 쓰기 지연으로 인한 데드락
- AggregateRoot와 Domain Event
- 누적 쿼리
- JPQL + 낙관 락
- 비관락 (@Lock(LockModeType.PESSIMISTIC_WRITE))
- flush()


https://oliveyoung.tech/2024-11-06/who-caused-our-batch-to-stop/
https://lincoding.tistory.com/130
https://mystudylog.tistory.com/202
https://f-lab.kr/insight/database-lock-and-deadlock-20260324