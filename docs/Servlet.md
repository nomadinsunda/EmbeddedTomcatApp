# 🔧 Servlet(`jakarta.servlet.Servlet`) 인터페이스 완벽 가이드

### 웹 애플리케이션을 움직이는 자바 서버 컴포넌트의 핵심

---

## 📌 1. `jakarta.servlet.Servlet`이란?

`Servlet`은 **웹 서버에서 실행되는 자바 클래스**로서, 클라이언트(주로 웹 브라우저)의 요청을 받아 처리하고, 응답을 생성하는 웹 컴포넌트입니다.
이 인터페이스는 **모든 서블릿의 최상위 인터페이스**로, <Strong>서블릿의 생명주기(lifecycle)</Strong>와 관련된 메서드를 정의합니다.

---

## 📦 2. 정의 및 주요 특징

```java
public interface Servlet {
    void init(ServletConfig config) throws ServletException;
    void service(ServletRequest req, ServletResponse res) throws ServletException, IOException;
    void destroy();
    ServletConfig getServletConfig();
    String getServletInfo();
}
```

* 이 인터페이스를 구현하거나 `GenericServlet` 또는 `HttpServlet`을 상속받아 사용합니다.
* Servlet은 \*\*Servlet Container (Tomcat, Jetty 등)\*\*에 의해 실행됩니다.

---

## 🔄 3. Servlet의 생명주기 (Life-Cycle)

서블릿은 클라이언트 요청에 따라 **다음 순서**로 호출됩니다:

| 단계      | 메서드         | 설명                          |
| ------- | ----------- | --------------------------- |
| 1️⃣ 생성  | 생성자         | 서블릿 인스턴스 생성 (기본 생성자)        |
| 2️⃣ 초기화 | `init()`    | 서블릿 초기화. `ServletConfig` 전달 |
| 3️⃣ 서비스 | `service()` | 클라이언트 요청 처리                 |
| 4️⃣ 종료  | `destroy()` | 서블릿 종료 시 리소스 해제             |
| 5️⃣ 소멸  | GC          | 가비지 컬렉션 대상이 됨               |

> Servlet은 기본적으로 **싱글 인스턴스 멀티스레드 모델**로 작동하므로, `service()` 메서드는 동시에 여러 요청을 처리할 수 있습니다.

---

## 🔍 4. 주요 메서드 설명

### ✅ `init(ServletConfig config)`

* 서블릿 초기화 시 **한 번만 호출**
* 설정 파일(`web.xml`)의 `<init-param>`을 포함한 `ServletConfig` 제공

### ✅ `service(ServletRequest req, ServletResponse res)`

* 클라이언트 요청이 올 때마다 호출
* `HttpServlet`의 경우 `doGet()`, `doPost()` 등으로 위임

### ✅ `destroy()`

* 컨테이너가 서블릿을 종료할 때 호출
* 파일 닫기, 연결 해제 등 정리 작업 수행

### ✅ `getServletConfig()`

* 전달받은 `ServletConfig`를 반환

### ✅ `getServletInfo()`

* 서블릿에 대한 정보(버전, 작성자 등)를 문자열로 반환

---

## 📚 5. 구현 방식: 직접 vs 상속

### 1. 직접 구현 (권장 ❌)

```java
public class MyServlet implements Servlet {
    // init(), service(), destroy() 구현 필수
}
```

### 2. `GenericServlet` 상속 (비HTTP 포함)

```java
public class MyServlet extends GenericServlet {
    @Override
    public void service(ServletRequest req, ServletResponse res) {
        // 비HTTP 요청도 처리 가능
    }
}
```

### 3. `HttpServlet` 상속 (대부분 이 방식 사용)

```java
public class MyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        // HTTP GET 요청 처리
    }
}
```

---

## 🧪 6. 실전 예: HelloServlet

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.getWriter().write("Hello, Servlet!");
    }
}
```

> 💡 `@WebServlet`은 `web.xml` 없이 서블릿을 등록할 수 있게 해주는 Servlet 3.0 이상의 기능입니다.

---

## 📎 7. Servlet 인터페이스가 중요한 이유

* **서블릿 컨테이너와 애플리케이션 사이의 계약(Contract)**
* HTTP가 아닌 프로토콜도 처리 가능 (ServletRequest/Response는 HTTP에 종속되지 않음)
* Spring MVC의 `DispatcherServlet`, JSF, Struts 등 모든 자바 웹 프레임워크는 Servlet 위에서 작동함

---

## ✅ 정리

| 항목    | 설명                                  |
| ----- | ----------------------------------- |
| 패키지   | `jakarta.servlet`                   |
| 주요 구현 | `GenericServlet`, `HttpServlet`     |
| 처리 대상 | 클라이언트 요청 (주로 HTTP)                  |
| 역할    | 웹 요청 → 자바 로직 → 응답 처리                |
| 생명주기  | init → service → destroy            |
| 관련 기술 | Servlet Container (Tomcat, Jetty 등) |

---

## 🚀 마무리

`jakarta.servlet.Servlet`은 모든 자바 기반 웹 프레임워크의 근간이 되는 **핵심 인터페이스**입니다.
현대적인 Spring Boot나 Jakarta EE 기반 애플리케이션이라 할지라도, 이 서블릿의 구조를 이해하고 있으면 **프레임워크 내부 동작의 흐름**을 더 깊이 있게 이해할 수 있습니다.

---

# 🌐 `HttpServlet` 완벽 가이드

### 서블릿의 핵심을 이루는 HTTP 기반 추상 클래스

---

## 📌 1. HttpServlet이란?

`HttpServlet`은 `Servlet` 인터페이스를 구현한 **추상 클래스**로, **HTTP 프로토콜을 사용하는 웹 애플리케이션**에서 사용할 수 있도록 다양한 편의 메서드를 제공합니다.

> ✅ `GET`, `POST`, `PUT`, `DELETE` 등 **HTTP 메서드에 대응하는 메서드가 미리 구현되어 있어**, 우리는 필요한 메서드만 **선택적으로 오버라이드**하면 됩니다.

---

## 🧱 2. 클래스 구조

`HttpServlet`은 다음과 같은 상속 구조를 가집니다:

```
java.lang.Object
   ↳ GenericServlet
       ↳ HttpServlet
