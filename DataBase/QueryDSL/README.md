### QueryDSL란 무엇이고 왜 사용하셨나요?

<aside>
💡

QueryDSL은 기존 JPA의 문제를 해결하기 위해 도입했습니다. JPA의 경우 런타임에서 오류를 잡을 수 있고, 동적 쿼리의 작성이 어려워 만약 필터 조건이 많다면 이에 따른 분기와 메서드의 숫자가 늘어나 유지보수의 어려움을 겪었습니다.

QueryDSL은 엔티티 기반 쿼리용 클래스를 이용하여 자바 코드로 타입 안전한 쿼리를 작성할 수 있는 프레임워크입니다. 특히 QueryDSL 의 `where` 절은 `null` 파라미터를 자동 무시하는 특성을 살려 각 필터 조건을 별도의 `BooleanExpression` 메서드로 분리해 조건이 추가되더라도 메서드를 새로 만드는 것이 아닌 기존 쿼리를 조합하는 방식으로 유지보수를 가능한 구조를 이해 사용했습니다.

</aside>

~~여기서는 Spring 관련 기술 질문이지만, SQL과 관련이 아예 없지 않기에 여기서 다루도록 하겠습니다.~~

## QueryDSL?

---

https://github.com/OpenFeign/querydsl

QueryDSL 이란 Query Domain Specific Language의 약자 입니다.

이는 **자바 코드로 쿼리를 작성**할 수 있게 해주는 프레임워크입니다.

<br> <br>

(참고로 기존의 QueryDSL은 더 이상 업데이트를 하지 않아 저는 OpenFeign 에서 제공하는 QueryDSL을 사용합니다.)

저희가 **JPA** 를 사용해 개발을 하게 되는데, 왜 비슷한 기능인 **QueryDSL**을 사용할까요?

🤔 그러면 먼저 JPA에 대해서 잠깐 설명하겠습니다.

### JPA란?

---

이는 **객체 지향적으로 데이터**를 관리하자는 생각에서 나온 인터페이스입니다.

조금 더 명확히 이야기 하자면, 자바 애플리케이션에서 관계형 데이터베이스를 사용하는 방식을 정의한 **ORM**(Object-Relational Mapping) 기술이고 객체와 테이블을 **SQL 쿼리 없이 데이터를 객체 자체**를 다룰 수 있게 해 줍니다.

- **객체 중심 개발**이 가능해졌고
- 상속, 참조 등의 구조를 **자동으로 매핑**해줍니다.
- 또한 자동으로 CRUD 같은 SQL 문을 생성해 쿼리문이 나가게 됩니다.
- 또한 **JDBC 기반**이어서 DB와 자동으로 연동될 수 있습니다.
<br><br>
하지만 CRUD 같은 간단한 쿼리문은 자동으로 생성이 되지만, 복잡한 관계의 경우 우리가 **직접 쿼리문을 작성**해야 할 때가 있습니다.

```java
class interface UserRepository extends JpaRepository<User, Long> {
	User findByUserName(String UserName);
}
```

위의 경우 아래의 쿼리문이 자동으로 나가게 됩니다.

```sql
SELECT u.id, u.user_name, u.age
FROM User u
WHERE u.user_name =?
```
우리는 어떻게 쿼리문이 나가게 되는지 항상 알기는 해야 합니다.
<br><br><br>


하지만 조금 더 복잡한 쿼리문의 경우 다음과 같은 쿼리를 직접 작성할 수 있습니다.
<br>

**저희는 성인인 모든 유저를 찾고 싶습니다.**

```java
class interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("SELECT u FROMT User u WHERE u.age > 19")
	List<User> findAllAdultUser();
}
```

🔎 위에서 오탈자를 발견하셨나요?

### JPQL의 문제

1. **에러 확인 시점이 런타임 시점이다.**

위의 쿼리문이 **실행**될 때에만 에러가 확인이 됩니다.

따라서 애플리케이션은 실행이 되는데, 추후 이 쿼리문을 써서 문제가 생기는 경우

