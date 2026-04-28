## GC(Garbage Collection)의 기본 동작 원리(Stop-the-world, Mark and Sweep)에 대해 설명해주세요.

### GC(Garbage Collection)
메모리 기법 중의 하나로, 프로그램이 동적으로 할당했던 메모리 영역 중에서 필요 없게 된 영역을 해제하는 기능

➡️ 자동으로 메모리 관리

> 자바의 메모리 관리 방법 중 하나로 JVM의 Heap 영역에서 동적으로 할당했던 메모리 중 필요없게 된 메모리 객체를 모아 주기적으로 제거하는 프로세스

### 가비지(Garbage)
Heap 영역의 객체 중 Stack에서 도달 불가능한(Unreachable) 객체들은 가비지 컬렉션의 대상
![JVM의 메모리 영역](https://blog.kakaocdn.net/dna/c80g54/btrIT9fcYdX/AAAAAAAAAAAAAAAAAAAAAOiEoC_94bX3B6RxR8v9zgl1Rr-9OaOYd9X8yAniLCc1/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1777561199&allow_ip=&allow_referer=&signature=TrlDf2tj9c3YyQsjxw%2FCNi8Iqis%3D)
```java
Product product = new Product("노트북", 1500000);
sellTo(product);
product = null; 
```

```java
Person person = new Person("홍길동", 30);
person.sayHello();

person = new Person("김홍길동", 22);
person.sayHello();
```

new Product("노트북", 1500000) 객체와 new Person("홍길동", 30)는 메모리 어딘가(Heap 영역)에 할당

이 메모리 영역을 가리키던 product는 null이 되고, person은 새로운 객체를 참조

➡️ 이전에 참조되었던 new Product("노트북", 1500000) 객체와 new Person("홍길동", 30)는 어떤한 경로로도 참조되지 않는

**"도달 불가능한 (Unreachable)"** 상태가 됨!!

이런 객체를 가비지로 판단해 회수

### STW(Stop The World)
가비지 컬렉션의 단점...

자동으로 처리해줄 때 메모리가 언제 해제되는지 정확하게 알 수 없어 제어하기가 어렵다!!

또한, 가비지 컬렉션이 동작하는 동안에는 **다른 동작을 멈추기** 때문에 오버헤드 발생!

이게 바로 Stop The World!!

> GC를 수행하기 위해 JVM이 프로그램 실행을 멈추는 현상을 의미하고, GC가 작동하는 동안 GC 관련 Thread를 제외한 모든 Thread는 머추게 되어 서비스 이용에 차질이 생길 수 있다.
> 
> 따라서, 이 시간을 최소화 시키는 것이 쟁점이다.
> ![stop-the-world-시각화](https://blog.kakaocdn.net/dna/UGGwm/btrIRGekM2e/AAAAAAAAAAAAAAAAAAAAALiE8u5kvuMvQrahktUlv2M-L862ZsX1R4gKMB-H6EKw/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1777561199&allow_ip=&allow_referer=&signature=mQZ2nS%2Fmyn%2BFbuhZ3uOpx115%2Fs8%3D)
> 

➡️ 가비지 컬렉션이 너무 자주 실행되면 소프트웨어 성능 하락의 문제

_ex) 익스플로러는 가비지 컬렉션을 너무 자주 실행해 성능 문제를 일으켰었음~_

즉, 자바를 언어로 사용하는 개발자들의 주요 쟁점 -> GC 튜닝 (GC를 최적화 하는 일!)

### Mark and Sweep
다양한 GC에서 사용되는 객체를 분류하는 내부 알고리즘
![GC 동작 과정](https://blog.kakaocdn.net/dna/cgSNa1/btrIVgyOGYA/AAAAAAAAAAAAAAAAAAAAADVYIHQMN0XJvlNfQVCD3Z7rZohK28N8p18HpJd0Kwry/img.gif?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1777561199&allow_ip=&allow_referer=&signature=mv9hSrnGMYCrN20Ro8y3t9cTFGA%3D)
1. **Mark**: Root Space로부터 그래프 순회를 통해 연결된 객체들을 찾아내어 각각 어떤 객체를 참조하고 있는지 찾아서 마킹
2. **Sweep**: 참조하고 있지 않는 객체 (Unreachable)들을 찾아 Heap에서 제거
3. **Compact**: Sweep 후 분산된 객체들을 Heap의 시작 주소로 모아 메모리가 할당된 부분과 그렇지 않은 부분으로 압축

   _(가비지 컬렉터의 종류에 따라 진행하지 않는 경우도 존재)_

GC의 Root: 

Mark And Sweep 방식은 루트로부터 해당 객체체 접근이 가능한지가 해제의 기준이 됨!

JVM GC에서의 Root는 Heap 영역의 객체를 참조할 수 있는 출발점들을 의미

대표적으로 현재 실행 중인 스레드의 Stack 영역에 있는 지역 변수와 매개변수, static 변수, Method Area 또는 Metaspace의 클래스 관련 참조, Native Method Stack에서 JNI를 통해 참조 중인 객체 등
![Root space](https://blog.kakaocdn.net/dna/cgBYrs/btrI1VuVcpW/AAAAAAAAAAAAAAAAAAAAAIrdehR7OibsqfZh2MKEtkSI-fByc-JfWUaL-ddozcCF/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1777561199&allow_ip=&allow_referer=&signature=x3bRNk8FWRpyPcucjCFuUZ8w%2BLw%3D)

### GC(Garbage Collection)의 기본 동작 원리(Stop-the-world, Mark and Sweep)에 대해 설명해주세요.
> Garbage Collection은 프로그램에서 더 이상 참조되지 않는 객체를 자동으로 찾아 메모리에서 해제하는 메모리 관리 방식입니다.
>
>기본 동작은 크게 Stop-the-world, Mark, Sweep 단계로 설명할 수 있습니다. 먼저 Stop-the-world는 GC가 정확하게 객체 참조 관계를 확인하기 위해 애플리케이션 실행을 잠시 멈추는 단계입니다. 이 시간 동안 일반 스레드는 중단되고, GC 스레드가 메모리 정리를 수행합니다.
>
>그다음 Mark 단계에서는 GC Root라고 불리는 stack, static 변수, JNI 참조 등에서 출발해 도달 가능한 객체들을 탐색하고, 아직 사용 중인 객체로 표시합니다.
>
>마지막 Sweep 단계에서는 Mark되지 않은 객체, 즉 어디에서도 참조되지 않아 더 이상 사용할 수 없는 객체들을 메모리에서 회수합니다.
>
>즉, GC는 사용 중인 객체와 사용하지 않는 객체를 구분한 뒤, 불필요한 객체를 제거해 메모리 누수를 줄여줍니다. 다만 GC 중에는 Stop-the-world로 인해 애플리케이션이 잠시 멈출 수 있기 때문에, 응답 지연이나 성능 오버헤드가 발생할 수 있습니다.