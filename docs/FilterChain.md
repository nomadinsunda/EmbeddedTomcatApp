# 🔄 jakarta.servlet.FilterChain 완벽 가이드

### 필터 체인의 핵심 제어 객체, 요청 흐름을 다음 단계로 넘기는 연결자

---

## 📌 1. FilterChain이란?

`FilterChain`은 **서블릿 컨테이너가 제공하는 객체**로서, 필터가 **자신 다음의 필터나 최종 서블릿으로 요청을 전달할 수 있게 해주는 인터페이스**입니다.

> 🔗 말 그대로 “필터들의 연결(chain)”이며, 현재 필터가 끝난 뒤에 다음 처리 단계(다음 필터 or 서블릿)를 호출하는 역할을 합니다.

---

## 📦 2. 기본 구조

```java
public interface FilterChain {
    void doFilter(ServletRequest request, ServletResponse response)
        throws IOException, ServletException;
}
```

* `doFilter()` 메서드가 핵심입니다.
* 호출하지 않으면 **다음 필터나 서블릿으로 요청이 절대 전달되지 않습니다.**
* 필터 안에서는 이 메서드를 호출하여 **제어권을 다음으로 넘깁니다.**

---

## 🔁 3. 동작 방식

예를 들어, 다음과 같은 필터 체인이 있다고 가정해봅시다:

```
Client Request
   ↓
[LoggingFilter]
   ↓
[AuthFilter]
   ↓
[CompressionFilter]
   ↓
[Servlet or JSP]
```

각 필터는 다음과 같이 동작합니다:

```java
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
    // 전처리
    chain.doFilter(req, res);  // 다음 필터 또는 서블릿으로 이동
    // 후처리
}
```

> **전처리**는 `chain.doFilter()` 호출 **이전**,
> **후처리**는 `chain.doFilter()` 호출 **이후**에 수행됩니다.

---

## 🔍 4. 실제 예제

```java
@WebFilter("/*")
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        System.out.println("📥 요청 수신: " + ((HttpServletRequest) request).getRequestURI());

        chain.doFilter(request, response); // 반드시 호출해야 다음으로 전달됨

        System.out.println("📤 응답 완료");
    }
}
```

---

## ⚠️ 5. 주의 사항

| 실수                          | 문제점                                           |
| --------------------------- | --------------------------------------------- |
| `chain.doFilter()`를 호출하지 않음 | 요청이 다음 필터나 서블릿으로 전달되지 않음 = 요청 중단              |
| 예외 발생 시 `doFilter()` 호출 누락  | 응답 자체가 되지 않거나 블로킹 상태 발생 가능                    |
| 스레드 불안정 코드 작성               | 모든 필터는 **멀티스레드 환경**에서 동작하므로 공유 자원 접근 시 동기화 필수 |

---

## ✅ 6. 정리

| 항목     | 설명                                        |
| ------ | ----------------------------------------- |
| 인터페이스명 | `jakarta.servlet.FilterChain`             |
| 도입 버전  | Servlet 2.3                               |
| 핵심 메서드 | `doFilter(request, response)`             |
| 목적     | 필터 체인에서 다음 필터 또는 서블릿으로 요청을 전달             |
| 역할     | 체인의 흐름 제어, 필터 간 연결 유지                     |
| 위치     | `Filter` 구현 클래스의 `doFilter()` 메서드 안에서 사용됨 |

---

## 🧠 마무리

서블릿 필터 시스템에서 **FilterChain**의 목적은 필터 기반 요청 처리 과정에서 제어 흐름을 명확하게 연결하고, 각 필터가 책임을 분리된 방식으로 수행하도록 도와주는 것입니다.

이 구조 덕분에 자바 웹 애플리케이션은 로깅, 인증, 인코딩, 압축, CORS 처리 등 다양한 공통 기능을 관심사 분리 원칙에 따라 필터 체인으로 구성할 수 있게 됩니다.

---

StandardWrapperValve.invoke(Request, Response)에서 필터 체인을 생성함.