심지어 이에 대해 QA 없이 바로 배포가 된 경우 **서비스 전체에 장애**를 줄 수 있습니다.
<br><br>

2. **자바 언어가 아닌 직접 문자열로 작성해야 한다.**

위와 같은 문제인데요, 우리는 Query를 직접 문자열로 작성해야 합니다.

그러나 QueryDSL은 자바 코드로 작성되기 때문에 **컴파일 시점에 문제를 체크**할 수 있게 됩니다.

그래서 위의 오탈자를 발견하지 못한다면 에러가 저 메서드가 실행될 때 발생이 됩니다.
<br><br>

3. **메서드 분리와 재사용의 어려움**

우리가 예를 들어 “주문 내역을 확인하는데 본인, 사장님, 어드민에 따라 보여지는 내역이 다릅니다.”

|  | 본인 Customer | 사장님 Owner | 어드민 Admin |
| --- | --- | --- | --- |
| 주문 내역 | 본인의 주문 내역 | 본인 음식점의 주문 내역 | 전체 주문 내역 |


우리가 JPA로 구현한다면 다음 3개의 쿼리문을 만들 것 같습니다.
<br><br>

```java
@Query("SELECT o FROM Order o JOIN o.user u WHERE u = :user")
List<Order> findCustomerOrder(@Param("user")User user);

@Query("SELECT o FROM Order o JOIN o.store s WHERE s.owner = :user")
List<Order> findOwnerOrder(User user);

@Query("SELECT o FROM Order o")
List<Order> findAdminOrder();
```

문제가 무엇일까요?

1️⃣ 비슷한 쿼리문을 **계속 작성**해야 한다.

2️⃣ 조회 로직이 수정되면 저 3개의 쿼리문을 **모두 수정**해야 한다. (유지 보수의 어려움)

4. **동적 쿼리의 어려움**

위와 동일 상황에서 필터를 걸어 본다면?

|  | 본인 Customer | 사장님 Owner | 어드민 Admin |
| --- | --- | --- | --- |
| 주문 내역 | 본인의 주문 내역 | 본인 음식점의 주문 내역 | 전체 주문 내역 |
| 가게 id(nullable) | 가게 id로 필터링 | 필터링 기능 없음 | 가게 id로 필터링 |
| 주문 상태(nullable) | 주문 상태에 따라 필터링 | 주문 상태에 따라 필터링 | 주문 상태에 따라 필터링 |

```java
class record GetOrderFilter (
	UUID storeId,
	OrderStatus orderStatus	
) {
}
```

```java
List<Order> findByCustomer(User user);
List<Order> findByCustomerAndStatus(User user, OrderStatus status);
List<Order> findByCustomerAndStatue(User user, OrderStatus status);
...??
```

그렇다면 저렇게 **파라미터를 받고 서비스 (비즈니스 로직)에서 분기가 생긴다.**

<br><br>
개인적으로 분기에 따라 if-else 로 처리하는 것을 매우 매우 싫어한다. (전 전략 패턴을 좋아합니다.. 여러분은 개인적으로 좋아하는 디자인 패턴이 있나요? ♥️)

그렇다면 이 분기에 따른 다른 **JPA 쿼리문을 작성해 수행**해야 한다.

그러나 조금 더 생각해보면 만약 필터가 추가된다면 **저 3개의 쿼리문을 모두 수정**해야 하고, 조건이 생김에 따라 **기하급수적으로 수정사항**이 생긴다.(지금은 쿼리문을 작성 안해도 되는 쉬운 예시이지만)

즉, 조건만 다른 상태에서 **동적으로 조건을 넣기가 매우 까다로워 진다.**

### QClass 와 API

---

우리가 QueryDSL을 알려면 **QClass**를 무조건 알아야 합니다.

QueryDSL은 다음의 방식으로 진행이 됩니다.

1. **APT(Annotation Processing Tool)**
    
    빌드 시점에 `@Entity` 어노테이션을 찾아서 분석합니다.
    
