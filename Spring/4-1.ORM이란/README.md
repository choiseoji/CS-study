> ORM 은 객체 지향 프로그래밍과 관계형 데이터베이스 프로그래밍 사이의 구조적 불일치를 자동으로 해소해주는 기술입니다. 즉, 쿼리문을 직접 작성하지 않고 객체를 통해 데이터를 관리하는 기술입니다.
> 

### ORM [Object Relational Mapping]

---

- 프로그래밍 언어 엔티티(객체)와 해당 데이터베이스 사이의 연결을 **추상화**하는 프로세스




> 객체 지향 언어는 **상속, 참조, 다형성**이 중심이지만, RDS 는 **테이블, 외래 키, 조인** 중심입니다.



**[발생 가능 문제]**

**1️⃣ 상속 구조**

`Dog` 를 `Animal` 에 상속하고 싶습니다.

그러나 관계형 DB 에는 상속의 개념이 없습니다.

**2️⃣ 객체 참조 구조**

Member 테이블에는 Team 을 참조하고 있습니다.

객체 지향 프로그래밍에서는 `member.getTeam()` 으로 찾을 수 있지만

관계형 DB 에서는 FK 를 통해 JOIN 을 해야 합니다.

**3️⃣ 동일과 동등**

객체 지향 프로그래밍에서는 `==` 과 `equals()` 는 다릅니다.

그러나 관계형 DB 에서는 PK 가 같으면 동일 Row 로 인식합니다.

객체 지향형 **프로그래밍(클래스)**과 **테이블** 사이를 ORM 이 **자동으로 매핑**하기에 우리가 객체 중심의 코드로 데이터를 다룰 수 있게 되었습니다.

즉, 우리는 **비즈니스 로직에 더 집중**하게 되었습니다.

Java 에서는 JPA 표준 인터페이스와 Hibernate 구현체가 대표적인 ORM 입니다. (Persistant API)

- **동작 방식**
    1. **Persistence Context 관리**
        
        애플리케이션과 DB 사이 엔티티를 보관하는 가상 공간으로 1차 캐싱과 쓰기 지연이 일어난다.
        
        ```java
        // 1차 캐시 예시
        Member a = em.find(Member.class, 1L); // 캐시 저장
        Member b = em.find(Member.class, 1L); // 캐시에서
        ```
        
    2. **Dirty Checking**
        
        트랜잭션이 끝나는 시점에 객체 상태 변화를 감지해 개발자가 업데이트 쿼리 없이 자동 실행된다.
        
        ```java
        Member mem = em.find(Member.class, 1L);
        member.setName("변경");
        //em.update 불필요
        // 트랜잭션 커밋 시점에 스냅샷과 비교해 쿼리를 자동 생성
        ```
        
    3. **쿼리문 자동 생성 및 실행**
        
        Dialect 설정을 통해 특정 DB 의 SQL 을 자동 생성해 JDBC API를 통해 실행된다.
        

https://dev-coco.tistory.com/74

### ORM 의 장점

---

**1️⃣ 객체 지향적인 코드로 직관적이며 비즈니스 로직에 더 집중할 수 있다.**

ORM 을 사용하면 SQL 쿼리가 아닌 직관적인 코드로 데이터를 조작할 수 있어 “객체 지향 프로그래밍”에 집중할 수 있게 된다.

- 선언문, 할당, 종료 같은 부수 코드가 없거나 줄고 객체에 대한 코드를 별도 작성하기에 가독성이 올라간다.
- SQL 은 절차적으로 진행하는데 (아래 토글) 객체 지향적으로 접근으로 변경된다.

- **JDBC 방식의 절차적/순차적 접근 코드 예시 [비효율]**
    
    ```java
    public void updateMemberGrade(Long memberId, String newGrade) {
    	String selectSQL = "SELECT * FROM member WHERE id = ?";
    	String updateSQL = "UPDATE member SET grade = ? WHERE id = ?";
    	
    	Connection con = null;
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	
    	try {
    		con = dataSource.getConnection();
    		// 1. 로직 조회
    		pstmt = con.prepareStatement(selectSQL);
    		pstmt.setLong(1, memberId);
    		rs = pstmt.executeQuery();
    		
    		if (rs.next()) {
    			// 2. 비즈니스 로직 수행
    			String currentGrade = rs.getString("grade");
    			
    			
    			//3. 로직 수정
    			pstmt = con.prepareStatement(updateSQL);
    			pstmt.setString(1, newGrade)l
    			pstmt.setLong(2, memberId);
    			pstmt.executeUpdate();
    			
    		}
    	} catch (SQLException e) {
    		// 예외 처리 로직
    	} finally {
    		// 자원 반납
    		closeResources(con, pstmt, rs);
    	}
    	
    }
    ```
    

