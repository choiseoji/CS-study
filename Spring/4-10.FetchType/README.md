## Eager Loading과 Lazy Loading 에 대해 설명해주세요.

### Fetch Type 이란?

---

JPA 가 하나의 Entity 를 조회할 때, **연관관계에 있는 객체들을 어떻게 가져올 것**인지를 나타내는 설정값입니다.

- JPA는 객체와 필드를 보고 쿼리를 생성하는데, 다른 객체와 연관관계 매핑이 있다면 그 객체들도 조사를 합니다.
- 이 때 **바로** 불러올지 아니면 **사용할** 때 불러올 지를 결정짓는 타입입니다.

## Eager Loading

---

> 즉시 로딩(Eager Loading)
> 

`@ManyToOne` 이나 `@OneToOne` 은 즉시로딩이 기본입니다.

- 데이터 조회 시 연관된 모든 객체의 데이터까지 한 번에 불러오는 것입니다.

```java
// Order Entity
...
@ManyToOne
@JoinColumn(name = "member_id")
private Member member;

@OneToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "delivery_id")
private Delivery delivery;
```

위의 주문을 조회하는 로직이 있다면 

- 주문과 N:1 매핑인 회원 관계 모두를 조회하게 될 것입니다.
- 주문과 1:1 매핑인 배송 관계 모두를 조회하게 될 것 입니다.

### 장단점

연관된 엔티티들을 모두 불러올 수 있다.

**😰 단점**

엔티티간 관계가 복잡해질수록 조인으로 인한 성능 저하가 나타날 수 있습니다.

- 불필요한 조인을 무조건 수행해야 합니다.

JPQL 에서 N+1 문제가 발생합니다.

→ 그래서 이를 명시적 조인으로 해결하는 방법?

## Lazy Loading

---

> 지연 로딩(Lazy Loading)
> 

`@OneToMany` 나 `@ManyToMany` 는 이가 기본값입니다.

- 필요한 시점에 연관된 객체를 데이터로 불러오는 것 입니다.
- 이도 동일하게 프록시 객체를 사용합니다.

```java
// Order Entity
@ManyToOne(fetch = FetchType.LAZY)
private Member member;

@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
@JoinColumn(name = "delivery_id")
private Delivery deliver
```

위의 주문을 조회하는 로직이 있다면 

- 주문을 조회해도 order 만 조회하는 쿼리만 생성이 되고
- 나머지 연관 객체들을 조회하는 쿼리는 생성되지 않습니다.

### 장단점

초기 로딩 시간이 적고 메모리 소비가 적습니다.

**🤔 단점**

- 그러나 초기화가 지연되면 원하지 않는 순간에 성능 영향을 줄 수 있습니다.

### 주의점

`@OneToOne` 에서 Lazy Loading이라도 경우 연관관계 주인이 누구인지에 따라 N+1 문제가 발생할 수 있습니다.

```java
@Entity
public class Parent {
	...
	@OneToOne(mappedBy = "parent", fetch = FetchType.LAZY)
	private Child child;
}

@Entity
public class Chile {
	...
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Parent parent;
}
```

- 주인 → FetchType.Lazy 가 정상적으로 작동해서 (주인에 FK 가 있으므로) 값이 있으면 프록시를 아니면 null 을 채웁니다. (: N+1 문제 해결)
- `mappedBy` 가 있는 곳 → FetchType.Lazy 가 무시되고 즉시 로딩으로 작동합니다. 자식 테이블에 FK 가 있기에 부모만 조회해서는 자식 객체가 존재하는지 알 수 없기에 N+1 문제가 발생합니다.

### 해결 방법

**1️⃣ Fetch Join을 사용한다. (명시적 조인)**

```java
@Query("select p from Parent p join fetch p.child")
List<Parent> findAllWithChild();
```

- 장단점
    - 데이터를 수정 가능하며 엔티티 객체를 다시 쓸 수 있다.
    - 1:N 관계에서 Fetch Join 시 페이징이 안 먹는다.
    - 불필요한 컬럼까지 조회할 수 있다.

**2️⃣ MapsId 를 사용한다.**

```java
@OneToOne(fetch = FetchType.LAZY)
    @MapsId // Parent의 PK를 자신의 PK이자 FK로 매핑
    @JoinColumn(name = "parent_id")
    private Parent parent;
```

- 장단점
    
    부모의 ID = 자식 ID 이므로 주인이 아닌 쪽에서도 지연로딩이 정상 작동된다.
    
    단, 부모와 자식 간 생명주기가 1:1 로 결합되어야 한다.
    

**3️⃣ DTO Projection** 

엔티티 자체 조회가 아닌 DB 에서 필요 컬럼만 가져와 DTO 생성자에 매핑하는 방법이다.

단, 조회된 결과는 영속성 컨텍스트에서 관리가 되지 않기에 조회 시에 사용하는 것을 더 추천한다.

```java
// 데이터를 담을 순수 자바 Record (또는 DTO 클래스)
public record ParentChildDto(Long parentId, String childName) {}

public interface ParentRepository extends JpaRepository<Parent, Long> {
    // JPQL의 'NEW' 키워드를 사용하여 패키지 경로를 포함한 DTO 생성자 호출
    @Query("select new com.example.dto.ParentChildDto(p.id, c.name) " +
           "from Parent p join p.child c")
    List<ParentChildDto> findAllParentChildDto();
}
```

- 장단점
    
    최적화가 가능한 부분이다. 페이징도 편하다.
    
    그러나 수정이 불가능하고 특정 API 에서만 사용할 수 있어 오히려 재사용이 어려울 수 있다.
    

4️⃣ `@EntityGraph` 로 단일 조인 쿼리 수행 가능