2. **QClass**
    
    엔티티와 1:1로 매핑되는 `QMember` , `QOrder` 같은 클래스를 `target/generated-sources` 에 생성합니다.
    
    경로는 지정 가능합니다.
    
3. **타입 체크**
    
    이 QClass 에는 **엔티티의 메타데이터**가 있습니다. 그래서 코드를 작성할 때 이 필드가 String 인지 Long 인지 컴파일러가 미리 알 수 있습니다.
    
<br><br>
이에 따라 다음과 같이 사용할 수 있게 됩니다.

```java
import static com.example.entity.QUser.user;

public List<User> findUser(String userName) {
	return queryFactory
		.selectFrom(user)
		.where(user.username.eq(name)
		.fetch();
}
```

다시 예제로 넘어갑니다.

|  | 본인 Customer | 사장님 Owner | 어드민 Admin |
| --- | --- | --- | --- |
| 주문 내역 | 본인의 주문 내역 | 본인 음식점의 주문 내역 | 전체 주문 내역 |
| 가게 id(nullable) | 가게 id로 필터링 | 필터링 기능 없음 | 가게 id로 필터링 |
| 주문 상태(nullable) | 주문 상태에 따라 필터링 | 주문 상태에 따라 필터링 | 주문 상태에 따라 필터링 |

이 내용을 동적 쿼리문을 이용해 필터링을 걸어 처리해봅시다.
<br><br>

```sql
public List<Order> findCustomerOrders(User user, OderSearchVO vo) {
	return queryFactory
		.select(order)
		.where(
			order.customer.eq(user),
			statusEq(vo.status),
			storeEq(vo.store)
		)
		.fetch();
}

private BooleanExpression statueEq(Orderstatus status) {
	retur status != null ? order.status.eq(status) : null;
}

private BooleanExpression storeEq(Long storeId) {
	return storeId != null? order.store.id.eq(storeId) : null;
}
```

참고로 BooleanExpression 은 QueryDSL에서 중요합니다.

QueryDSL에서 **null 이면 처리하지 않는다는 과정**을 통해 동적으로 쿼리가 가능하게 됩니다.

## QueryDSL 실전 패턴

~~아래는 우아한 형제들 테크톡 일부분을 가져왔습니다.~~
<br><br>

하지만 이렇게 말해도 항상 모든 경우에 효율적인 것은 아닙니다.

즉, 우리는 어떻게 Query를 부르는지 어떻게 구조를 짰을 때 적절한 시간 복잡도가 나오는지 **고민** 🤔을 해야 합니다.

특히 대량의 데이터에서는 더더욱이요 !

### 1. Extends / Implements 사용하지 않기

---

QueryDSL의 경우 다음과 같이 작성해야 합니다. (docs 기준)

```java
class interface UserRepository extends JpaRepository<User, Long>, UserQueryRepository {

}

/** ----- **/
class interface UserQueryRepository {

}
/** ------ **/
@Repository
public class UserQueryRepositoryImpl implements UserQueryRepository {
	// 코드

}
```

UserRepository에서 원하는 메서드를 다 불러올 수는 있지만,

위의 문제점은 **상속 구조가 너무 깊어진다는 것** 입니다.

또한 단순 조회 쿼리를 만들 때마다 인터페이스를 만들고 이에 맞추어 Impl 클래스를 만들며… 반복적인 코드 작업이 필요합니다.

그렇기에 다음으로 작성하는 것을 더 추천합니다.

```java
@Configurtion
public class QueryDslConfig {
	private EntityManager em;
	
	@Bean
	public JpaQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(em);
	}
}

/** ------- **/
@Repository
@RequiredArgsConstructor
public class UserQueryRepository {
	private final JpaQueryFactory queryFactory; // 상속 없이 주입
	
	// 메서드 ...
}
```

### 2. 동적 쿼리 작성 시 `BooleanExpression` 을 적극 활용하자

---

이에는 `BooleanBuilder` 과 `BooleanExpression` 이 존재합니다.

