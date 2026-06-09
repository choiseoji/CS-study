## Cookie, Session, JWT의 차이에 대해 설명해주세요.

### Cookie
Key-Value 형식의 문자열 / 사이트가 사용하고 있는 서버를 통해 클라이언트의 브라우저에 설치되는 작은 기록 정보 파일

#### 인증 방식
![](https://blog.kakaocdn.net/dna/dz22UP/btrGKdk8u61/AAAAAAAAAAAAAAAAAAAAAAsSsGluKS8NTBbfc6bMxr4isBwOGq5l3guxTxFqsc0Y/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1782831599&allow_ip=&allow_referer=&signature=ej7hq1pnMRau8qfyitJSmyWQ8Ag%3D)
1. 브라우저가 서버에 요청을 보냄
2. 서버는 클라이언트의 요청에 대한 응답 작성할 때 클라이언트 측에 저장하고 싶은 정보를 응답 헤더의 Set-Cookie에 담음
3. 해당 클라이언트는 요청을 보낼 때마다, 매번 저장된 쿠키를 요청 헤더의 Cookie에 담아 보냄

#### 단점
- 보안에 취약 - 쿠키 값을 그대로 보내기 때문에 유출 및 조작 위험 존재
- 용량 제한이 있음
- 다른 브라우저간 공유 불가능
- 쿠키 사이즈 클수록 네트워크 부하 심해짐

### Session
쿠키의 보안적인 이슈로 서버측에 민감 정보를 저장하고 관리

#### Session 객체
Key에 해당하는 Session Id와 이에 대응되는 Value로 구성

-> 세션 생성 시간, 마지막 접근 시간 및 user가 저장한 속성 등이 Map 형태로 저장

#### 인증 방식
![](https://blog.kakaocdn.net/dna/bOHl96/btrqAVAjQrg/AAAAAAAAAAAAAAAAAAAAAEN-vPuNV1zf4O_hCAdiRULfiBNKCR4xRRTNqGYnda-h/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1782831599&allow_ip=&allow_referer=&signature=aK9KJxXByo1SXyOfMz%2Fy98i65vw%3D)
1. 로그인하면 세션이 서버 메모리에 저장
2. 서버에서 브라우저 쿠키에 Session Id 저장
3. 쿠키 정보가 담겼기때문에 브라우저는 요청에 Seesion Id를 쿠키에 담아 전송
4. 서버는 요청에 동봉된 Seesion Id과 서버 메모리에 있는 Session Id를 비교하여 인증 수행

#### 단점
- 세션 ID 탈취해 위장할 수 있음
- 요청이 많아지면 서버에 부하

### Token
서버에서 클라이언트에게 인증되었다는 의미의 토큰 부여

#### 인증 방식
![](https://blog.kakaocdn.net/dna/blmhiY/btrqFjUG9ID/AAAAAAAAAAAAAAAAAAAAADMZDm6LVykU6exqI02x8kQOUuAWo3LA-H96xDAIQsRD/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1782831599&allow_ip=&allow_referer=&signature=6ljc3pwbxRgDBnyQ4%2B2q0gKBvBI%3D)
1. 로그인 시 서버는 클라이언트에게 유일한 토큰 발급
2. 클라이언트는 서버 측에서 받은 토큰을 쿠키나 스토리지에 저장해두고 HTTP 헤더에 포함시켜 전달
3. 서버는 전달받은 토큰을 검증하고 요청에 응답

#### 단점
- 토큰 데이터 길이가 길어 인증 요청이 많아질수록 네트워크 부하 심해짐
- Payload 자체는 암호화되지 않기 때문에 유저의 중요 정보 담을 수 없음
- 토큰 탈취의 대처 어려움 (사용 기간 제한 설정)

### JWT
인증에 필요한 정보들을 암호화시킨 JSON 토큰
![](https://blog.kakaocdn.net/dna/bwmwBq/btrqGVel5Qk/AAAAAAAAAAAAAAAAAAAAACGDUfkfoew8M5xzabdhZZ1pLxMnnnVz0_Wrdetplosz/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1782831599&allow_ip=&allow_referer=&signature=Xchkty6snNokmz%2FE2xYIkn7sZL8%3D)

Header와 Payload, Signature로 구성

#### 인증 과정
![](https://blog.kakaocdn.net/dna/t2DrY/btrqGTOykhT/AAAAAAAAAAAAAAAAAAAAADsCFTxDyIe9u5VjEEURzEGJe1hPamGVTQqoPBR8QERX/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1782831599&allow_ip=&allow_referer=&signature=6WtCbXe%2FEWSkeBX%2FKuPhfK8DoPI%3D)

1. 로그인 인증 요청
2. 서버는 header, payload, signature 정의 후 암호화하여 JWT 생성해 쿠키에 담아 전달
3. 클라언트는 JWT를 저장
4. 요청할때마다 헤더에 Access Token을 담아 보냄
5. 서버는 JWT를 내 서버에서 발행한 토큰인지 일치 여부 확인해 인증 검증
6. 엑세스 토큰 시간 만료되면 리프레시 토큰 이용해 새로운 엑세스 토큰 발급

#### 단점
- 토큰 자체에 정보가 있어 양날의 검...
- 토큰 길어서 네트워크 부하
- payload 탈취하여 디코딩하면 데이터 볼 수 있음
- stateless 특징 때문에 토큰 탈취당하면 대처 어려움

## 요청이 많이 몰렸을 때, 어떻게 처리하는 것이 좋을까요?

| 관점           | 세션 방식        | JWT 방식             |
| ------------ | ------------ | ------------------ |
| 클라이언트가 보내는 값 | 짧은 sessionId | 긴 JWT              |
| 네트워크 부하      | 작음           | 상대적으로 큼            |
| 서버 저장소 조회    | 필요           | 보통 불필요             |
| 서버 부하        | 상대적으로 큼      | 상대적으로 작음           |
| 수평 확장        | 세션 공유 필요     | 쉬움                 |
| 병목 지점        | Redis/세션 저장소 | 네트워크 전송량, 토큰 검증 비용 |
| 강제 로그아웃      | 쉬움           | 어려움                |

```text
서버 저장소 부하가 더 걱정된다
→ JWT가 유리

네트워크 비용과 요청 크기가 더 걱정된다
→ 세션이 유리
```

웹 서비스에서는 세션 저장소 병목이 더 치명적인 경우가 많아서, 대규모 트래픽에서는 JWT Access Token 사용이 유리

### 추가적으로 어떤 방식을 도입해볼 수 있을까?
#### 로그인 재시도 요청 제한
IP 기준 혹은 계정 기준으로 특정 시간 내에 실패 회수를 제한하거나 Captch 또는 추가 인증 고려

+) 추가로 refrech token으로 access token 재발급하는 로직도 제한을 두는 것이 좋음!

#### 인증 서버 분리

#### Oauth
일부 인증 부하는 줄일 수 있음

그러나 완전한 해결책 XX