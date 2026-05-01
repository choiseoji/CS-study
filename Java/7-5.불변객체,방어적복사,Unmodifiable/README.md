### Q. 불변 객체를 왜 사용하며, 방어적 복사와 Unmodifiable 컬렉션의 차이는 무엇인가요?

불변 객체는 생성 이후 상태가 변경되지 않기 때문에 멀티스레드 환경에서 동기화 없이 안전하게 사용할 수 있고, 여러 계층에서 공유하더라도 사이드 이펙트가 없어 예측 가능성과 데이터 안정성을 높일 수 있어 사용합니다. 또한 값이 변하지 않기 때문에 Map의 key나 Set의 요소로도 안전하게 사용할 수 있습니다.

방어적 복사는 새로운 객체를 생성하여 내부 상태와 외부를 완전히 분리하는 방식으로, 외부에서 수정하더라도 내부에 영향을 주지 않습니다. 반면 Unmodifiable 컬렉션은 기존 컬렉션을 그대로 참조하면서 수정만 막는 읽기 전용 뷰이기 때문에, 내부 원본이 변경되면 그 변경이 외부에도 반영된다는 차이가 있습니다.

</br>
</br>

### 💡 불변 객체 (Immutable Object)

생성 이후 내부 상태가 절대 변경되지 않는 객체

→ 모든 필드가 생성 시점에 초기화되고, 이후에 변경되지 않는 객체

</br>

**특징**

- setter 없음
- 모든 필드가 변경 불가능 (`final`)
- 상태 변경이 필요하면 → 새로운 객체 생성
- 외부에 내부 상태를 노출할 때 → 가변 객체일 때만 방어적 복사 필요
- final class와 모든 필드에 final을 붙여서 불변 객체를 만들 수 있음

**예시**

```java
public final class User {

    private final String name;           // 불변 객체
    private final List<String> hobbies;  // 가변 객체 (방어적 복사 필요)

    public User(String name, List<String> hobbies) {
        this.name = name;
         // 외부에서 전달된 List를 그대로 참조하지 않고 복사하여 내부 상태 보호
        this.hobbies = new ArrayList<>(hobbies);
    }

    public String getName() {
        return name; // String은 불변 객체라서 그냥 반환해도 됨
    }

    public List<String> getHobbies() {
    
		    // 내부 List를 그대로 반환하지 않고 복사해서 외부 변경으로부터 보호
        return new ArrayList<>(hobbies);
    }
}
```

</br>

**왜 불변 객체를 사용해야할까??**

- 스레드 안정성 : 상태 변경이 없어 동기화 없이 안전
- 예측 가능성 : 상태가 변하지 않아 디버깅이 쉬움
- 공유 안정성 :  여러 곳에서 참조해도 부작용 없음
- Map의 key / Set 요소로 안전하게 사용 가능 (hash 값 유지)

<details>
<summary>Map의 동작 방식</summary>

    Map<Key, Value>
    
    1. key.hasCode() 계산
    2. 그 hash 값으로 저장 위치 결정
    
    그래서 가변 객체를 Key로 사용하면, 값이 변경되면서 hash 변경 → 위치 변경 → 기존 값을 못 찾게 된다.
</details>
    
</br>

**언제 사용?**

- DTO / 응답 객체
- 캐시 데이터 (Redis / 메모리 캐시에 객체 저장)
- 이벤트 / 메시지 객체 (이벤트는 기록인데 값이 바뀌면 데이터 신뢰성 깨짐)
- 멀티 쓰레드 공유 데이터

</br>
</br>

### 💡 방어적 복사

외부에서 전달된 객체나 내부 객체가 의도치 않게 변경되는 것을 막기 위해 복사해서 사용하는 것

→ 외부와 내부가 동일한 객체를 참조하지 않도록 복사하여 상태 변경 가능성을 차단하는 것

