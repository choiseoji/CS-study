## Spring Event에 대해서 간략히 이야기 해주시고 어떨 때 사용하고 주의사항과 본인의 적용 예를 알려주세요.
> Spring Event는 애플리케이션 내부에서 특정 사건이 발생했을 때, 후속 로직을 이벤트 리스너로 분리해서 처리할 수 있게 해주는 기능입니다. 보통 ApplicationEventPublisher로 이벤트를 발행하고, @EventListener나 @TransactionalEventListener로 처리합니다.
>
> 핵심 비즈니스 로직과 부가 로직을 분리하고 싶을 때 사용합니다. 이렇게 하면 서비스 간 의존성을 줄이고, 핵심 로직을 더 단순하게 유지할 수 있습니다.
>
> 다만 Spring Event는 기본적으로 동기 방식으로 동작하기 때문에 비동기 처리가 필요하면 @Async 설정이 필요합니다. 또 트랜잭션이 커밋되기 전에 이벤트가 처리되면, 롤백이 발생했는데도 메일이나 알림이 발송되는 문제가 생길 수 있어서 @TransactionalEventListener(phase = AFTER_COMMIT)을 사용하는 것이 좋습니다.
>
> 제가 적용 했던 사례로는 결제 완료 후 메일 발송이 있습니다. 결제 기능에서는 결제 승인, 결제 내역 저장, 주문 상태 변경이 핵심 로직이고, 결제 완료 메일 발송은 결제 이후 수행되는 부가로직이라 생각했씁니다.
>
> 그래서 결제 처리가 완료되면 PaymentCompletedEvent를 발행하고, 별도의 이벤트 리스너에서 결제 완료 메일을 발송하도록 분리할 수 있습니다. 이때 결제 내역 저장과 주문 상태 변경이 실제로 DB에 커밋된 이후에 메일이 발송되어야 하므로, 일반 @EventListener보다는 @TransactionalEventListener(phase = AFTER_COMMIT)을 사용하는 것이 안전하다고 생각했습니다.

### Spring Event
이벤트를 기반으로 데이터를 주고받는 방법으로, 발행된 이벤트의 전달은 Spring Context가 수행

개발자의 역할: 적절한 곳에서 이벤트를 던지는 것, 적절한 이벤트 리스너를 선언해 발행된 이벤트를 받아 처리하는 것

* 이벤트 발행
  : ApplicationEventPublisher는 이벤트의 발행을 담당하는 인터페이스

    ```java
    @FunctionalInterface
    public interface ApplicationEventPublisher {
     
        default void publishEvent(ApplicationEvent event) {
            publishEvent((Object) event);
        }
     
        // since Spring 4.2
        void publishEvent(Object event);
     
    }
    ```

* 이벤트 구독
  : 이벤트를 구독하기 위해서는 발행된 이벤트를 구독할 리스너 클래스를 만들고 빈으로 등록한 뒤, 이벤트가 발행되었을 때 수행할 메서드의 위에 @EventListener 어노테이션을 달아야함

    ```java
    @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Reflective
    public @interface EventListener {
     
        @AliasFor("value")
        Class<?>[] classes() default {};
     
        String condition() default "";
     
        String id() default "";
     
    }
    ```

  기본 설정의 이벤트 리스너는 동기로 동작 -> 비동기 원한다면 @Async 옵션과 함께 사용

* 트랜잭션 시점에 따른 이벤트 구독
  : 이벤트 리스너가 이벤트의 발행 시점을 기준으로 동작한다면, @TransactionalEventListener는 이벤트를 발행하는 트랜잭션 스코프의 커밋 또는 완료 전후 시점을 기준으로 동작

    ```java
    @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @EventListener
    public @interface TransactionalEventListener {
     
        TransactionPhase phase() default TransactionPhase.AFTER_COMMIT;
     
        boolean fallbackExecution() default false;
     
        // 나머지는 @EventListener와 동일
     
    }
    ```
  **phase**라는 옵션이 존재하는데, 해당 이벤트 리스너가 어떤 시점에 실행될지를 지정 가능

    또한, 트랜잭션 내에서 발행된 이벤트 리스닝이 기본 옵션, 트랜잭션 내에서 발행된 것이 아닌 이벤트 모두 구독하고자 한다면 **fallbackExecution** 옵션을 true로 설정

[phase 옵션]
- AFTER_COMMIT (기본값) - 트랜잭션이 성공적으로 마무리(commit)됬을 때 이벤트 실행
- AFTER_ROLLBACK – 트랜잭션이 rollback 됬을 때 이벤트 실행
- AFTER_COMPLETION – 트랜잭션이 마무리 됐을 때(commit or rollback) 이벤트 실행
- BEFORE_COMMIT - 트랜잭션의 커밋 전에 이벤트 실행

[AFTER_COMMIT 문제]
리스너 코드 안에서 다시 트랜잭션을 처리하면 해당 트랜잭션은 커밋되지 않는 현상이 발생

```java
@Component
@RequiredArgsConstructor
public class SmsEventHandler {
 
    private final AlarmService alarmService;
    private final AlimTalkService alimTalkService;
 
    //    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendFCM(RegisteredEvent event) throws InterruptedException {
        alarmService.send(event.getName());
    }
 
    //    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendAlimTalk(RegisteredEvent event) throws Exception {
        alimTalkService.send(event.getName());
    }
}
 
 
@Service
@RequiredArgsConstructor
@Transactional
public class AlarmService {
    private final AlarmRepository alarmRepository;
    
    // 트랜잭션 commit 안 됨!!!!
    public void send(String name) throws InterruptedException {
        alarmRepository.save(Alarm.builder().createdAt(LocalDateTime.now()).build());
        System.out.println(name + "에게 push 알림 발송");
    }
}
```
이전의 이벤트를 publish 하는 코드에서 트랜잭션이 이미 커밋 되었기 때문에 AFTER_COMMIT 이후에 새로운 트랜잭션을 수행하면 해당 데이터소스 상에서는 트랜잭션을 커밋하지 않는다는 것

따라서 @Transactional 어노테이션을 적용한 코드에서 **PROPAGATION_REQUIRES_NEW** 옵션을 지정하지 않는다면 (매번 새로운 트랜잭션을 열어서 로직을 처리하라는 의미) 이벤트 리스너에서 트랜잭션에 의존한 로직을 실행했을 경우 이 트랜잭션은 커밋되지 않는다

  
[해결방법]

1. AFTER_COMMIT 이후에 동일한 데이터소스를 사용하지 않는 방법
: 이벤트 리스너를 별도의 스레드에서 실행 ex) @Async 어노테이션 추가

```java
@Async
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void sendAlimTalk(RegisteredEvent event) throws Exception {
    alimTalkService.send(event.getName());
}
```
2. @Transactional(propagation = Propagation.REQUIRES_NEW)
: 이벤트 리스너의 로직 안에서 실행되는 @Transactional 로직을 위한 새로운 트랜잭션이 이전의 트랜잭션과 구분되어 새롭게 시작

```java
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendAlimTalk(RegisteredEvent event) throws Exception {
        alimTalkService.send(event.getName());
    }
```
