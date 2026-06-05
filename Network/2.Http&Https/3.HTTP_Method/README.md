# HTTP 메서드와 이것이 하는 역할, 그리고 상태 코드가 무엇이 있는지 설명해보세요.

***

앞에서 HTTP는 서버와 클라이언트가 데이터를 교환하는 것이라 했고, 메시지 타입은 Request 와 Response 가 존재한다.

또한 `ASCII` 로 인코딩된 텍스트 정보이다.

[Request]
`HTTP Method` + `요청 타겟(URL 또는 프로토콜, 포트, 도메인의 절대 경로 등)` + `HTTP버전`

```
POST /api/users HTTP/1.1
Host: kauth.kakao.com
Content-Type: application/json

{
    "name": "test",
    "role": "admin"
}
```


[Response]
상태 줄, 헤더, 본문 으로 이루어져 있다.

```
HTTP/1.1 200 OK
Content-Type: application/json
Cache-Control: no-cache

{
    "status": "success",
    "id": 1234
}
```


## HTTP Request Method

- 주어진 리소스에 수행하기 원하는 행동을 나타내는 메서드이다.

1. `GET`

    - 리소스 조회
    - `GET /memgers/100?username=test&role=admin'
    - 전달하고 싶은 데이터를 쿼리 스트링을 통해서 전달하고 그 외에도 메시지 바디를 사용해 전달할 수 있지만, 서버에서 따로 구성해야 하기에 지원하지 않는 곳이 많아 권장하지 않는다.
    - 조회 시 POST를 사용할 수 있지만 GEt 메서드는 캐싱이 가능하기에 GET을 사용하는 것이 유리하다.


2. `POST`

    - 요청 데이터를 처리하고 주로 **등록**에 사용된다.
    - 메시지 바디를 통해서 서버로 요청 데이터를 전달하면 서버는 요청 데이터를 처리해 업데이트한다.
    - 전달된 데이터로 주로 신규 리소스를 등록하고 프로세스 처리에 사용한다.
    - 만약 데이터를 GET 할 때, JSON으로 조회 데이터를 넘겨야 하는 애매한 경우 POST 를 사용하기도 한다.

3. `PUT`

    - 리소스를 대체(덮어쓰기)를 하고 해당 리소스가 없으면 생성한다.
    - 데이터를 대체해야 하므로 클라이언트가 리소스의 구체적인 전체 경로를 지정해 보내주어야 한다.

4. `PATCH`

    - 리소스를 부분 변경한다.
    - PATCH 를 지원하지 않는 서버에서는 대신 POST를 사용할 수 있다.

5. `DELETE`

    - 리소스를 삭제한다.
    - 상태 코드는 대부분 200을 사용하고 상황에 따라 204를 사용한다.

기타 메서드

6. `HEAD`

    - GET 과 동일하지만, body 를 제외하고 상태 줄과 헤더만 반환한다.
    - 일종의 검사 용도로 리소스를 받지 않고 오직 찾기만 원할 대 사용한다.
    - 예: 서버의 응답 헤더를 봄으로써 리소스가 수정되었는지 확인한다.

7. `OPTIONS`

    - 대상 리소스에 대한 통신 가능 옵션을 설명한다. (주로 CORS 에서 사용)
    - preflight 에 사용되는 HTTP 메서드이다.

8. `CONNECT`

    - 대상 자원으로 식별되는 서버에 대한 터널을 설정한다.

9. `TRACE`

    - 대상 리소스에 대한 경로를 따라 메시지 루프백 테스트를 수행한다.
    - 이를 통해 요청했던 패킷 내용과 응답 받은 요청 패킷을 비교해 변조 유무를 확인할 수 있다.

## HTTP 상태 코드

특정 HTTP 요청이 성공적으로 완료되었는지 알려준다.

응답 5개의 그룹(정보 응답, 성공 응답, 리다이렉션 메시지, 서버 에러 응답, 브라우저 호환성)으로 나누어져 있고 아래에 자세히 나와있다.

1xx 는 요청을 받았고 작업을 진행 중이다.
2xx 는 요청을 성공적으로 받았고 이해했으며 수용한다.
3xx 는 요청을 완료하기 위해 브라우저의 추가 이동이 필요하다.
4xx 는 잘못된 문법 등 **클라이언트 측 원인으로 요청을 처리할 수 없다.**
5xx 는 서버가 유효한 요청을 처리하는 데 실패했다. (즉각 대응이 필요하다.)

📍 아주 중요
✏️ 직접 다루기 보다는 인프라 관련해서 자주 마주칠 수 있는 중요 코드

[section 10 of RFC 2616](https://datatracker.ietf.org/doc/html/rfc2616#section-10)

1. **정보 응답**

    `100 Continue`
        임시적인 응답은 지금까지의 상태가 괜찮으며 클라이언트가 계속해 요청을 하거나 이미 요청을 완료한 경우에는 무시해도 된다.

    `101 Switching Protocol`
        클라이언트가 보낸 upgrade 요청 헤더에 대한 응답이고 서버에서 프로토콜을 변경할 것이다.

    `102 Processing`
        서버가 요청을 수신했고 이를 처리하고 있지만, 아직 제대로된 응답을 알려줄 수 없음을 알려준다.

    `103 Early Hints`
        Link 헤더와 함께 사용되어 서버가 응답을 준비하는 동안 사용자 에이전트가 사전 로딩을 시작할 수 있도록 한다.
    
2. **성공 응답**

    📍 `200 OK`
        요청이 성공적으로 되었다는 의미. 성공 의미는 HTTP 메서드에 따라 달라진다. GET, HEAD, PUT, POST, TRACE
    
    📍 `201 Created`
        요청이 성공적이었으며 결과로 새로운 리소스가 생성되었다는 의미이다. POST나 일부 PUT 요청 이후에 따라온다.

    `202 Accepted`
        요청을 수신하였지만 그에 응해 행동할 수 없다.
        다른 프로세스에서 처리 또는 서버가 요청을 다루고 있거나 배치 프로세스를 하고 있는 경우를 위해

    `203 Non-Authoritative information`
        돌려받은 메타 정보 세트가 Origin 서버의 것과 일치하지 않지만 로컬이나 third-party 복사본에 모아졌음을 의미한다. 이러한 조건에서는 203이 아닌 200 Ok가 우선시 된다.

    📍 `204 No Content`
        요청에 대해 보내줄 수 있는 콘텐츠가 없지만 헤더는 의미있을 수 있다.

    `205 Reset Content`
        응답 코드는 요청을 완수한 이후 사용자에게 이 요청을 보낸 문서를 리셋하라고 알려준다.

    `206 Partial Content`
        클라이언트에서 복수의 스트림을 분할 다운로드 하고자 범위 헤더를 전송했을 때 사용한다.

    `207 Multi-Status`
        여러 리소스가 여러 상태 코드인 상황일 경우 해당 정보를 전달한다.

    `208 Already Reported`
        동일 컬렉션으로 바인드된 복수의 내부 멤버를 반복적으로 열거하는 것을 피하기 위해 사용된다.

    `226 IM used`
        GET 요청에 대한 리소스 의무를 다 했고, 그 응답이 하나 또는 그 이상의 인스턴스 조작이 현재 인스턴스에 적용이 되었음을 알려준다.

3. **리다이렉션 메시지**

    `300 Multiple Choice`

    ✏️ `301 Moved Permanently`
        - 요청한 리소스의 URI가 완전히 영구적으로 변경됨.
        - 웹사이트 주소를 http://old.com에서 https://new.com으로 영구 이전할 때 Nginx에서 설정.

    ✏️ `302 Found`
        - 리소스의 위치가 일시적으로 변경됨.
        - 로그인 안 한 유저를 일시적으로 로그인 페이지로 보냈다가 원래 페이지로 복귀시킬 때.

    `303 See Other`

    ✏️ `304 Not Modified`
        - 클라이언트가 가진 캐시 데이터가 최신이므로 그대로 사용하면 됨.
        - 브라우저가 이미지 파일을 요청했을 때 서버가 "변한 게 없으니 네 컴퓨터 캐시 써"라고 할 때.
    
    `305 Use Proxy`
        - 지금 안써요.

    `306 unused`
        - 지금은 안쓰는 예약어

    ✏️ `307 Temporary Redirect`
        - 302와 같으나, 리다이렉트 시 HTTP 메서드(POST 등)를 변경하면 안 됨.
        - 사용자가 결제 요청(POST)을 보냈는데, 일시적인 서버 사정으로 다른 주소의 POST로 그대로 토스할 때.

    ✏️ `308 Permanent Redirect`
        - 301과 같으나, 리다이렉트 시 HTTP 메서드를 변경하면 안 됨
        - API 버전 업그레이드로 POST /v1/user 요청을 POST /v2/user로 그대로 영구 토스할 때.

4. **클라이언트 에러 응답**

- 특히 404는 라우팅 문제인지, 데이터 없음인지 등 구분이 애매하면 예외가 생깁니다. 이와 관련된 이야기를 [다음 유튜브](https://www.youtube.com/watch?v=bcd2k3iY8Wg)에서 이야기 하고 있습니다.

    📍 `400 Bad Request`
        - 클라이언트가 올바르지 않은 파라미터나 형식을 보냄.
        - 클라이언트가 올바르지 않은 파라미터나 형식을 보냄.이메일 가입 요청 시 이메일 형식(@)이 누락되었을 때 예외처리 후 리턴.
    

    📍 `401 Unauthorized`
        - 해당 요청을 하려면 본인이 누구인지 인증(로그인)이 필요함.
        - 로그인을 안 한 비회원이 마이페이지를 조회하려고 접근했을 때.

    `402 Payment Required`

    📍 `403 Forbidden`
        - 인증은 되었으나, 해당 리소스에 접근할 권한이 없음.
        - 일반 회원이 관리자(Admin) 전용 정산 페이지를 열려고 할 때 권한 체크 후 차단.

    📍 `404 Not Found`
        - 요청한 URI에 해당하는 리소스가 존재하지 않음.
        - 사용자가 삭제된 게시글 번호(GET /posts/99999)를 조회하려고 할 때.

    ✏️ `405 Method Not Allowed`
        - 해당 URI에서 허용하지 않는 HTTP 메서드를 사용함.
        - 상품 등록 API를 POST로 만들어 놨는데 클라이언트가 PUT으로 찔렀을 때 Spring이 자동 리턴

    `406 Not Acceptable`

    `407 Proxy Authentication Required`

    `408 Request Timeout`

    📍 `409 Conflict`
        - 서버의 현재 상태와 충돌이 발생함.
        - 이미 가입된 이메일로 똑같이 회원가입을 시도할 때 중복 예외 처리.

    `410 Gone`

    `411 Length Required`

    `412 Precondition Failed`

    `413 Payload Too Large`

    `414 URI Too Long`

    ✏️ `415 Unsupported Media Type`
        - 서버가 지원하지 않는 데이터 형식으로 요청함
        - 서버는 JSON 데이터만 받는데 프론트엔드가 XML 형식으로 데이터를 전송했을 때

    `416 Requested Range Not Satisfiable`

    `417 Expectation Failed`

    `418 I'm a teapot`

    `421 Misdirected Request`

    `422 Unprocessable Entity`

    `423 Locked`

    `424 Failed Dependency`

    `426 Upgrade Required`

    `428 Precondition Required`

    `429 Too Many Requests`

    `431 Request Header Fields Too Large`

    `451 Unavailable For Legal Reasons`

