## Q. Dispatcher Servlet 의 다중 요청 동시 처리 방식에 대해 설명해주세요

DispatcherServlet은 상태를 가지지 않는 싱글톤 서블릿으로 동작하며, 여러 요청을 각각의 스레드에서 처리하도록 설계되어 있습니다.

클라이언트 요청이 들어오면 Tomcat과 같은 서블릿 컨테이너가 요청마다 별도의 스레드를 생성하고, 그 스레드가 DispatcherServlet의 service 메서드를 호출하여 요청을 처리합니다.

따라서 하나의 DispatcherServlet 인스턴스가 여러 스레드에 의해 동시에 호출되지만, 요청별로 독립적인 스레드에서 처리되기 때문에 동시 처리가 가능합니다.

</br>
</br>

### 동작 과정

![Tomcat.png](Tomcat.png)

- 클라이언트 요청이 들어오면 Tomcat과 같은 서블릿 컨테이너가 요청마다 스레드를 할당
- 해당 스레드가 DispatcherServlet의 service() 메서드를 호출

(DispatcherServlet은 하나의 인스턴스로 관리되며(싱글톤), 여러 스레드가 동시에 이 객체를 공유하여 요청을 처리한다)

</br>

**Thread Pool 관리**

- Tomcat은 스레드 풀에서 스레드를 꺼내 쓰도록 동작한다.
    - 스레드 생성 비용이 비싸기 때문에 요청마다 생성 X, 미리 만들어두고 재사용하는 구조
- Blocking 구조 → 요청 1개 처리 하는 동안 thread 1개 점유라서 동시 요청이 많은 상황이면 thread 부족 가능성 있음
- thead 부족하면 → 요청 대기 큐로 이동, 대기 큐도 가득 차면 요청 거절

</br>

**Thread Safe 주의점**

Controller에서 멤버 변수 사용하면 여러 스레드가 값을 덮어 쓸 수 있음

```java
@RestController
public class OrderController {

    // 멤버 변수 (인스턴스 변수): 모든 스레드가 이 변수를 공유함
    private String lastOrderProduct; 

    @GetMapping("/order")
    public String order(String product) {
        this.lastOrderProduct = product; // 1. 스레드 A가 "아이폰" 저장
        
        // 2. (비즈니스 로직 수행 중이라고 가정 - 시간 지연)
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 3. 그 사이 스레드 B가 들어와서 "갤럭시"로 덮어씀
        
        return "주문 완료: " + this.lastOrderProduct; // 4. 스레드 A는 "아이폰"을 기대했지만 "갤럭시"를 응답받음
    }
}
```

→ 그래서 지역 변수나 파라미터로 값을 전달받아 무상태성을 유지해야 함!!

</br>

**참고 자료**

[[Spring] Dispatcher Servlet 📌](https://yummy0102.tistory.com/552)