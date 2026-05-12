## Q.Servlet 이란?

Servlet이란 자바에서 HTTP 요청을 처리하기 위한 서버 측 자바 프로그램입니다.
서블릿 컨테이너에 의해 생성 및 관리되며, 클라이언트의 요청을 받아 응답을 생성하는 역할을 합니다.

</br>
</br>

**💡 Servlet이란?**

- **자바에서 HTTP 요청을 처리하기 위한 서버 측 자바 프로그램**
- 서블릿 컨테이너에 의해 생성-관리되는 자바 객체이며, **클라이언트의 요청을 받고 응답을 생성하는 역할**

</br>

**과거에는 웹 요청마다 Servlet이 직접 요청 처리**

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) {

        response.getWriter().write("hello");
    }
}
```

- 브라우저가 `/hello` 요청
→ 톰캣이 요청 받음
→ 해당 Servlet 실행
→ 브라우저에 반환
- HelloServlet 클래스가 하나의 Servlet이다.

</br>

**서블릿의 특징**

- HTTP 요청/응답 처리
    - GET : doGet()
    - POST : doPost()
- Servlet은 직접 실행되는 것이 아니라 서블릿 컨테이너에 의해 관리된다.
- 대표적인 서블릿 컨테이너:
    - Apache Tomcat
    - Jetty

</br>

**스프링 MVC와의 관계**

- 스프링 MVC도 Servlet 기반으로 동작한다.
- Dispatcher Servlet 역시 하나의 Servlet이며, 스프링 MVC의 프론트 컨트롤러 역할을 수행한다.