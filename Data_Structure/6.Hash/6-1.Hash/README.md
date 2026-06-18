### Q. 해시 알고리즘에 대해 소개해주세요.

**해시 알고리즘**

- 임의의 크기를 가진 데이터를 고정된 크기의 값(해시값)으로 변환하는 알고리즘
- 주로 HashMap, HashTable 같은 자료구조에서 빠른 검색, 삽입, 삭제를 위해 사용되며,
- key를 해시 함수에 넣어 index로 변환한 뒤 해당 위치에 데이터를 저장하는 방식
- 암호화적 해시
    - 보안 목적
    - 예시 : MD5, SHA-1, SHA-256, SHA-512
    - 입력이 조금만 바뀌어도 결과가 크게 바뀜, 역산이 사실상 불가능, 무결성 검증, 비밀번호 저장, 전자서명
- 일반 해시
    - HashMap같은 자료구조에서 사용
    - 빠른 조회, 삽입, 삭제가 목적
    - 예시 : Java String.hashCode(), MurmurHas, FNV Hash
    - 빠르게 계산 가능, 충돌 최소화

</br>
</br>

### Q. HashMap과 HashTable의 차이점은 무엇인가요?

HashMap과 HashTable은 모두 Map 인터페이스를 구현한 클래스이고, 크게 동시성 지원과 key에 Null 허용 여부에 차이가 있다.

**HashTable**

- Java 1.0에 등장한 Map 인터페이스의 구현체
- 거의 모든 메서드에 synchronized가 붙어 동기적으로 동작함 → Thread Safe
- 따라서, 거의 모든 연산에 스레드 간 잠금을 걸고 푸는 것을 반복하여 연산 속도가 느린편
- null key / null value 허용 안 함 → NullPointerException 발생

**HashMap**

- Java 1.2에 등장한 Map 인터페이스의 구현체
- 동기화 지원을 안 해서 다중 스레드 환경에서 동시성 문제 발생함 → Thread Safe X
- 연산 도중에 map이 변경되면 ConcurrentModificationException를 던져서 fail-fast 처리함
- 비동기적으로 작동하기 때문에 싱글 스레드 환경에서는 널리 사용됨
- null key 1개, null value 허용

**HashMap을 Thread Safe하게 사용하는 방법**

Java에서는 HashMap을 Thread Safe하게 사용하기 위해 `Collections.synchronizedMap()` 과 `ConcurrentHashMap`을 제공한다.

(1) synchronizedMap

```java
Map<String, String> map = Collections.synchronizedMap(new HashMap<>());
```

- 기존 HashMap을 synchronized로 감싼 Wrapper 객체
- 동일한 Map 객체에 대해 한 번에 하나의 스레드만 synchronized 메서드에 들어갈 수 있음
- 따라서, 한 스레드가 접근 중이면 다른 스레드는 대기해야 함
- 구현이 단순하지만 동시성이 높아질수록 성능 저하 발생
    - 제일 큰 단점 : 읽기만 하는 작업도 기다려야 함

(2) concurrentHashMap

```java
Map<String, String> map = new ConcurrentHashMap<>();
```

- 동시성을 고려해 설계된 Thread Safe Map
- Lock을 사용하긴 하지만, 전체 Map을 하나의 Lock으로 잠그지 않음
- 읽기 연산은 대부분 락 없이 수행 가능하고, 쓰기 연산도 필요한 영역(버킷)에 대해서만 동기화하여 처리한다 → synchronizedMap보다 높은 동시성과 성능을 제공
- null key와 null value를 허용하지 않음
- Java 8부터는 Segment Lock 대신 버킷 단위의 동기화와 CAS(Compare And Swap)를 활용하여 동시성을 제어
    - Segment Lock : HashMap 전체를 여러 segent로 나눔
        
        ```
        Map
         ├─ Segment 1 🔒
         │   ├─ bucket 0
         │   ├─ bucket 1
         │   └─ bucket 2
         │
         ├─ Segment 2 🔒
         │   ├─ bucket 3
         │   ├─ bucket 4
         │   └─ bucket 5
         │
         └─ Segment 3 🔒
             ├─ bucket 6
             ├─ bucket 7
             └─ bucket 8
        ```
        
- 여러 스레드가 동시에 조회하거나 서로 다른 버킷에 접근할 수 있어 병렬 처리 성능이 우수함
    - 읽기 작업 : 내부 필드들이 volatile로 선언되어 있어서 다른 스레드에서 수정한 값을 안전하게 조회할 수 있음
    - 쓰기 작업 : 버킷이 비어있으면 CAS로 내가 먼저 넣을 수 있는지 원자적으로 확인하고 삽입하고, 버킷에 이미 데이터가 있으면 해당 버킷만 synchronized로 잠근 후 수정한다.
- 따라서 멀티 스레드 환경에서는 Hashtable이나 synchronizedMap보다 ConcurrentHashMap을 사용하는 것이 일반적임

**💡 결론**

스레드 안정성이 필요없는 환경이라면 HashMap을 사용하는게 좋고, 만약 스레드 안정성이 필요하다면 HashTable보다는 concurrentHashMap을 사용하는게 좋다고 한다.

</br>
</br>

### Q. 어떤 것이 좋은 해쉬 함수인가요?

좋은 해시 함수는 충돌을 최소화하면서, 빠르게 계산할 수 있는 함수이다.

해시 테이블은 해시 함수가 데이터를 버킷에 얼마나 고르게 분산시키는지에 따라 성능이 결정되므로, 다음과 같은 특징을 가져야 합니다.

- 균등한 분포
    - 입력 값이 특정 버킷에 몰리지 않고 전체 버킷에 고르게 분포되어야 함
    - 만약 특정 버킷에 데이터가 집중되면 충돌이 많이 발생하여 탐색 성능이 O(1)에서 O(n)까지 저하될 수 있음
- 빠른 계산 속도
    - 해시 함수는 데이터 조회, 삽입, 삭제 시마다 호출되므로 계산 비용이 낮아야 함
    - 아무리 충돌이 적어도 계산 자체가 오래걸리면 성능이 떨어지니깐
- 결정성
    - 동일한 입력값에 대해서 항상 동일한 해시값을 반환해야한다 → 그래야 저장한 데이터를 다시 찾을 수 있으니
- 충돌 최소화
    - 서로 다른 입력값이 같은 해시값을 갖는 경우를 최소화해야 함
- 확산 효과
    - 입력값이 조금만 바뀌어도 해시값은 크게 달라져야 함
    - 비슷한 값이 서로 다른 버킷에 분산되어야지 충돌을 줄일 수 있어서