## URI와 URL 차이점

URI는 인터넷 상의 자원을 식별하기 위한 상위 개념이고, URL은 자원의 위치를 통해 자원을 식별하는 하위 개념입니다. 즉 URI는 "무엇을 식별할 것인가"에 초점이 있고, URL은 "어디에 있는가"에 초점이 있습니다.

</br>
</br>

**URI(Uniform Resource Identifier)**

- 인터넷 상의 자원을 **식별**하기 위한 통합된 식별자
- 자원을 식별하는 모든 방법을 포함하는 상위 개념
- URL과 URN은 모두 URI의 하위 개념

</br>

**URL(Uniform Resource Locator)**

- **자원의 위치**(Location)를 통해 자원을 식별하는 방식
- 자원이 어디에 존재하는지 주소를 나타냄
- https : 접근 프로토콜
- www.example.com : 서버 주소
- /users/1 : 자원의 경로

</br>

**URN(Uniform Resource Name)**

- 자원의 위치와 관계없이 고유한 이름(Name)으로 식별하는 방식
- 자원의 위치가 변경되어도 식별자는 유지됨

```
urn:isbn:9780134685991
```

</br>

**예시**

```
http://www.naver.com/index.html?page=111&id=111
```

- http://www.naver.com/index.html은 URL → index.html의 위치까지 표현
- http://www.naver.com/index.html?page=111&id=111은 URI → 사용자가 원하는 자원에 도달하기 까지 필요한 식별자