## 동기(Sync)와 비동기(Async)의 제어권 반환 및 작업 완료 알림 방식의 차이는 무엇인가요?
>동기와 비동기의 핵심 차이는 호출자가 작업 완료를 직접 신경 쓰는지, 아니면 작업 완료 알림을 나중에 받는지입니다.
>
>동기는 호출자가 작업의 완료 여부를 직접 기다리며, 작업이 완료되어야 다음 흐름으로 넘어갑니다. 즉, 결과가 반환되는 시점을 작업 완료 시점으로 판단합니다.
>
>반면 비동기는 호출자가 작업 완료를 직접 기다리지 않고 자신의 다음 작업을 계속 수행합니다. 작업이 끝나면 호출받은 쪽이나 시스템이 콜백, Future, 이벤트 등의 방식으로 완료를 알려줍니다.
>
>다만 제어권 반환 여부는 동기/비동기보다는 블로킹/논블로킹 관점에 가깝습니다. 따라서 동기/비동기는 작업 완료를 누가 신경 쓰는가와 완료 알림을 어떤 방식으로 받는가의 차이라고 정리할 수 있습니다. 

### 동기 / 비동기
#### 동기
요청한 작업에 대해 완료 여부를 따져 순차적으로 처리하는 것

호출자가 완료 를 직접 기다림 -> 결과를 받는 순간 작업이 완료 되었다고 판단

#### 비동기
요청한 작업에 대해 완료 여부를 따지지 않고 자신의 다음 작업을 그대로 수행하는 것

작업 완료 시 호출 받은 쪽이 나중에 알려줌 -> 작업 완료 알림의 주도권이 호출자가 아니라 작업 수행 쪽에 존재

#### 즉, 동기와 비동기를 구분하는 주요 쟁점은 작업을 순차적으로 수행할지 아닌지!

| 구분              | 동기                  | 비동기                        |
| --------------- | ------------------- | -------------------------- |
| 완료 여부를 누가 신경 쓰나 | 호출자                 | 호출받은 쪽 / 시스템               |
| 완료 알림 방식        | 결과가 반환되면 완료         | 콜백, Future, 이벤트 등으로 나중에 알림 |
| 호출자 흐름          | 결과를 기준으로 다음 작업 진행   | 요청 후 다른 작업 가능              |
| 예시              | `result = method()` | `methodAsync(callback)`    |


### 블로킹 / 논블로킹
#### 블로킹
요청에 대한 결과를 기다리도록 하는 것

#### 논블로킹
결과를 기다리지 않고 다음 작업을 바로 수행하는 것

#### 즉, 블로킹과 논블로킹은 작업 흐름을 막냐 안 막냐의 차이

=> 제어권: 함수의 코드나 프로세스의 실행 흐름을 제어할 수 있는 권리

블로킹과 논블로킹의 명확한 구분 방법: 제어권 반환 방식에서 차이 존재

### 논블로킹과 비동기
- Asynchronous
  - 요청에 처리 완료와 관계 없이 응답, 운영체제에서 응답할 준비가 되면 응답
  - 결과값을 반환하지 않는 방식으로 대기시간 최소화하는 방식
- Non-Blocking
  - 요청에 처리할 수 있으면 바로 응답, 아니면 Error 반환
  - 입출력 작업의 블로킹을 최소화하여 대기시간을 최소화하는 방식

### 블로킹과 동기
- Synchronous
  - 시스템 콜의 리턴을 기다리는 동안 대기 큐에 머물 수도 아닐 수도 있음
  - 호출한 함수가 작업이 완료될때까지 기다림
- Blocking
  - 시스템 콜의 리턴을 기다리는 동안 필수로 대기 큐에 머뭄
  - 제어권이 넘어가서 다른 작업을 수행 못하는 프로세스 또는 스레드를 큐에 저장

