## 프록시 서버 사용시 페이지 내용과 데이터의 값이 계속 바뀌면?

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2Fb6Evdl%2FbtsDRpmoQ0f%2FAAAAAAAAAAAAAAAAAAAAAAag8A8w5-2ouL4XQX7rAeZUgNPNrmldcBulisnzoFyT%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1782831599%26allow_ip%3D%26allow_referer%3D%26signature%3D39qSWm2CK1fwC6xLfM8C9OuQlYc%253D)

### 캐시 유효
```http request
Cache-Control: max-age=60
```
프록시 서버는 이 페이지나 데이터를 60초 동안 캐시

이 시간 내에 재요청시 원서버까지 안가고 프록시가 저장해 둔 데이터를 보여줌

=> 캐시 만료 전이기에 원 서버 데이터가 바뀌었더라도 잠깐 이전 데이터를 볼 수 있음

### 캐시 만료
캐시 시간이 지나면 프록시는 원 서버에 재요청

-> 원 서버의 데이터 변경 존재 -> 프록시는 새로운 데이터를 받아오고 캐시 갱신

### 재검증
Etag나 Last-Modified 헤더를 사용해 캐시된 데이터가 여전히 유효한지 원 서버에 확인

```text
프록시: 이 데이터 아직 최신이야?
원 서버: 안 바뀌었어 → 304 Not Modified
원 서버: 바뀌었어 → 새 데이터 응답
```

### 계속 바뀌는 데이터
실시간 알림, 채팅, 주식 가격 등...

자주 변경되거나 민감한 정보는

```http request
Cache-Control: no-store
Cache-Control: no-cache
```

| 설정           | 의미                          |
| ------------ | --------------------------- |
| `no-store`   | 아예 저장하지 말라는 뜻               |
| `no-cache`   | 저장은 가능하지만 사용 전에 서버에 확인하라는 뜻 |
| `max-age=10` | 10초까지만 캐시 사용 가능             |


### 검증 헤더
stale한 캐시 데이터가 실제 서버 데이터에서도 변경되었는지 판별하기 위해 존재

1. Last-Modified: 리소스가 마지막으로 수정된 날짜와 시간

    캐시 데이터는 body만 저장하느게 아니라 헤더도 함께 저장
    
    stale하여 서버 쪽 변경이 필요한 데이터인지 확인하기 위해 요청 보낼 때 Last-Modified 값을 요청 헤더 If-Modified-Since에 담아서 보냄

    서버에서 마지막 수정일자와 If-Modified-Since의 날짜 비교해서 변경 없으면 304 Not Modified 응답

    - 단점: 1초 미만 단위로 캐시 조정 불가능 / 날짜 형식이라 실제 데이터가 변경 되었는지 정확히 판별하기 어려움

2. Etag: 리소스의 특정 버전을 식별하는 고유한 문자열
    이 Etag 값을 요청 헤더인 If-None-Match에 담아서 서버에 보냄
    
    이 값을 비교해 판별