- BooleanBuilder vs BooleanExpression
    
    BooleanBuilder은 가변 빌더 객체로 **if 처럼 조건에 따라 분기**하는 형태입니다.
    
    ```java
    public List<User> findUsers(String nameCheck, Integer ageCheck) {
    	BooleanBuilder builder = new BooleanBuilder();
    	
    	if (nameCheck != null) {
    		builder.and(user.username.eq(nameCheck));
    	}
    	if (ageCheck != null) {
    		builder.and(user.age.eq(ageCheck));
    	}
    	
    	return queryFactory
    			.selectFrom(user)
    			.where(builder)
    			.fetch();
    }
    ```
    
    BooleanExpression는 불변 표현식으로 **메서드 체이닝 형태로 구현**을 대부분 하게 됩니다.
    
    ```java
    public List<User> findUsers(String nameCheck, Integer ageCheck) {
        return queryFactory
                .selectFrom(user)
                .where(
                    nameEq(nameCheck), // null이면 무시됨
                    ageEq(ageCheck)    // null이면 무시됨
                )
                .fetch();
    }
    
    // 부품(Expression)들
    private BooleanExpression nameEq(String nameCheck) {
        return nameCheck!= null ? member.username.eq(nameCheck) : null;
    }
    
    private BooleanExpression ageEq(Integer ageCheck) {
        return ageCheck!= null ? member.age.eq(ageCheck) : null;
    }
    ```
    

리스트의 `where` 절에 `null` 이 전달되면 QueryDSL은 이를 무시하게 됩니다.
<br><br>

⚠️ 단, 모든 조건이 `null` 이라면 `where` 절이 사라지는 것과 같아져서 **Full Scan 이 발생**됩니다.

❕ 최소의 필수 조건은 꼭 걸어주세요 !

### 3. SELECT 성능 개선 전략

---

1️⃣ **`exists` 대신에 `fetchFisrt()` 사용하기**

QueryDSL의 `exists()` 는 내부적으로 `count(*)` 쿼리를 날립니다.

```sql
select exists (
	select 1
	from add_item_sum
	where created_date > '2026-01-01'
	);
```

위 쿼리 문은 해당 row를 발견하는 순간에 끝나게 되어 실행 시간이 빠릅니다.

<br><br>

하지만 아래의 쿼리문은 모든 조건을 다 체크해야 하기에 시간이 오래 걸립니다.

이는 `count` 조건을 만족하는 레코드가 전체 몇 개인지를 세기 위해 마지막 레코드까지 **모두 확인**해야 합니다.

```sql
select count(1)
from add_item_sum
where created_date > '2026-01-01'
```

그런데 QueryDSL은 exists가 count 로 동작하게 됩니다.

따라서 우리가 원하는 **exists 처럼 동작이 안 됩니다.**

- ? 왜 그럴까?
    
    JPQL에서 `select` 절에서 `exists` 를 오랫동안 지원하지 않았습니다.
    
    QueryDSL은 Hibernate 뿐 아니라 다양한 JPA 구현체에서 돌아가야 합니다. 그래서 공통적으로 결과가 존재하는지를 통일하기 위해 기존의 `count > 0` 을 사용해 구현했습니다.
    
    또한 `exists` 는 Boolean 타입을 반환해야 하는데, 이를 SQL 문으로 변환했을 때 모든 DB에서 표준적으로 동작하기 위해 이를 택했습니다.
    

그러면 어떻게 해야 우리는 `exists` 처럼 동작하게 할 수 있을까요?

대신 우리가 작성하면 됩니다.

```java
@Transactional(readOnly = true)
public Boolean exist(Long itemId) {
	Integer fetchOne = queryFactory
				.selectOne()
				.from(item)
				.where(item.id.eq(itemId))
				.fetchFirst();
	
	// 0이 아닌 null 로 확인해야 합니다.
	return fetchOne != null;
}
```

후에 사용은 다음과 같이 합니다.

`fetchFirst == limit(1).fetchOne();` 

이를 통해 DB 엔진이 `LIMIT 1` 을 통해 하나를 찾는 즉시 쿼리를 종료하게 되어 `exists` 와 동일한 성능을 내게 되는 것 입니다.