### 1. Sync-Blocking
![](https://blog.kakaocdn.net/dna/DH4wp/btsbVd8EPf3/AAAAAAAAAAAAAAAAAAAAAHmNxGLbwyWJ0JZEnlyrOSww25o1BBd7PFsYPvJ3W3gl/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1782831599&allow_ip=&allow_referer=&signature=VOeucJjhInROy%2FQMVmaJmZxmznM%3D)
제어권은 넘어가고, 순서대로 진행

- 함수는 다른 함수의 리턴 값을 고려해 동작
- 다른 함수에게 제어권을 넘겨주고 대기
- 제어권을 넘겨주고 해당 함수가 실행을 완료하여 리턴 값과 제어권을 돌려줄 때까지 대기

### 2. Sync-NonBlocking
![](https://blog.kakaocdn.net/dna/cpRVMD/btsbUpIetpp/AAAAAAAAAAAAAAAAAAAAACMNFfCiim-W2LTBc2bL46EXZZnoO_kOPQlQ6vJ9kv_p/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1782831599&allow_ip=&allow_referer=&signature=TakB%2Fov5dcEdWrhtTJPPMsK65vY%3D)
제어권이 넘어가지는 않았지만, 순차 진행 되어야 하기에 계속적으로 작업 완료 확인

- 다른 함수의 리턴 값을 고려해서 동작
- 함수는 다른 함수에게 제어권을 주지 않고 자신의 코드 실행
- A 함수는 B 함수의 리턴 값이 필요하기 때문에 중간 중간 B에게 함수 실행 완료 했는지 물어봄
- A 함수는 B 함수에게 제어권을 주지 않고 자신의 코드 계속 실행

> Sync Blocking vs Sync NonBlocking
> 
> 성능 차이는 상황에 따라 다르겠지만, 일반적으로 동기 + 논블로킹이 동기 + 블로킹보다 효율적
> 
> 동기 + 논블로킹은 호출하는 함수가 제어권을 가지고 있어서 다른 작업을 병렬적으로 수행할 수 있는 반면 동기 + 블로킹은 호출하는 함수가 제어권을 잃어서 다른 작업을 수행할 수 없기 때문

### 3. Async-Blocking
![](https://blog.kakaocdn.net/dna/bI25a3/btsb5lLFUEx/AAAAAAAAAAAAAAAAAAAAAOvM8LvIuX2g-5pxzsW_0ad6Z6F0cXSU6oKBNK8rtFfo/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1782831599&allow_ip=&allow_referer=&signature=iHNHyFHSt9nYHNxy7FhZ8N%2FCODE%3D)
순서대로 진행되지 않아도 되지만 제어권이 넘어갔기 때문에 호출자는 더 진행하지 못하므로 대기 시간 발생

- 다른 함수의 리턴 값을 고려하지 않고 동작하지만 다른 함수에게 제어권을 넘겨주고 대기
- A 함수는 B 함수의 리턴 값을 신경 쓰지 않고 콜백 함수를 보냄
- A 함수는 자시관 관련 없는 B 함수의 작업이 끝날 때까지 기다려야 함
- B 함수 작업에 관심이 없음에도 불구 하고 제어권 넘김

=> Sync-Blocking과 비교했을 때 이점이 없어서 거의 사용 X

### 4. Async-NonBlocking
![](https://blog.kakaocdn.net/dna/blMhex/btsb6ZuUOdm/AAAAAAAAAAAAAAAAAAAAAD9LdFKEfWvYTjj8c4OzR-UolEJ-nmEyH45KCWhjkTjP/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1782831599&allow_ip=&allow_referer=&signature=H%2B%2FRCuvOAy25In45l9vCyzlqekM%3D)
제어권이 넘어가진 않았지만 순서대로 진행되지 않아도 되기 때문에 호출하고 작업을 하고 있다가 완료 시 콜백

- 함수를 호출하고 호출 할 때 콜백 함수를 함께 줌
- B 작업 끝나면 콜백 함수 실행

![](https://blog.kakaocdn.net/dna/yWAIK/btsID45CQZM/AAAAAAAAAAAAAAAAAAAAADSmqvnt8hv5H96FF8XrUEe1HfGGPaVJ41LiIM5fqG1D/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1782831599&allow_ip=&allow_referer=&signature=Uc3A9Y0%2Fh3Loa%2BCwHx7HmymKJhQ%3D)