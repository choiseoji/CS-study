# REST, RESTful 이란 무엇인가요?

***

> REST는 자원의 이름으로 구분해 정보를 주고 받는 모든 것을 의미합니다. REST 는 클라이언트와 서버 구조, Stateless 등의 특징이 존재하는데요, RESTful API란 이 REST 의 원칙을 잘 지켜 설계한 API 를 의미합니다. 그렇기에 URL 구조가 직관적이고 명확해 이해하기 쉽습니다.

## REST (REpresentational State Transfer)

***

- 자원의 이름으로 구분해 해당 자원의 상태나 **정보를 주고 받는 모든 것**을 의미한다.

- 즉, 자원(resource) 의 표현(representation)에 의한 상태 전달의 의미한다.

> 어떤 자원에 대해 CRUD 연산을 수행하기 위한 URI로 HTTP 메서드를 사용해 요청을 보내며 요청을 위한 자원은 특정 형태로 표현된다.

[추가] URI vs URL

- URL == 인터넷 상 자원의 위치

- URI == 인터넷 상 자원 식별 문자열 구성

### REST 구성 요소

***

1. Resource - URI

- 모든 자원에는 고유 ID 가 존재하고 이 자원은 서버에 존재한다.

2. 행위 - Method

- GET, POST, PUT, PATCH, DELETE 의 메서드를 제공한다.

3. 표현

- Client <-> Server 가 데이터를 주고 받는 형태

- JSON, XML, TEXT, RSS 등이 존재 (JSON, XML 이 일반적)

### REST 의 특징

***

1. Server-Client

- 자원이 있는 쪽이 Server, 자원을 요청하는 쪽이 Client 가 된다.

    REST SERVER
        API 제공하고 비즈니스 로직 처리 및 저장 책임

    Client
        사용자 인증이나 context(세션이나 로그인 정보)등을 직접 관리하고 책임

2. Stateless

- HTTP 프로토콜은 Stateless 프로토콜이기에 REST 또한 이를 따른다.

- Client 의 Context 를 서버에 저장하지 않는다.

    즉, 세션과 쿠키 같은 context 정보를 신경쓰지 않아도 된다.

- Server 는 각 요청을 별개의 것으로 인식하고 처리한다.

    서버 처리의 일관성을 부여하기에 자유도가 높아짐

3. Cacheable

- 웹에서 기존의 인프라 그대로 캐싱 기능을 적용가능하다.

4. Layered System

- Client 는 Rest Server 와 통신하는데
    Client 는 지금 미들 서버와 통신하는지 직접 통신하는지 등을 모른다.
    서버는 보안이나 로드 밸런싱, 암호화 등을 계층을 추가하고 구성 가능하다.

5. Uniform interface

- URI 로 지정한 리소스에 대한 요청을 통일되는 아키텍처 스타일

- 즉, 언어나 기술에 종속되지 않음

6. Self-Descriptiveness

- 요청 메시지만 보고 쉽게 이해할 수 있는 표현 구조

### REST API

- REST 특징으로 서비스 API를 구현한 것

- **각 요청이 어떤 동작이나 정보를 위한 것인지를 그 요청의 모습 자체로 추론이 가능하다.**

- REST API 디자인 가이드

1. URI는 정보의 자원을 표현해야 하고

2. 자원에 대한 행위는 HTTP 메서드로 표현한다.
    단, 행위는 URI 에 포함하지 않는다.

#### REST API 설계 규칙

1. URI 는 명사를 사용해야 한다.

1-1. `/getAllUsers` 와 같은 동사를 사용하면 안되고

2. 슬래시(/)로 계층 관계를 표현하고

3. URL 마지막은 슬래시를 포함하지 않는다.

4. 밑줄(_)은 불가 하이픈(-) 가능

5. URI 는 소문자만

6. HTTP 응답 상태 코드 사용하고

7. 파일 확장자는 URI 에 포함하지 않는다.

## Rest API vs RESTful API

> RESTful 은 REST 의 설계 규칙을 잘 지켜 설계된 API를 의미한다.

