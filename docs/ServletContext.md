# 🌐 ServletContext 완벽 가이드: 웹 애플리케이션 전역 객체의 핵심 이해

Java Servlet 기반의 웹 애플리케이션을 개발할 때, 우리가 자주 마주치는 객체 중 하나가 바로 <Strong>`ServletContext`</Strong>입니다. 하지만 초보 개발자뿐 아니라 경력자도 종종 이 객체의 **정확한 역할**과 **제한 사항**을 혼동하곤 합니다.

이번 포스트에서는 `ServletContext`의 정의, 사용 사례, 제한사항, 실전 예제까지 풍부한 예제와 함께 상세히 정리해보겠습니다.

---

## 📌 1. ServletContext란 무엇인가?

`ServletContext`는 **서블릿이 자신이 속한 웹 애플리케이션에 대한 정보를 얻고, 서블릿 컨테이너와 상호 작용하기 위한 인터페이스**입니다. 다시 말해, 서블릿이 **컨테이너와 통신할 수 있는 수단**을 제공합니다.

```java
ServletContext context = getServletContext();
```

* `ServletContext`는 서블릿이 초기화될 때 `ServletConfig`를 통해 간접적으로 제공됩니다.
* 하나의 **웹 애플리케이션 당 하나의 `ServletContext` 인스턴스**만 존재합니다.
* 웹 애플리케이션의 루트 경로(`/`)에 해당하며, 서블릿, JSP, 정적 리소스 등 모든 컴포넌트가 공유할 수 있는 **전역 공간**입니다.

---

## 🧠 2. ServletContext로 할 수 있는 일들

`ServletContext`는 매우 다양한 기능을 제공합니다. 대표적인 기능은 다음과 같습니다:

### ✅ 2.1 전역 속성 공유

웹 애플리케이션 내 모든 서블릿/JSP 간에 **공통 데이터를 공유**할 수 있습니다.

```java
// 저장
context.setAttribute("globalValue", "SharedValue");

// 조회
String value = (String) context.getAttribute("globalValue");
```

### ✅ 2.2 MIME 타입 조회

파일 확장자에 따른 MIME 타입을 조회할 수 있습니다.

```java
String mimeType = context.getMimeType("example.pdf");  // application/pdf
```

### ✅ 2.3 리소스 경로 관련 메서드

애플리케이션 내부의 리소스 경로를 다룰 수 있습니다.

```java
String realPath = context.getRealPath("/WEB-INF/config.properties");
```

### ✅ 2.4 리소스 스트림 열기

애플리케이션 내 리소스를 `InputStream` 형태로 읽을 수 있습니다.

```java
InputStream in = context.getResourceAsStream("/WEB-INF/config.properties");
```

### ✅ 2.5 로깅

웹 컨테이너가 제공하는 로깅 시스템에 접근할 수 있습니다.

```java
context.log("애플리케이션이 시작되었습니다.");
```

---

## 🏗️ 3. ServletContext의 생명주기

* `ServletContext`는 **웹 애플리케이션이 시작될 때 생성되고, 종료될 때 소멸**됩니다.
* 따라서 `ServletContextListener`를 사용하면 애플리케이션 초기화 및 종료 시점에 필요한 작업을 수행할 수 있습니다.

```java
@WebListener
public class MyContextListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("앱이 시작됨");
    }
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("앱이 종료됨");
    }
}
```

---

## ☁️ 4. 분산 환경(Distributed Environment)에서의 제한 사항

웹 애플리케이션이 \*\*분산 환경(distributed)\*\*으로 설정된 경우, 즉 여러 JVM에서 동일한 애플리케이션이 실행되는 구조에서는 `ServletContext`의 의미가 바뀝니다.

> 분산 설정된 애플리케이션에서는 **각 JVM마다 별도의 `ServletContext` 인스턴스**가 존재합니다.

### ⚠️ 중요한 제약

이 경우, `ServletContext`를 이용해 **전역 데이터를 저장하면 JVM 간에 공유되지 않기 때문에** 전역이 아니게 됩니다.

* ❌ 잘못된 예시: `ServletContext.setAttribute()`를 전역 캐시처럼 사용
* ✅ 올바른 방법: **데이터베이스, Redis, 외부 캐시 서버** 등을 이용해 전역 데이터를 저장

---

## 🧩 5. ServletContext vs ServletConfig

| 항목    | ServletContext  | ServletConfig      |
| ----- | --------------- | ------------------ |
| 적용 범위 | 웹 애플리케이션 전체     | 특정 서블릿에 한정         |
| 공유 여부 | 모든 서블릿 간 공유 가능  | 해당 서블릿 내부에서만 사용 가능 |
| 사용 목적 | 전역 설정, 리소스 접근 등 | 서블릿 개별 설정 전달       |
| 생성 시점 | 웹 애플리케이션 시작 시   | 서블릿 초기화 시점         |

---

## 💡 실전 예제

### 1. 전역 방문자 수 카운팅 예제

```java
@WebServlet("/visit")
public class VisitCounterServlet extends HttpServlet {
    @Override
    public void init() {
        getServletContext().setAttribute("visitorCount", 0);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        ServletContext context = getServletContext();
        Integer count = (Integer) context.getAttribute("visitorCount");
        context.setAttribute("visitorCount", count + 1);

        resp.getWriter().write("👥 방문자 수: " + (count + 1));
    }
}
```

---

## 📚 정리

* `ServletContext`는 **서블릿 컨테이너와 애플리케이션 간의 인터페이스** 역할
* 웹 애플리케이션 **전체에서 공유되는 전역 객체**
* 리소스 조회, MIME 타입 확인, 전역 속성 저장, 로깅 등 다양한 기능 제공
* **분산 환경에서는 전역 공유 수단으로 사용하면 안 되며**, 외부 저장소를 사용해야 함

---

## ✅ 마무리하며

서블릿 개발을 하면서 `ServletContext`는 자주 등장하는 객체지만, 그 쓰임새를 정확히 이해하고 적절히 사용하는 것이 중요합니다. 특히 **애플리케이션 스코프의 전역 데이터 관리**, **서블릿 간 정보 공유**, **초기화 작업 처리** 등에 유용하게 활용될 수 있습니다.

하지만, 분산 환경에서는 반드시 **외부 자원을 통한 데이터 공유** 전략으로 전환해야 하며, 과도한 `ServletContext` 사용은 오히려 설계의 복잡도를 높일 수 있습니다.