```java
@Transactional
public void updateMemberGrade(Long memberId, String newGrade) {
	// 1. 객체 조회 (식별자로 객체를 찾기)
		Member mem = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원");
			
	// 2. 비즈니스 로직 수행 (상태 변경
	// dirty checking 으로 자동 업데이트
	mem.changeGrade(newGrade);
	
}
```

**2️⃣ 재사용성 및 유지보수**

- ORM 은 독립적으로 작성되어 있고 해당 객체를 재사용 가능하다.
- ERD 에 대한 의존도를 낮춰준다.

**3️⃣ DBMS 에 대한 종속성이 줄어든다**

우리가 DBMS 를 교체해도 많은 시간이 걸리지 않는다.

### ORM의 단점

---

**1️⃣ N+1 문제 발생**

1번 쿼리 후 연관 엔티티가 N 번 추가 쿼리가 발생할 수 있다.

```java
// SELECT * FROM member - 1번
List<Member> members = memberRepository. findAll();
for (Member m : members) {
	// SELECT * FROM team WHERE id = ? - N 번
	m.getTeam().getName();
}
```

- **자세한 설명**
    
    실행 쿼리 1
    
    ```sql
    SELECT * FROM member;
    ```
    
    DB 에서 모든 회원 정보를 가져옵니다. (100명) - 1번의 쿼리
    
    - 회원 엔티티 내부의 `Team` 객체는 실제 데이터가 아닌 프록시 상태로 채워지기에 아직 팀 테이블에는 접근하지 않았습니다.
    
    실행 쿼리 2 - 100번의 쿼리
    
    이제 for 문을 돌면서 `m.getTeam().getName()` 이 호출되고 있습니다.
    
    1. `m.getTeam().getName()`
        
        프록시 객체에 데이터가 없어 DB 쿼리를 날립니다.
        
        `SELECT * FROM team WHERE id = [Member1의 Team_id];`
        
    2. `m.getTeam().getName()`
        
        2번째 팀 정보가 필요하므로 다시 DB 에 쿼리를 날립니다.
        
        `SELECT * FROM team WHERE id = [Member2의 Team_id];`
        
    3. 조회된 회원이 100명이라 각 회원의 팀 정보를 위해 추가로 100번의 쿼리가 실행됩니다.
    
    ⇒ 총 101번의 쿼리가 발생합니다.
    
    ### 해결 방법
    
    ---
    
    ```sql
    // Fetch Join
    @Query("SELECT m FROM Member m JOIN FETCH m.team")
    List<Member> findAllWithTeam();
    ```
    
    가능한 방법들
    
    `FetchType.LAZY` , `@EntityGraph` , `Fetch Join` , `BatchSize`
    

**2️⃣ 복잡한 쿼리가 어렵다.**

우리 데이터가 1000만 건이라면? 데이터가 특정 필터 또는 로직을 포함해야 한다면? 

Search 를 할 때 어떤 파라미터는 `Null` 이어도 되는 것이 여러 개라면?

이런 상황에서는 QueryDSL 이나 Native SQL 이 필요합니다.

**3️⃣ 성능을 예측하기 어렵고 세밀한 제어가 어려울 수 있다.**

우리가 실제로 어떤 SQL 이 나가는지 모르면 최적화에 어려움이 있습니다.

### 추가로 공부할 것들

---

> JPA 와 MyBatis 중 어떤 상황에서 무엇을 택해야 할까요?
> 
- 간략한 설명
    
    JPA 는 도메인 모델으로 비즈니스 로직 중심으로 작성하고 싶을 때
    
    MyBatis 는 복잡한 통계 쿼리나 레거시 DB 스키마에서 사용합니다.
    

> 영속성 컨텍스트의 생명 주기와 OSIV 패턴의 문제점은?
> 
- 간략한 설명
    
    **OSIV (Open Session In View)**
    
    `Transaction 범위`, `LazyInitializationException`, `커넥션 고갈` 이 키워드
    
    OSIV ON : View 렌더링까지 영속성 컨텍스트를 유지해 DB 커넥션 점유 시간 증가
    
    OSIV OFF : 트랜잭션 종료 시 영속성 컨텍스트를 종료 (그러나 지연 로딩이 불가하다.)
    
    그래서 `spring.jpa.open-in-view=false` + 서비스 레이어에서 DTO 변환
    

> Dirty Checking 동작 과정과 성능 문제가 발생할 수 있는 경우
> 
- 간략한 설명
    
    엔티티 로딩 시 스냅샷 저장 → 커밋 시 전체 필드 비교
    
    변경 필드가 1개여도 전체 컬럼 업데이트 쿼리 날림
    
    `@DynamicUpdate` 적용 후 변경 컬럼만 업데이트 가능
    
    단, 매번 쿼리를 새로 생성해 캐싱이 불가능함.