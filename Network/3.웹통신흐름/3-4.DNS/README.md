## DNS 란 무엇인가요?

DNS(Domain Name Service)는 사람이 기억하기 쉬운 도메인 이름을 실제 통신에 사용되는 IP주소로 변환해주는 분산 계층형 시스템입니다.

사용자가 도메인으로 접속하면 DNS가 해당 도메인에 매핑된 IP 주소를 찾아 반환하며, 대표적으로 A 레코드를 통해 도메인과 IPv4 주소를 매핑할 수 있습니다.

DNS는 Root DNS, TLD DNS, Authoritative DNS로 역할을 분리한 계층형 구조를 사용하여 확장성과 가용성을 확보하고 있으며, 조회 결과를 캐싱하여 성능을 높입니다. 다만 IP가 변경되더라도 TTL이 만료되기 전까지는 기존 캐시가 사용될 수 있기 때문에 TTL 설정도 중요합니다.

</br>
</br>

### DNS란??

도메인 이름을 IP 주소로 변환해주는 서비스를 말한다. (Domain Name Service)

IP 주소는 사람이 기억하기 어렵고, 서버의 IP가 변경될 수 있어 이를 위해 DNS를 사용한다.

**DNS 레코드 종류**

- A 레코드 : 도메인 → IPv4
- AAAA 레코드 : 도메인 → IPv6
- CNAME 레코드 : 별칭 (www.example.com → example.com)
- MX 레코드 : 메일 서버 정보 (gmail.com → 메일 서버)
- NS 레코드 : 도메인을 관리하는 DNS 서버 정보 (example.com → ns1.example.com)

</br>

### DNS 특징

**DNS 분산**

- DNS는 전 세계 모든 도메인 정보를 하나의 서버가 관리하지 않고 여러 서버에 분산하여 관리하는 구조이다.
- 계층형(Hierarchical) 구조를 통해 도메인 관리 책임을 나누어 가진다.

```sql
Root DNS
    ↓
TLD DNS (.com, .kr, .net ...)
    ↓
Authoritative DNS
```

- Root DNS
    - TLD DNS 서버 위치 정보 관리
    - 실제 도메인 IP는 저장하지 않음
- TLD DNS
    - 각 도메인의 Authoritative DNS 정보 관리
    - 실제 도메인 IP는 저장하지 않음
- Authoritative DNS
    - 실제 도메인과 IP 매핑 정보 관리
    - 최종 DNS 응답 제공
- 장점
    - 확장성 → 전 세계 수많은 도메인을 효율적으로 관리 가능
    - 가용성 → 특정 DNS 서버 장애가 전체 인터넷 장애로 이어지지 않음
    - 부하 분산 → DNS 요청을 여러 서버가 나누어 처리

</br>

**Recursive Query / Iterative Query 질의**

DNS 조회는 Recursive Query와 Iterative Query 방식으로 이루어진다.

- Recursive Query
    - DNS 서버가 최종 IP 주소를 찾아서 반환하는 방식
    - 주로 클라이언트 ↔ Local DNS 서버 간 통신에 사용
    - 최종 결과 반환
- Iterative Query
    - 다음에 질의해야 할 DNS 서버 위치를 반환하는 방식
    - DNS 서버 간 통신에 사용
    - 다음 DNS 서버 위치 반환

</br>

**DNS 캐시와 TTL**

- DNS 캐시
    - DNS 조회 결과를 일정 시간 저장하는 기능
    - 매번 Root DNS부터 조회하지 않아 응답 속도를 향상시킨다.
    - 브라우저, 운영체제, Local DNS 서버 등에 캐시가 저장될 수 있다.
- TTL을 길게 설정하면
    - DNS 조회 감소
    - 응답 속도 향상
    - IP 변경 반영 지연
- TTL을 짧게 설졍하면
    - IP 변경 반영 빠름
    - DNS 조회 증가
    - DNS 서버 부하 증가

</br>

### CDN과 DNS

- CDN도 DNS를 활용하여 동작한다.
- 사용자가 CDN 도메인에 요청하면 일반적인 DNS 조회 과정과 동일하게 IP 주소를 조회한다.
- 다만 CDN의 Authoritative DNS는 GSLB와 유사한 로직을 사용하여 사용자 위치, 네트워크 상태, 서버 부하 등을 고려한 최적의 Edge 서버 IP를 반환한다.
- 따라서 같은 도메인이라도 사용자 위치에 따라 서로 다른 Edge 서버 IP가 응답될 수 있다.

```sql
한국 사용자
cdn.example.com
↓
서울 Edge 서버 IP

미국 사용자
cdn.example.com
↓
LA Edge 서버 IP
```