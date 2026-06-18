## Java 의 HashMap 에서는 어떤 방법으로 해시 충돌을 해결하는지 아시나요?

Java의 HashMap은 분리 연결법(체이닝) 방식으로 해시 충돌을 해결합니다. 충돌이 발생하면 동일한 버킷에 데이터를 LinkedList 형태로 저장하고, 조회 시에는 해당 버킷 내에서 Key를 비교하며 탐색합니다. Java 8부터는 충돌이 많아져 노드 수가 일정 개수 이상이 되면 Red-Black Tree로 변환하여 탐색 성능을 O(n)에서 O(log n)으로 개선합니다.

해시 충돌 방식으로 크게 분리 연결법(Separate Chaining)과 개방 주소법(Open Addressing)이 있다.

- 분리 연결법
    - 충돌이 발생하면 같은 버킷에 연결해서 저장하는 방식
    - Java의 HashMap은 이 방식으로 충돌을 해결함
- 개방 주소법
    - 충돌이 발생하면 다른 버킷을 찾아 저장하는 방식
    - 대표적으로 선형 탐사, 이차 탐사, 이중 해싱 방법이 있다고 함. (아래 링크 참고)
    - https://innovation123.tistory.com/103#개방 주소법(Open Address)-1-3-1-3)

</br>

**Java 7 까지는 LinkedList로 해결**

```
bucket[3]
   ↓
(key=A, value=1)
   ↓
(key=B, value=2)
   ↓
(key=C, value=3)
```

- 동일한 버킷에 저장된 데이터를 순차적으로 탐색하여 원하는 Key를 찾음
- 하지만 충돌이 많이 발생하면 LinkedList 길이가 길어져 탐색 성능이 O(n)까지 저하될 수 있음

</br>

**Java 8 이후는 Red-Black Tree 사용**

```
bucket[3]
       A
      / \
     B   D
        /
       C
```

- 충돌이 많이 발생한 버킷을 LinkedList 대신 Red-Black Tree로 변환해서 사용
    - 버킷 내 노드 수가 8개 이상 + HashMap의 전체 용량(버킷의 수)이 64 이상일 때 변환
    - 노드 수 8개는 Java 개발팀이 다양한 성능 테스트와 벤치마크를 통해 선택한 경험적인 임계값임.
    - 반대로, 노드 수가 6개 이하가 되면 다시 LinkedList로 전환
- 탐색 성능을 O(n)에서 O(logN)으로 개선