```

* `GenericServlet`: 비-HTTP 기반 서블릿을 위한 기본 클래스
* `HttpServlet`: HTTP 요청 처리에 특화된 클래스

---

## 🛠️ 3. 오버라이드할 주요 메서드

| 메서드                                              | 설명                        |
| ------------------------------------------------ | ------------------------- |
| `doGet(HttpServletRequest, HttpServletResponse)` | HTTP GET 요청 처리            |
| `doPost(...)`                                    | HTTP POST 요청 처리           |
| `doPut(...)`                                     | HTTP PUT 요청 처리            |
| `doDelete(...)`                                  | HTTP DELETE 요청 처리         |
| `init()` / `destroy()`                           | 서블릿의 초기화 및 종료 처리          |
| `getServletInfo()`                               | 서블릿에 대한 정보 제공 (버전, 저작자 등) |

### 🔸 `service()` 메서드는 거의 오버라이드하지 않음

* `HttpServlet`은 기본적으로 `service()` 메서드를 오버라이드하여 들어온 요청의 메서드(GET, POST 등)를 확인하고 `doXXX()` 메서드로 분배합니다.
* **개발자는 `doGet()`이나 `doPost()` 등만 구현하면 충분합니다.**

---

## 📥 4. 예제: 간단한 HelloServlet

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        resp.setContentType("text/plain");
        resp.getWriter().write("Hello, HttpServlet!");
    }
}
```

* `@WebServlet("/hello")`로 매핑된 URL에 대해 GET 요청을 받으면 문자열 응답을 반환합니다.

---

## 💡 5. 주의할 점: 멀티스레드 환경

`HttpServlet`은 **싱글 인스턴스, 멀티스레드** 방식으로 동작합니다.
즉, 서블릿 인스턴스는 하나만 생성되며, **다수의 스레드가 동시에 `service()` → `doGet()` 같은 메서드를 호출**합니다.

### ❗ 그러므로 아래는 위험한 코드입니다:

```java
public class UnsafeServlet extends HttpServlet {
    private int counter = 0;

    protected void doGet(...) {
        counter++; // ❌ 여러 스레드가 동시에 접근하면 동기화 문제 발생
    }
}
```

### ✅ 올바른 방식:

* 지역 변수 사용
* 동기화
* ThreadLocal 활용
* 상태 없는(stateless) 방식 유지

---

## 🔧 6. 덜 사용되는 메서드들

| 메서드           | 설명                        |
| ------------- | ------------------------- |
| `doOptions()` | CORS 사전 요청 처리 등에 사용 가능    |
| `doTrace()`   | HTTP TRACE 요청 처리 (거의 안 씀) |
| `doHead()`    | GET과 유사하나 응답 본문 없음        |

---

## 🧾 7. 요약

| 항목      | 설명                                     |
| ------- | -------------------------------------- |
| 클래스명    | `HttpServlet`                          |
| 상속      | `GenericServlet` 상속                    |
| 주요 메서드  | `doGet`, `doPost`, `init`, `destroy` 등 |
| 목적      | HTTP 요청을 편리하게 처리하기 위한 추상 클래스           |
| 동작 방식   | 하나의 인스턴스가 여러 요청을 멀티스레드로 처리             |
| 안전한 사용법 | 상태 없는 방식 유지, 공유 리소스 주의                 |

---

## ✅ 마무리

`HttpServlet`은 Java 기반 웹 애플리케이션에서 **가장 핵심적인 역할을 하는 클래스**입니다.
직접 이 클래스를 상속받아 서블릿을 구현하는 경우는 줄어들고 있지만, **Spring MVC의 `DispatcherServlet`**, **Spring WebFlux의 서블릿 어댑터**, **Struts**, **JSF** 등 **모든 웹 프레임워크의 기반이 되는 구조**입니다.




