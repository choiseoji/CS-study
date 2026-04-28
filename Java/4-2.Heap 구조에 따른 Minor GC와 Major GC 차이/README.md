## Heap 메모리의 구조(Young, Old Generation)에 따른 Minor GC와 Major GC의 차이점은 무엇인가요?

### JVM Heap 설계 전제
1. 대부분의 객체는 금방 접근 불가능한 상태가 된다.
2. 오래된 객체에서 새로운 객체로의 참조는 아주 적게 존재한다.

➡️ 대부분의 객체는 일회성이며, 메모리에 오랫동안 남아있는 경우는 드물다는 의미!

JVM 메모리는 **객체 생존 기간에 따라** Young, Old 2가지 영역으로 나누게 됨!

Young 영역은 'Eden' 영역과 'Survivor' 영역으로 나누어 관리

![메모리 구조](https://blog.kakaocdn.net/dna/dvtadK/btszyTGRU9K/AAAAAAAAAAAAAAAAAAAAAENJvO3HXFaBbNE-ymgZ3tZWUFlv88F8iqBFS00yQa3J/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1777561199&allow_ip=&allow_referer=&signature=FMSOVzLJKZO0mN8ysP0uSVFyQAc%3D)

### Young 영역
새롭게 생성된 객체가 할당되는 영역
➡️ Young Generation 영역에서 발생하는 GC를 Minor GC라고 부름!!

대부분의 객체가 도달 불가능 상태가 되기 때문에 많은 객체가 young에서 생성되었다가 사라짐

Minor GC는 상대적으로 빈번하게 발생하지만 짧게 끝남!!

가비지 컬렉션의 동작인 Old Generation까지 스캔해서 가비지를 찾지 않도록 해서 더욱 짧은 Stop-the-world 시간 안에 메모리를 회수할 수 있도록 함

- Eden
  - new를 통해 새로 생성된 객체가 위치하는 곳
  - 정기적인 쓰레기 수집 후 살아남은 객체들은 Survivor 영역으로 보냄
- Survivor 0 / Survivor 1
  - 최소 1번의 GC 이상 살아남은 객체가 존재하는 영역
    - Survivor 영역에는 특별한 규칙 존재 -> Survivor 0 또는 Survivor 1 둘 중 하나는 비어있어야 함

      - Survivor 영역이 복사 방식으로 작동하기 때문에!
        ![minor GC 과정](https://blog.kakaocdn.net/dna/cz9lzz/btrIRGyAeEV/AAAAAAAAAAAAAAAAAAAAAPW9-mKpLv2IeUr9BG4ug7R7KQPMbT1NmGM5k8bd4qqH/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1777561199&allow_ip=&allow_referer=&signature=rukmjwTHeXnnXKE6xzf55FRcDY0%3D)
          1. 객체 생성 단계
          ```
          Eden: 새 객체들
          Survivor 0: 비어 있음
          Survivor 1: 비어 있음
          ```
    
        2. Minor GC 발생 (여러번의 GC로 S0 가득 차면 S0 여역 객체도 가바지로 회수)
        ```
        Eden        → 정리됨
        Survivor 0  → 살아남은 객체 저장
        Survivor 1  → 비어 있음
        ```
        3. 다음 GC 발생
        
            Eden + Survivor 0에서 살아남은 객체를 복사해 Survivor 1으로 이동 (이 단계에서 age 값을 1 올림)
        ```
        Eden        → 정리됨
        Survivor 0  → 비어 있음
        Survivor 1  → 살아남은 객체 저장
        ```
        4. 이렇게 Survivor 0과 Survivor 1이 번갈아가면서 사용해 age를 올리고 일정 수준 이상으로 올라가면 promotion

### Old 영역
Young 영역에서 Reachable 상태를 유지하여 살아남은 객체가 복사되는 영역

* promotion: Young 영역에서 Old 영역으로 객체가 이동하는 과정

Young 보다 크게 할당되며, 영역의 크기가 큰 만큼 가비지는 적게 발생

* Old 영역이 크게 할당되는 이유는 Young 영역에 수명이 짧은 객체들은 큰 공간을 필요로 하지 않으며 큰 객체들은 Young 영역이 아니라 바로 Old 영역에 할당 됨

Old 영역에 대한 GC를 Major GC (혹은 Full GC)라고 함!!

* Major GC는 객체들이 계속 Promotion 되어 Old 영역의 메모리가 부족해지면 발생하게 됨

### Permanent 영역
영구적인 구역 -> 생성된 객체들의 정보의 주소값이 저장된 공간

클래스 로더에 의해 load 되는 class, method 등에 대한 Meta 정보가 저장되는 영역

원래 Java7까지는 힙 영역에 존재했었지만 Java 8 버전 이후에 Native Method Stack에 편입되게 

![heap](https://blog.kakaocdn.net/dna/bYh0JQ/btrIIRNPCiZ/AAAAAAAAAAAAAAAAAAAAAL8gTEss5cXP3jsuSEH3WqB81qzaD0uXTAGzs0jEoeD9/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1777561199&allow_ip=&allow_referer=&signature=33Gg6hdcgnwikUeRx%2F0MQPMRY%2Bk%3D)

### Minor GC와 Major GC의 차이
Old 영역에 할당된 메모리가 허용치를 넘게 되면, Old 영역에 있는 모든 객체들을 검사하여 참조되지 않는 객체들을 한꺼번에 삭제하는 Major GC가 실행

Old Generation은 Young Generation에 비해 상대적으로 큰 공간을 가지고 있어, 이 공간에서 메모리 상의 객체 제거에 많은 시간이 걸리게 됨!

### Heap 메모리의 구조(Young, Old Generation)에 따른 Minor GC와 Major GC의 차이점은 무엇인가요?
> Heap 메모리는 보통 객체의 생존 기간에 따라 Young Generation과 Old Generation으로 나뉩니다. 대부분의 객체는 처음에 Young Generation의 Eden 영역에 생성되고, Minor GC가 발생할 때 살아남은 객체는 Survivor 영역으로 이동하면서 age가 증가합니다. 여러 번 살아남은 객체는 Old Generation으로 승격, 즉 promotion됩니다.
> 
> Minor GC는 Young Generation을 대상으로 하는 GC입니다. Eden 영역이 부족할 때 주로 발생하고, Eden과 Survivor 영역에서 아직 참조되는 객체만 남긴 뒤 정리합니다. Young 영역의 객체는 대부분 금방 사라지기 때문에 비교적 자주 발생하지만, 처리 범위가 작아서 보통 빠른 편입니다.
> 
> 반면 Major GC는 Old Generation을 대상으로 하는 GC입니다. Old 영역에는 오래 살아남은 객체들이 모여 있기 때문에 Young 영역보다 크고, 살아있는 객체도 상대적으로 많습니다. 그래서 Major GC는 Minor GC보다 발생 빈도는 낮지만, 한 번 발생하면 시간이 더 오래 걸리고 Stop-the-world 시간이 길어질 수 있습니다.
