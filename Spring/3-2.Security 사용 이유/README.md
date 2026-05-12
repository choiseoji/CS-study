## Spring 인증 구현 시 Security 를 사용하는 이유에 대해서 알려주세요.
> Spring Security를 사용하는 이유는 Spring 환경에서 인증과 인가를 일관성 있고 안전하게 처리하기 위해서입니다.
> 
> 직접 구현하면 컨트롤러마다 로그인 여부나 권한 체크를 반복해야 하고, 보안 검증이 누락될 위험이 있습니다. Spring Security는 요청이 컨트롤러에 도달하기 전에 필터 체인에서 인증과 인가를 처리하기 때문에 보안 로직을 중앙화할 수 있습니다.
> 
> 또한 Spring의 IoC/DI 구조와 잘 통합되어 있어서 UserDetailsService, AuthenticationProvider, PasswordEncoder 같은 컴포넌트를 Bean으로 관리하고 확장할 수 있습니다.
> 
> 여기에 세션 관리, CSRF 방어, 비밀번호 암호화, OAuth2, JWT 같은 검증된 보안 기능을 제공하기 때문에, Spring 프로젝트에서는 Spring Security를 기반으로 보안 기능을 구성하는 것이 효율적이고 안정적입니다.

### Spring 생태계에 적합
Spring Security는 보안 기능을 별도 라이브러리처럼 억지로 붙이는 게 아니라, Spring의 Bean, DI, AOP, 어노테이션 설정 방식 안에서 자연스럽게 보안 기능을 구성 가능

예시 1) PasswordEncoder를 Bean으로 등록해서 주입받아 사용
```java
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```
```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
            request.getUsername(),
            encodedPassword
        );

        userRepository.save(user);
    }
}
```

직접 구현한다면 비밀번호 암호화 객체를 어디서 만들지, 어떻게 공유할지, 테스트에서는 어떻게 교체할지 고민해야 하는데, Spring Security에서는 PasswordEncoder를 Bean으로 등록하고 DI로 주입받아 사용

### 보안 로직 중앙화
Spring Security를 사용하지 않으면 각 컨트롤러나 서비스에서 로그인 여부, 권한 체크, 예외 처리를 직접 작성
```java
if (!user.isLogin()) {
    throw new UnauthorizedException();
}

if (!user.hasRole("ADMIN")) {
    throw new ForbiddenException();
}
```
위와 같은 코드가 여러 곳에 반복될 가능성 높음 -> 중복 코드가 많아지고 특정 API에서 권한 검사 빠뜨릴 위험도 존재

! Spring Security 사용하면 요청이 컨트롤러에 도달하기 ㅈㄴ에 Security FilterChain에서 인증과 인가를 먼저 처리 가능

=> 보안 정책을 애플리케이션 전반에 일관되게 적용

### 인증과 인가를 선언적으로 관리
Spring Security를 사용하면 URL별, 메서드별 접근 권한을 설정 가능

```java
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/user/**").authenticated()
.anyRequest().permitAll()
```

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long userId) {
    ...
}
```
설정이나 어노테이션을 통해 선언적 관리 가능

### 검증된 보안 기능 사용 가능
Spring Security 제공 기능

- 로그인 인증
- 권한 기반 접근 제어
- 세션 관리
- CSRF 방어
- 비밀번호 암호화
- Remember-me
- OAuth2 로그인
- JWT 기반 인증
- 보안 예외 처리
- HTTP 보안 헤더 설정

직접 구현하면 누락이나 취약점이 생길 가능성이 높음

### 확장성
Spring Security는 프로젝트 요구사항에 맞게 확장 가능

예를 들어 DB 기반 로그인은 UserDetailsService를 구현해서 처리할 수 있고, JWT 인증이 필요하면 커스텀 필터를 추가할 수 있음!

OAuth2 로그인이 필요하면 Spring Security의 OAuth2 Client 기능을 활용할 수 있음!

즉, 기본 보안 구조는 Spring Security에 맡기고, 서비스에 특화된 부분만 직접 구현할 수 있음.