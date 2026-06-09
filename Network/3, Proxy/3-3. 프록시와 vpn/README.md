## VPN과 프록시의 차이는?

### VPN
Virtual Private Network

가상 사설망 -> 원격 서버를 통해 인터넷 트래픽을 재라우팅하고 실제 IP 주소를 가상 IP 주소로 대체함으로써 웹사이트 측에서 사용자의 실제 IP 주소와 위치를 확인할 수 없도록 하는 것

#### 프록시 서버와 차이
운영체제 수준에서 동작하기 때문에 브라우저나 백그라운드 앱에서 발생하는 모든 트래픽을 리디렉션 할 수 있음

![](https://surfshark.com/wp-content/uploads/2023/01/Proxy_vs_VPN_2_KO.svg)

| 구분    | 프록시 서버                   | VPN                       |
| ----- | ------------------------ | ------------------------- |
| 동작 범위 | 특정 앱/브라우저 요청 중심          | 기기 전체 네트워크 트래픽            |
| 암호화   | 보통 없음, HTTPS면 해당 통신만 암호화 | VPN 터널 자체를 암호화            |
| IP 숨김 | 가능                       | 가능                        |
| 보안성   | 상대적으로 낮음                 | 상대적으로 높음                  |
| 속도    | 보통 더 빠름                  | 암호화 때문에 느려질 수 있음          |
| 사용 목적 | 캐싱, 우회, 필터링, 로깅, 접근 제어   | 보안 통신, 사설망 접속, 공공 와이파이 보호 |

VPN은 내부 자원 접근 보안용, 프록시는 트래픽 제어/중계/운영 편의용

[왓챠 사내 VPN 개발](https://medium.com/watcha/watcha-%EC%82%AC%EB%82%B4-vpn-%EA%B0%9C%EB%B0%9C%ED%95%98%EA%B8%B0-e04f946d3ccb)

=> 재택 근무에서 오피스에서만 접근 가능한 리소스 접근 불가 문제 발생

VPN 연결을 통해 재택시 제한된 리소스 접근하는 방식 고안
![](https://miro.medium.com/v2/resize:fit:2000/format:webp/1*7mjgWNzrfqbE5twuiBWm6A.png)
![](https://miro.medium.com/v2/resize:fit:2000/format:webp/1*lnWX9BRJcEWKjlDZKAp-2Q.png)