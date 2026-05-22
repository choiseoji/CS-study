## 데드락 회피 알고리즘의 대표 격인 은행원 알고리즘(Banker's algorithm)의 핵심 원리와 실무적 제약 조건은 무엇인가요?
>은행원 알고리즘은 자원을 할당하기 전에 시스템이 안전 상태를 유지할 수 있는지 검사해서, 교착 상태를 회피하는 알고리즘입니다.
> 
> 각 프로세스의 최대 자원 요구량, 현재 할당량, 추가 필요량, 사용 가능한 자원을 기준으로 safe sequence가 존재하는지 확인합니다.
> 
> 약 어떤 순서로든 모든 프로세스가 필요한 자원을 받고 종료한 뒤 자원을 반납할 수 있다면 안전 상태로 보고 자원을 할당합니다.
> 
> 반대로 safe sequence가 없으면 불안전 상태이므로 자원 할당을 보류합니다
> 
> 다만 실무에서는 각 프로세스의 최대 자원 요구량을 미리 알아야 하고, 프로세스 수와 자원 수가 일정해야 하며, 매 요청마다 안정성 검사를 해야 해서 오버헤드가 큽니다.
> 
> 또한 불안전 상태가 반드시 데드락을 의미하는 것은 아니기 때문에 보수적으로 동작해 자원 활용도가 낮아질 수 있습니다.
> 
> 그래서 실무에서는 직접 적용보다는 락 순서 통일, 타임아웃, 트랜잭션 범위 축소 같은 방식으로 데드락 가능성을 줄이는 경우가 많습니다.

### 은행원 알고리즘
각 자원 유형마다 인스턴스가 여러개 있는 경우 사용

#### [방법]
- 각 프로세스의 자원 요청 개수 사용
- 현재 상태가 안전 상태인지 확인
- 불안정 상태라면 교착 상태라고 판단

자원을 전부 할당할 수 있는 프로세스부터 천천히 할당


![Safe State와 Unsafe state](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FdzKzTP%2Fbtq1jlpnlS9%2FAAAAAAAAAAAAAAAAAAAAAE6iVc_W7cAaMdbpXEchXu5lEw07ZR1l9cxZVNQC5Z_L%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1780239599%26allow_ip%3D%26allow_referer%3D%26signature%3Dqi2BiUDMvWxcuLbYC3ngS4C8hXc%253D)

**[Safe state]**

safe sequence(교착상태를 발생시키지 않고 자원을 할당하는 순서)가 존재하며 모든 프로세스가 정상적으로 종료될 수 있는 상태를 의미

**[Unsafe state]**

교착상태가 발생할 가능성이 있는 상태

=> Unsafe state라고 모두 데드락 발생은 아님

**[Safe sequence 찾기]**

Safe sequence가 존재하면 safe state 이기 때문에 교착상태가 발생하지 않는다는 것이 보장

- Maximum Demand : 프로세스에서 필요한 최대 자원의 수
- allocation : 현재 프로세스에 할당 된 자원의 수
- need : 프로세스에서 추가로 필요한 자원의 수
- Avaliable : 이용 가능한 자원의 수
- 프로세스들은 max needs 수 만큼 자원을 할당 받아야 자원을 사용하고 해제 함.

### [작동 원리]
1. 안정성 검사: 시스템이 안전 상태인지 확인 -> 모든 프로세스가 최대 자원 요구량을 충족시킬 수 있어서, 교착상태 없이 모든 프로세스가 종료될 수 있는 상태
2. 자원 요청: 프로세스가 자원ㅇ르 요청할 때, 시스템은 요청이 안정 상태를 유지할 수 있는지 검사 -> 요청 충족시켜도 안정 상태에 있을 경우에만 자원 할당

  ![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FcjeFpP%2Fbtq1nJbWrR5%2FAAAAAAAAAAAAAAAAAAAAAKQcQNxox-OE-DqJBUX7ygdUJocKF49ONM1v0LSVN6Hj%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1780239599%26allow_ip%3D%26allow_referer%3D%26signature%3D4s3aPrbxMw%252FsU5CFnzb7LRRMyvA%253D)

현재 avaliable resource = 3

1. P2에 자원 2개 할당 (1)
2. P2 자원 사용하고 해제 (5)
3. P1에 자원 5개 할당 (0)
4. P1 자원 사용하고 해제 (10)
5. P3에 7개의 자원 할당 (3)
6. p3 자원 사용하고 해제 (12)

만약 첫 표에서 P3에 자원이 하나 더 할당되어 avaliable resource = 2라면?

![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2Fx48hr%2Fbtq1e8xnblJ%2FAAAAAAAAAAAAAAAAAAAAAFB-Re3H4CG-DxiCBAG7WQnME19T_Rd24KO49xpj2um7%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1780239599%26allow_ip%3D%26allow_referer%3D%26signature%3DRVUueYvt60jkEqnySc%252B9TdpBOr8%253D)

=> safe sequence가 존재하지 않아 safe state XX


### 한계
1. 항상 불안전 상태를 회피해야 하므로 자원 활용도가 낮음
2. 최대 자원 요구량을 미리 알고 있어야 함
3. 자원과 프로세스, 사용자 수가 일정 해야함
4. 매 요청마다 안정성 검사 해야해서 오버헤드 큼
5. 보수적으로 동작할 가능성 존재
6. 실무 자원은 단순 숫자 표현 어려움

