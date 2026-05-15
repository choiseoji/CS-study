## Q. HandlerMapping 과 HandlerAdapter 역할의 차이가 무엇인가요?

HandlerMapping은 요청 URL과 매칭되는 Handler(Controller)를 찾아주는 역할이고, HandlerAdapter는 해당 Handler를 실제로 실행하는 역할입니다.

Spring MVC는 HandlerMapping과 HandlerAdapter를 분리된 구조로 설계하고 있으며, 이를 어댑터 패턴으로 구현하여 DispatcherServlet이 특정 Handler 구현 방식에 의존하지 않고 다양한 형태의 Handler를 일관된 방식으로 처리할 수 있도록 합니다.

</br>
</br>

### 분리 배경

Spring MVC에는 다양한 컨트롤러 타입이 존재합니다.

</br>

1. `@Controller` 어노테이션 기반 컨트롤러
    - 메서드를 리플렉션으로 찾아 실행
    - ModelAndView 반환
    
    ```java
    @Controller
    public class MyController {
    
        @RequestMapping("/myPath")
        public ModelAndView handle() {
            // 비즈니스 로직
            return new ModelAndView("viewName");
        }
    }
    ```
    
2. HttpRequestHandler 기반 컨트롤러
    - handleRequest() 직접 호출
    - 반환값 없음
    
    ```java
    public class MyHttpRequestHandler implements HttpRequestHandler {
    
        @Override
        public void handleRequest(HttpServletRequest request,
                                  HttpServletResponse response) {
            // 서블릿 API 직접 활용
        }
    }
    ```
    

이렇게 Handler마다 호출 방식과 결과 처리 방식이 다르기 때문에, DispatcherServlet이 이를 직접 처리하면 조건 분기와 결합도가 증가한다.

또한 새로운 Handler 타입이 추가될 때마다 DispatcherServlet을 수정해야 하는 구조가 되기 때문에 확장성이 떨어진다.

</br>

### 어댑터 패턴으로 해결

이 문제를 해결하기 위해 Spring MVC는 어댑터 패턴을 사용하여 다음과 같이 역할을 분리한다.

- HandlerMapping: 요청에 맞는 Handler 객체를 찾는 역할
- HandlerAdapter: 해당 Handler를 실행 가능한 방식으로 어댑팅하여 호출하는 역할

이를 통해 DispatcherServlet은 특정 Handler 구현 방식에 의존하지 않고, 일관된 방식으로 요청을 처리할 수 있다.

```java
public class MyControllerHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return handler instanceof MyController;
    }

    @Override
    public ModelAndView handle(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler) {
        return ((MyController) handler).handle();
    }

    @Override
    public long getLastModified(HttpServletRequest request,
                                Object handler) {
        return -1;
    }
}
```

- HandlerMapping이 적절한 Controller 객체 반환
- 여러 Handler Adapter 중 supports() == true인 어댑터 선택
- 해당 어댑터의 handle() 호출

이 과정을 통해 DispatcherServlet은 Handler의 구체적인 타입이나 호출 방식에 의존하지 않고 요청 처리를 일관되게 조율할 수 있다.

</br>

**만약 새로운 컨트롤러 타입이 추가된다면?**

새로운 컨트롤러 타입이 추가되더라도, HandlerAdapter만 추가하면 DispatcherServlet 수정 없이 확장할 수 있다!

</br>

**추가**

### 어댑터 패턴이란??

어댑터 패턴은 호환되지 않는 인터페이스를 중간에서 변환하여 서로 함께 동작할 수 있도록 해주는 디자인 패턴이다.

즉, 기존에 이미 존재하는 클래스(Adaptee)를 수정하지 않고, 클라이언트가 기대하는 인터페이스(Target)에 맞게 감싸서 변환(Adapter)하는 구조이다.

</br>

**구성 요소**

- **Target**: 클라이언트가 기대하는 인터페이스
- **Adaptee**: 기존에 존재하지만 호환되지 않는 클래스
- **Adapter**: Adaptee를 Target 인터페이스에 맞게 변환

예를 들어, 이름은 runLegacyLogic이고, 반환 타입이 문자열인 메서드가 있다고 가정

```java
class LegacyService {

    public String runLegacyLogic() {
        return "home";
    }
}
```

클라이언트는 handle()이라는 동일한 인터페이스를 사용하고, 반환 타입도 ModelAndView로 통일된 형태를 기대한다.

```java
interface Handler {

    ModelAndView handle();
}
```

이때 Adapter는 기존 클래스를 감싸서 클라이언트가 기대하는 형태로 변환해준다.

→ 서로 다른 인터페이스를 호환 가능하게 만들어, 클라이언트는 동일한 방식으로 사용할 수 있게 된다.

```java
class LegacyServiceAdapter implements Handler {

    private final LegacyService legacyService;

    public LegacyServiceAdapter(LegacyService legacyService) {
        this.legacyService = legacyService;
    }

    @Override
    public ModelAndView handle() {

        // 1. 기존 인터페이스 호출
        String viewName = legacyService.runLegacyLogic();

        // 2. 결과를 새로운 형태로 변환
        ModelAndView mv = new ModelAndView();
        mv.setViewName(viewName);

        return mv;
    }
}
```

</br>

**참고 자료**

[Spring MVC에서 HandlerMapping과 HandlerAdapter를 나눈 이유](https://stonehee99.tistory.com/24)