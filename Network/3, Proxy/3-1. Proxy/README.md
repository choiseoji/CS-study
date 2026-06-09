## 프록시 서버는 무엇이고 왜 필요한가요? 또한 사용 사례를 설명해보고 본인의 프로젝트에 적용한다면 어떻게 해야할지 고민해보세요

### 프록시 서버
![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdna%2FFVywR%2FbtsIwBwllrD%2FAAAAAAAAAAAAAAAAAAAAABrqeRITd1egoLzvjJFOl4jTSCJiyh6Q6B_L9JHY01us%2Fimg.png%3Fcredential%3DyqXZFxpELC7KVnFOS48ylbz2pIh7yKj8%26expires%3D1782831599%26allow_ip%3D%26allow_referer%3D%26signature%3D6nHvCRksWTlqkoCI4syA5IBuW6A%253D)
클라이언트와 인터넷 서버 사이에서 데이터를 중계하는 중간 서버


### 프록시 서버 목적
1. **캐시 데이터**

   프록시 서버 중 일부는 프록시 서버에 요청된 내용을 캐시를 사용해 저장

   -> 캐시에 저장된 내용에 대한 재요청의 경우 시간 절약 및 트래픽 감소로 네트워크 병목 현상 방지

2. **보안 목적**

   프록시 서버를 중간 경유하면 IP를 숨길 수 있고 프록시 서버 자체를 방화벽으로도 사용 가능 (프록시 방화벽)

   추가로 엑세스 제어 목록을 사용해 특정 사용자나 그룹의 네트워크 리소스에 대한

3. **콘텐츠 필터링**

   특정 웹 사이트나 콘텐츠에 대한 접근을 제어

4. **접근 제어**

   네트워크 접근을 제어하고 모니터링

   -> 비정상적인 트래픽을 감지하고 차단

5. **로깅 및 모니터링**

   모든 요청과 응답을 기록하여 네트워크 사용 현황을 모니터링하고 분석