```java
public class BadUser {

    private final List<String> hobbies;

    public BadUser(List<String> hobbies) {
        this.hobbies = hobbies; // 그대로 참조 (문제 시작)
    }

    public List<String> getHobbies() {
        return hobbies; // 그대로 반환 (문제 확정)
    }
}
```

위와 같이 생성자에서 전달받은 가변 객체를 그대로 참조해서 반환한다면

```java
public class Main {
    public static void main(String[] args) {

        List<String> hobbies = new ArrayList<>();
        hobbies.add("coding");

        BadUser user = new BadUser(hobbies);

        // 1. 생성자 이후 외부에서 변경
        hobbies.add("hack");

        System.out.println(user.getHobbies());
        // [coding, hack] -> 내부 상태 깨짐

        // 2. getter로 꺼내서 변경
        user.getHobbies().add("exploit");

        System.out.println(user.getHobbies());
        // [coding, hack, exploit] -> 완전히 뚫림
    }
}
```

외부에서 동일한 참조를 공유하고 있기 때문에, 외부에서의 변경이 내부 상태에도 영향을 미친다.

→ 그래서 불변객체의 의미가 사라짐

인자로 전달된 가변 객체를 그대로 참조하지 않고 복사하여 내부에 저장하고, 반환 시에도 복사하여 외부에서 내부 상태를 변경하지 못하도록 하는 것을 방어적 복사라고 한다.

```java
public final class User {

    private final List<String> hobbies;

    public User(List<String> hobbies) {
        this.hobbies = new ArrayList<>(hobbies);  // 방어적 복사
    }

    public List<String> getHobbies() {
        return new ArrayList<>(hobbies);   // 방어적 복사
    }
}
```

</br>
</br>

### **💡 Unmodifiable 컬렉션**

기존 컬렉션을 기반으로 수정 연산만 막은 읽기 전용 뷰이다.

외부에서 수정하려고 하면 Exception이 발생하지만, 내부에서는 수정이 가능하다.

→ 수정은 막지만, 내부 컬렉션을 그대로 참조하고 있기 때문에 내부 변경이 외부에도 반영된다.

```java
public class User {

    private final List<String> hobbies = new ArrayList<>();

    public User() {
        hobbies.add("coding");
    }

    public List<String> getHobbies() {
    
        // Unmodifiable 컬렉션으로 반환
        return Collections.unmodifiableList(hobbies);
    }

    // 내부에서 변경하는 메서드
    public void addHobby(String hobby) {
        hobbies.add(hobby);
    }
}
```

```java
public class Main {
    public static void main(String[] args) {

        User user = new User();

        List<String> list = user.getHobbies();

        // 외부에서 수정 시도
        list.add("hack");  
        // UnsupportedOperationException 발생

        // 내부에서 수정
        user.addHobby("game");

        System.out.println(user.getHobbies());
        // [coding, game]  ← 값 바뀜 (외부에서 내부 컬렉션을 그대로 참조하기 때문에 변경사항 확인 가능)
    }
}
```

</br>


<details>
<summary>참고: List.of()</summary>
    
    내부적으로도 변경이 불가능한 구조를 가지기 때문에 원본 변경 영향도 받지 않는 진짜 불변 리스트
    
    ```java
    List<String> list = List.of("A", "B", "C");
    
    list.add("D"); // -> UnsupportedOperationException 발생
    ```

</details> 

**방어적 복사 VS Unmodifiable Collection VS List.of() 비교**

| 구분 | 방어적 복사 | Unmodifiable | List.of() |
| --- | --- | --- | --- |
| 객체 | 새로 생성 | 기본 참조 | 새로 생성 |
| 생성 방식 | 복사 | 래핑 (view) | 불변 객체 생성 |
| 외부 수정 | 복사본에서 가능 | X | X |
| 내부 변경 영향 | X | O | X |
| 참조 공유 | X | O | X |
| 진짜 불변 | O | X (내부에서 수정 가능) | O |