## 🌿Spring
### 1. Spring Framework 면접 질문 - IoC/DI/AOP

---

1. [스프링이 무엇이고 왜 사용하나요? (스프링과 스프링 부트 차이점)](https://github.com/core-CS/CS-study/tree/main/Spring/1-1.Spring%EC%9D%B4%EB%9E%80)
2. [Spring의 3대 요소인 IoC/DI, PSA, AOP 에 대해 설명해주세요. (+ 추구하는 것, 간략히만)](https://github.com/core-CS/CS-study/tree/main/Spring/1-2.IoC_DI_PSA_AOP)
3. [DIP란 무엇인가요?](https://github.com/core-CS/CS-study/tree/main/Spring/1-3.DIP%EB%9E%80)
4. `@Autowired` 의 작동 방식과 DI 방식의 종류 및 차이와 생성자 주입 권장 이유에 대해 이야기해주세요.
5. 구체 클래스가 하나임에도 Spring bean 을 사용하는 이유는 무엇인가요?
6. Spring Bean 이란 무엇인가요? (Bean 의 생성 주기와 엮어서)
7. Bean 의 Scope 종류에 대해 이야기 해주세요.
8. `@Bean` , `@Configuration` 을 통해 빈을 등록하는 상황은 어떤 상환인가요?
9. AOP에 대해서 설명해주시고 `@Aspect` 동작 방식과 스프링의 프록시를 엮어 이야기해주세요
10. 어노테이션의 작동 원리에 대해 설명해주세요. (Lombok `@Data` 를 지양하는 이유는?)

### 2. Spring Web MVC 및 서버 기술

---

1. Servlet 이란?
2. Dispatcher Servlet 이란? (MVC 동작 방식에 대해 설명해주세요)
3. Dispatcher Servlet 의 다중 요청 동시 처리 방식에 대해 설명해주세요
4. Apache Tomcat 이란?
5. Application Context 와 Servlet Context 의 차이가 무엇인가요?
6. HandlerMapping 과 HandlerAdapter 역할의 차이가 무엇인가요?
7. Filter 과 intercepter 어떤 차이가 있고 언제 사용하나요?
8. ArgumentResolver 란 무엇인가요?
9. `@RequestParam`, `@RequestBody` , `@ModelAndAttribute` 의 차이
10. `@Valid` 어노테이션과 동작 위치를 설명해주세요.

### 3. Spring Security 및 기타 기능

---

1. Spring Security 란?
2. Spring 인증 구현 시 Security 를 사용하는 이유에 대해서 알려주세요.
3. Spring Event에 대해서 간략히 이야기 해주시고 어떨 때 사용하고 주의사항과 본인의 적용 예를 알려주세요.

### 4. JPA

---

1. ORM 이란 무엇이고 장단점이 무엇인가요?
2. JPA와 Hibernate 에 대해 이야기 해주세요. 그리고 JPA 를 왜 사용하나요?
3. 영속성은 어떤 기능을 하나요? 이게 진짜 성능 향상에 큰 도움이 되나요?
4. 영속성 컨텐스트의 내용을 데이터 베이스에 반영하려면 어떻게 해야 하나요?
5. JPQL 쿼리를 실행 시 왜 플러시 되나요?
6. Flush 와 Commit 의 차이는 무엇인가요?
7. Dirty Checking 에 대해 설명해주세요.
8. 스프링의 `@Transactional` 에 대해서 알려주세요.
9. Entity 의 생명 주기에 대해 설명해주세요.
10. EntityManager 는 여러 스레드에서 공유하나요?
11. Eager Loading 에 대해 설명해주세요. (+ 장단점)
12. Lazy Loading 에 대해 설명해주세요. (+ 장단점)
13. Eager Loading/Lazy Loading 두 가지에 대해 사용 시 주의점이 무엇인가요?
14. FetchType.Eager 과 FetchType.LAZY 의 차이점은 무엇인가요?
15. **Fetch Join 과 Limit 를 같이 사용하면 어떤 문제가 발생하나요? → 여기서부터 지민님**
16. N+1 발생 원인에 대해 설명해 주세요.
17. N+1 해결 방안에 대해 설명해주세요. (다양하게)
18. M:N 의 문제점은 무엇인가요?
19. 그렇다면 다대다일 경우, 해결 방법은 무엇인가요?
20. JPA 1차 캐시, 2차 캐시에 대해 설명해주세요.
21. 왜 2차 캐시는 복사본을 반환할까요?