<br><br>

2️⃣ **묵시적 조인 (Cross Join) 방지하기**

만약 우리가 [`user.team.name`](http://user.team.name) 처럼 엔티티를 타고 들어가는 쿼리가 있다 해봅시다.

이는 당연히 Cross Join 을 발생시킬 수 있습니다. → [`team.name`](http://team.name) 에서 묵시적 조인이 발생합니다.

- **Cross Join**
    
    두 테이블의 모든 경우의 수를 다 조합하는 방식입니다.
    
    N개 행의 테이블 X M개 행의 테이블 = N * M의 결과
    

이는 QueryDSL의 문제가 아니라 Hibernate 자체의 이슈라서 JPA를 사용해도 동일 이슈가 발생합니다.

대신 우리는 명시적으로 `.join()` 을 이용해 Inner Join 을 유도해야 합니다.

- **Inner Join**
    
    특정 조건이 일치하는 데이터만 연결합니다.
    
    우리는 이를 PK를 이용해 사용하게 될 것 입니다.
    

3️⃣ **Entity 보다 DTO를 사용하자**

우리가 Repository를 구현하면서 Entity가 아닌 DTO를 준 적이 있나요?

사실 MVC 패턴에서 DTO는 Client 와 App 사이에서 Entity를 직접 노출하지 않고 필수적인 정보만 공유하기 위해 사용하는 패턴입니다.

그런데, Entity 조회 시 다음의 우려 사항이 있습니다.

- Hibernate 캐시가 발생한다.

이는 필요할 때가 있다고 생각이 되지만, 100만건의 데이터를 캐시에 다 올려둘 필요가 없습니다. 그게 Entity 전체라면 특히나 더요 !

- 불필요한 컬럼 조회

- OneToOne N+1 쿼리 

이의 경우 **Lazy Loading 이 불가능**하므로 무조건 N+1 문제가 발생하게 됩니다.

그렇기에 우리는 단순 조회의 경우 불필요한 데이터를 얻게 되는 경우가 있을 겁니다.

1. 비즈니스 로직 상 **데이터 변경**이 필요할 때는 Entity를 조회해야 합니다.
2. 만약 단순 화면 출력, 대량의 데이터 조회, 통계성 데이터 등 **단순 조회**만 할 때는 DTO를 사용합시다.

<br><br>

### 4. Group By 와 정렬 최적화

---

MySQL에서는 `Group by` 는 별도의 정렬 로직을 제공하는 경우가 많습니다.

이를 방지하려면 `Order By Null` 을 써야 하는데, QueryDSL의 경우 이를 지원하지 않아 직접 클래스르 구현해야 합니다.

그런데 여기서도 고민이 필요합니다.

만약 우리가 정렬이 필요한데 조회 결과가 100건 이하라면? 😒

우리는 이를 DB에서 정렬하는 것보다 **애플리케이션에서 정렬하는 것이 더 효율적**입니다.

왜냐하면 DB 보다 WAS의 자원이 더 저렴해 아주 많은 데이터가 아니라면 JAVA에서의 정렬을 고려하는게 좋습니다.

단, Paging이 필요하면 DB에서 정렬해야 합니다.

### 5. Covering Index

---

커버링 인덱스는 SELECT, WHERE, ORDER BY 등에 사용되는 **모든 컬럼이 인덱스에 포함된 상태**를 말합니다.

실제 데이터 블록을 보러 가지 않고 인덱스만 반복 결과를 반환하기에 속도가 빠릅니다.

이는 No-OffSet 과 더불어서 페이징 조회 성능을 높이는 보편적인 방법입니다.

그러나 JPQL은 `from` 절의 서브 쿼리를 지원하지 않기에 QueryDSL 단독으로 커버링 인덱스를 활용한 페이징 처리가 어렵습니다.

그렇기에 다음의 우회 방법이 필요합니다.

1. 인덱스가 걸린 PK 만 먼저 조회 한다.
2. 조회된 PK 리스트를 `in` 절에 넣어서 실제 필요한 데이터를 후속 조회한다.