5. **서버 에러 응답**

    📍 `500 Internal Server Error`
        - 서버 내부 오류로 인해 요청을 처리할 수 없음
        - 코드 내부에서 NullPointerException이나 DB 에러가 터졌는데 캐치하지 못했을 때 자동 발생.

    `501 Not Implemented`

    ✏️ `502 Bad Gateway`
        - 게이트웨이나 프록시 서버가 상위 서버로부터 잘못된 응답을 받음
        - 앞단의 Nginx는 살아있는데 뒷단의 Spring Boot(WAS) 애플리케이션이 완전히 꺼져있을 때

    ✏️ `503 Service Unavailable`
        - 서버가 일시적인 과부하 또는 점유로 인해 요청을 처리할 수 없음.
        - AWS 로드 밸런서 뒤의 서버들이 트래픽 폭주로 다운되어 트래픽을 처리할 서버가 한 대도 없을 때

    ✏️ `504 Gateway Timeout`
        - 게이트웨이가 타겟 서버의 응답을 기다리다 타임아웃 시간이 초과됨
        - Nginx가 Spring Boot에 요청을 보냈는데, Spring 내부 쿼리가 너무 무거워 60초 동안 대답이 없을 때

    `505 HTTP Version Not Supported`

    `506 Variant Also Negotiates`

    `507 Insufficient Storeage`

    `508 Loop Detected`

    `510 Not Extended`

    `511 Network Authentication Required`

참고로 상태 코드를 보다 보면 WebDAV 전용 이런 것을 볼 수 있는데 이는 웹 기반 분산 저작으로 초기 특수 하이퍼 텍스트 전용이기 일반적인 웹이나 앱에서는 마주칠 일이 적습니다. (102, 103, 207, 208, 226, 418, 422, 423, 424, 425, 506, 507, 508, 510)

