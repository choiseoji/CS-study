## Q. Servlet Context와 Application Context 의 차이가 무엇인가요?

ServletContext는 웹 애플리케이션 전체에서 공유되는 저장소로, 서블릿/JSP/필터 등 웹 컴포넌트들이 공통으로 사용하는 전역 컨텍스트입니다.

ApplicationContext는 Spring에서 Bean의 생성, 의존성 주입, 라이프사이클을 관리하는 IoC 컨테이너입니다.

Spring MVC에서는 ServletContext 위에 ApplicationContext를 계층적으로 구성하여 웹 환경과 Spring의 객체 관리 책임을 분리하고, 유연한 구조로 사용하고 있습니다.

</br>
</br>

**먼저 Context 란??**

특정 기능을 수행하기 위해 필요한 객체와 설정 정보를 관리하는 **공간** 입니다.

(컨테이너와 헷갈리면 안됩니다! 컨테이너는 객체를 생성하고 관리하는 주체를 말합니다.)

</br>

1. **Servlet Context = 웹 애플리케이션 실행을 위한 공용 공간**

Tomcat 같은 WAS가 웹 애플리케이션 실행 시 생성하는 객체입니다.

즉, Servlet/Tomcat 영역의 개념입니다. (Tomcat이 서블릿 컨테이너이니 컨테이너 안에 설정 정보를 관리하는 공간을 서블릿 컨텍스트로 이해하면 된다)

아래와 같은 웹 애플리케이션의 실행 환경 정보를 관리합니다.

- 서블릿 등록 정보
- 공통 데이터
- 파일 경로
- 세션 기반 환경

</br>

2. **Application Context = Spring 객체들을 관리하는 공간**

Spring이 생성하는 IoC 컨테이너입니다.

즉, Spring 영역의 개념입니다.

Spring Bean을 생성하고 관리하며 아래와 같은 기능을 제공합니다.

- DI(의존성 주입)
- AOP
- 트랜잭션 관리
- Bean 생명주기 관리

```java
@Service
public class MemberService {
}
```

예를 들어, 우리가 이런 MemberService를 만들어 Bean 등록을 했다면 Application Context에서 관리한다.

SpringMVC에서는 아래와 같은 구조를 가지고 있다

→ Servlet Context 내부에서 Application Context가 동작

```
Tomcat
 └─ ServletContext
       └─ DispatcherServlet
             └─ ApplicationContext
```

- Tomcat이 ServletContext를 생성하고
- DispatcherServlet이 Spring 진입점 역할을 하며
- 내부적으로 ApplicationContext를 생성해 Spring Bean들을 관리합니다.

</br>

**왜 둘을 분리했을까??**

웹 실행 환경 관리와 객체 관리 책임을 분리하기 위해서이다. (관심사의 분리)

</br>

**Context 부모/자식 관계**

Spring MVC에서는 ApplicationContext를 부모/자식 구조로 분리해서 사용한다.

1. Root Application Context = 공통 비즈니스 bean 관리
    - Service
    - Repository
    - DataSource
2. DispatcherServlet ApplicationContext = 웹 관련 bean 관리
    - Controller
    - HandlerMapping
    - ViewResolver

```
ServletContext
 ├─ Root ApplicationContext (부모)
 └─ DispatcherServlet ApplicationContext (자식)
```

→ 자식 Context는 부모 Context의 Bean을 조회할 수 있음 (Bean Lookup)

```java
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
}
```

→ 이렇게 Controller에서 Service를 호출할 수 있는 이유!

</br>

**Q. BeanFactory와 ApplicationContext의 차이점이 뭔가요?**

Bean Factory는 Spring의 가장 기본적인 IoC 컨테이너로, Bean 생성 및 조회 같은 핵심 기능만 제공합니다.

ApplicationContext는 BeanFactory를 상속한 확장 컨테이너입니다.

Spring 애플리케이션에서 주로 사용하는 컨테이너이며 

- DI(의존성 주입)
- 이벤트 처리
- 메시지 처리(i18n)
- 환경 변수 관리
- AOP 및 트랜잭션 처리 지원

등의 추가 기능을 제공합니다.

</br>

**Q. 왜 부모 Context는 자식 Context에 접근을 못 하나요?**

부모 Context가 자식 Context를 참조하게 되면 비즈니스 계층이 웹 계층에 의존하게 됩니다. 이렇게 되면 계층 구조가 깨지고 결합도가 높아지기 때문에 

- 자식 → 부모 조회 가능
- 부모 → 자식 조회 불가

구조로 설계하여 아키텍처 방향을 유지합니다.