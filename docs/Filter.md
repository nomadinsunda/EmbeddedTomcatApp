# 🧰 Jakarta Servlet Filter 완벽 가이드

### 요청과 응답을 가로채는 다재다능한 웹 필터링 메커니즘

---

## 📌 1. Filter란 무엇인가?

`jakarta.servlet.Filter`는 **HTTP 요청과 응답을 필터링할 수 있도록 해주는 인터페이스**입니다.
즉, 클라이언트 → 서블릿/정적 리소스 로 요청이 전달되기 전이나 응답이 클라이언트로 반환되기 전에 **중간에서 개입할 수 있는 구조**입니다.

> 📌 필터는 서블릿과 달리 **출력도 직접 생성하지 않고**, 특정 리소스를 처리하는 **전·후처리 역할**을 수행합니다.

---

## 🧠 2. 어떤 상황에서 사용하는가?

Filter는 다양한 웹 애플리케이션 요구사항을 처리할 수 있습니다. 예:

| 활용 시나리오  | 설명                |
| -------- | ----------------- |
| ✅ 인증/인가  | 로그인 인증, 권한 검사     |
| ✅ 로깅     | 요청 정보 로깅, 트랜잭션 로그 |
| ✅ 인코딩    | 요청/응답 문자셋 처리      |
| ✅ 압축     | GZIP 압축 등         |
| ✅ XSS 방어 | 입력값 필터링           |
| ✅ 응답 변형  | 이미지 변환, XSLT 적용 등 |

---

## ⚙️ 3. Filter 인터페이스 구조

```java
public interface Filter {
    void init(FilterConfig filterConfig) throws ServletException;
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException;
    void destroy();
}
```

### ✅ init()

* 필터가 초기화될 때 한 번만 호출
* 초기화 파라미터 또는 ServletContext 접근 가능

### ✅ doFilter()

* 필터링 작업의 핵심
* 요청(Request) 전 처리, 응답(Response) 후 처리 가능
* 반드시 `chain.doFilter(request, response)`를 호출해야 다음 필터 또는 서블릿으로 흐름이 이동함

### ✅ destroy()

* 웹 애플리케이션 종료 시 호출
* 리소스 정리 용도

---

## 🔄 4. 요청 흐름에서 Filter의 위치

```text
클라이언트 요청
   ↓
[Filter 1]
   ↓
[Filter 2]
   ↓
[Servlet or JSP]
   ↑
[Filter 2]
   ↑
[Filter 1]
   ↑
클라이언트 응답
```

* 필터 체인은 **스택 구조**처럼 동작합니다.
* 앞에서부터 순서대로 요청을 처리하고, **응답은 역순으로 처리**됩니다.

---

## 🧪 5. 실전 예제: 요청 로깅 필터

```java
@WebFilter("/*") // 모든 요청에 적용
public class LoggingFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println("요청 URI: " + req.getRequestURI());
        
        chain.doFilter(request, response); // 다음 필터 또는 서블릿 호출
        
        System.out.println("응답 완료");
    }
}
```

---

## 📝 6. web.xml 설정 방식 (Servlet 3.0 이하 또는 명시적 등록)

```xml
<filter>
    <filter-name>encodingFilter</filter-name>
    <filter-class>com.example.EncodingFilter</filter-class>
</filter>

<filter-mapping>
    <filter-name>encodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

---

## 🧩 7. ServletContext와 FilterConfig

* `init(FilterConfig config)` 메서드에서 `ServletContext`를 사용할 수 있음:

```java
public void init(FilterConfig config) {
    ServletContext ctx = config.getServletContext();
    String value = config.getInitParameter("logLevel");
}
```

* 이를 통해 설정값 로딩, 공통 리소스 접근, 컨텍스트 정보 활용이 가능

---

## 🧰 8. 대표적인 Filter 활용 사례

| 이름           | 설명                                      |
| ------------ | --------------------------------------- |
| ✅ 인증 필터      | JWT 토큰, 세션 검사 등                         |
| ✅ 문자셋 필터     | `request.setCharacterEncoding("UTF-8")` |
| ✅ 로깅 필터      | 요청 URI, IP, 소요 시간 기록                    |
| ✅ 압축 필터      | GZIP 압축 후 전송                            |
| ✅ 이미지 필터     | PNG → JPEG 변환 등                         |
| ✅ 보안 필터      | XSS, SQL Injection 예방 전처리               |
| ✅ Mime 타입 필터 | 요청/응답 컨텐츠 타입 조작                         |
| ✅ 트랜잭션 필터    | 요청 단위로 트랜잭션 처리                          |

---

## 🚫 9. 주의할 점

* `chain.doFilter()`를 호출하지 않으면 **요청 흐름이 끊어집니다.**
* 응답 객체를 조작할 때는 **출력 스트림이 이미 닫혔을 가능성**도 고려해야 합니다.
* 필터 순서를 조절해야 할 경우 `@WebFilter` 사용 시 `@Order` 또는 web.xml에서 순서를 명시해야 합니다.

---

## ✅ 마무리

`jakarta.servlet.Filter`는 웹 애플리케이션에서 **공통 기능을 수직적으로 분리**하고, 요청/응답을 전방위로 제어할 수 있게 해주는 매우 강력한 도구입니다. 특히 인증, 로깅, 보안, 리소스 처리 등에서 거의 필수적으로 활용됩니다.

오늘날에는 Spring Security, CORS 설정, 로깅 시스템 등이 자체 필터 체인을 구현하고 있으며, **Spring Boot에서도 Filter는 여전히 핵심 확장 지점**입니다.


