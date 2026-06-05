## TCP 3, 4 way handshake의 차이점에 대해서 설명해주세요.
>TCP 3-way handshake와 4-way handshake의 차이는 목적과 패킷 흐름입니다.
> 
> 3-way handshake는 연결을 시작하기 위한 과정이고, 클라이언트와 서버가 서로 통신 가능한 상태인지 확인한 뒤 초기 Sequence Number를 교환합니다. 흐름은 SYN → SYN+ACK → ACK 순서로 진행되고, 이후 ESTABLISHED 상태가 되어 데이터를 주고받습니다.
> 
> 반면 4-way handshake는 연결을 종료하기 위한 과정입니다. TCP는 양방향 통신이기 때문에 한쪽이 FIN을 보내도 반대쪽은 아직 보낼 데이터가 남아 있을 수 있습니다. 그래서 FIN → ACK → FIN → ACK 순서로 각각의 송신 종료를 따로 확인합니다.
>
> 즉, 3-way는 연결 수립, 4-way는 연결 종료이며, 종료는 양방향을 각각 닫아야 해서 4단계가 필요합니다. 


### TCP 3 way handshake
TCP/IP 프로토콜을 이용해 통신을 하는 응용 프로그램이 데이터를 전송하기 전에 먼저 정확한 전송을 보장하기 위해 사전에 세션을 수립하는 과정

#### TCP 3 way handshake 역할
- 양쪽 모두 데이터를 전송할 준비가 된 것을 보장하고, 데이터 전달 시작 전 다른 쪽이 준비되었다는 것을 알 수 있도록 함
- 양쪽 모두 상대편에 대한 초기 순차일련번호를 얻을 수 있도록 함

#### TCP 3 way handshake 과정
![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F225A964D52F1BB6917)
1. 클라이언트는 서버에 접속을 요청하는 SYN 패킷을 보냄
   - 클라이언트는 SYN을 보내고 SYN/ACK 응답을 기다리는 SYN_SENT 상태가 됨!
2. 서버는 SYN 요청을 받고 클라이언트에게 요청을 수락하는 ACK과 SYN flag가 설정된 패킷을 발송하고 클라이언트가 다시 ACK으로 응답하길 기다림
    - 서버는 SYN_RECEIVED 상태가 됨
3. 클라이언트는 서버에게 ACK을 보내고 이후부터 연결이 이루어지고 데이터가 오가게 됨!
    - 이때 서버의 상태가 ESTABLISHED

### TCP 4 way handshake
세션을 종료하기 위해 수행되는 절차

#### TCP 4 way handshake 과정
![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F2152353F52F1C02835)
1. 클라이언트가 연결을 종료하겠다는 FIN 플래그 전송
2. 서버는 확인 메시지를 보내고 통신이 끝날때까지 기다리는데 이 상태가 TIME_WAIT 상태
3. 서버가 통신이 끝났으면 연결이 종료되었다고 클랄이언트에게 FIN 플래그 전송
4. 클라이언트는 확인했다는 메시지 보냄