## Spring Security란?
> Spring Security는 Spring 기반 애플리케이션에서 인증과 인가를 처리하기 위한 보안 프레임워크입니다. 단순히 로그인 기능만 제공하는 것이 아니라, 요청이 컨트롤러에 도달하기 전에 SecurityFilterChain을 통해 인증, 인가, CSRF 같은 보안 처리를 수행합니다.
> 
> 인증은 사용자가 누구인지 확인하는 과정이고, 인가는 인증된 사용자가 특정 API나 리소스에 접근할 권한이 있는지 판단하는 과정입니다. 인증이 성공하면 사용자 정보가 Authentication 객체로 만들어지고, 이 정보가 SecurityContextHolder에 저장되어 이후 요청 처리나 권한 검증에 사용됩니다.
> 
> 따라서 Spring Security를 사용하면 로그인, JWT 인증, OAuth2 로그인, URL별 권한 제어, 메서드 단위 권한 제어 같은 보안 로직을 애플리케이션 전반에 일관되게 적용할 수 있습니다.

### Spring Security
Spring 기반의 애플리케이션의 보안 (인증과 권한, 인가 등)을 담담하는 스프링 하위 프레임워크

! '인증'과 '권한'에 대한 부분을 Filter 흐름에 따라 처리!

![스프링 시큐리티 동작 과정 예시](https://blog.kakaocdn.net/dna/MxcuU/btsIKhwNlDT/AAAAAAAAAAAAAAAAAAAAACWQCgisKTt1nXSmuu5oTgGOs2OmV66VheaSgh9zS5wu/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1780239599&allow_ip=&allow_referer=&signature=B8R7%2BAmibAvqSKsWajoxljUnM7k%3D)

### Spring Security 주요 기능
1. 인증
   : 사용자의 신원을 확인하는 과정으로, 다양한 인증 메커니즘(폼 로그인, OAuth, OpenID 등)을 지원 

2. 인가
   : 사용자가 애플리케이션의 특정 자원에 접근할 수 있는권한을 관리!
    : 역할 기반 권한 부여, URL 기반 권한 부여 등을 제공

3. 보안 필터
   : 요청에 대해 여러 보안 필터를 적용하여 보안성 강화
    : ex) UsernamePasswordAuthenticationFilter'는 사용자의 아이디와 비밀번호 검증

4. 암호화
   : 비밀번호 등의 민감한 데이터를 안전하게 저장할 수 있도록 암호화 기능 제공

5. 세션 관리
   : 세션 고정 공격 방어, 동시 세션 제어 등의 기능 포함

6. 공격 방어
   : CSRF, XSS, 세션 하이재킹 등 다양한 웹 공격을 방어하는 기능을 제공
