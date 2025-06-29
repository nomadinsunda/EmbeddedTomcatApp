다음은 `jakarta.servlet.ServletContextListener` 인터페이스에 대한 설명입니다.
(Spring의 ContextLoaderListener는 이 인터페이스를 구현합니다)
---

# 🧩 ServletContextListener 완벽 가이드: 웹 애플리케이션 생명주기 이벤트 리스너

`ServletContextListener`는 **서블릿 컨테이너와 웹 애플리케이션의 생명주기 이벤트**를 감지하기 위한 가장 기본적인 리스너 중 하나입니다. Java EE(Servlet 2.3) 이후 등장하여, **웹 애플리케이션이 시작되거나 종료될 때** 특정 작업을 수행할 수 있게 해주는 중요한 인터페이스입니다.

이번 포스트에서는 `ServletContextListener`의 정의부터 실전 예제까지 완벽하게 정리해보겠습니다.

---

## 🔍 1. ServletContextListener란?

```java
public interface ServletContextListener extends EventListener
```

* `ServletContextListener`는 **웹 애플리케이션의 시작/종료 시점에 호출되는 콜백 메서드를 정의**한 인터페이스입니다.
* 구현 클래스는 **컨텍스트 초기화(`contextInitialized`)** 및 **소멸(`contextDestroyed`) 이벤트**를 감지하여 원하는 동작을 수행할 수 있습니다.
* Servlet 2.3부터 도입되었습니다.
* 이벤트 수신 대상은 `ServletContextEvent`.

---

## 🧠 2. 언제 사용하나?

* 애플리케이션 부트스트랩 작업 수행
  (예: 데이터베이스 연결 풀 초기화, 외부 리소스 로딩)
* 애플리케이션 종료 시 정리 작업 수행
  (예: 쓰레드 종료, 리소스 반환, 로그 출력)

---

## ⚙️ 3. 주요 메서드

### ✅ contextInitialized(ServletContextEvent sce)

```java
void contextInitialized(ServletContextEvent sce)
```

* 웹 애플리케이션이 시작될 때 호출됨
* `ServletContext`를 얻어 전역 설정, 리소스 초기화 등에 활용 가능

### ✅ contextDestroyed(ServletContextEvent sce)

```java
void contextDestroyed(ServletContextEvent sce)
```

* 웹 애플리케이션이 종료될 때 호출됨
* 외부 리소스 정리, 캐시 플러시, 로그 출력 등에 활용

---

## 📝 4. 사용 예제

### 예시 1: 단순 로그 출력 리스너

```java
@WebListener
public class MyAppLifecycleListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("🚀 웹 애플리케이션 시작됨");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🧹 웹 애플리케이션 종료됨");
    }
}
```

> 💡 `@WebListener` 어노테이션은 Servlet 3.0 이상에서 `web.xml` 없이 자동 등록 가능

---

### 예시 2: 애플리케이션 전역 설정값 로딩

```java
@WebListener
public class AppConfigLoader implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        String configPath = ctx.getInitParameter("configPath");
        Properties props = new Properties();

        try (InputStream in = ctx.getResourceAsStream(configPath)) {
            props.load(in);
            ctx.setAttribute("appConfig", props); // 전역 속성 저장
        } catch (IOException e) {
            throw new RuntimeException("초기화 실패", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 필요 시 정리 작업
    }
}
```

---

## 🧾 5. web.xml 등록 방법 (Servlet 3.0 이전 방식)

```xml
<listener>
    <listener-class>com.example.listener.MyAppLifecycleListener</listener-class>
</listener>
```

* `@WebListener`를 사용하지 못하는 경우(예: 구버전 WAS, 보안 정책 등) `web.xml`을 통한 명시적 등록이 필요합니다.

---

## 📌 6. ServletContextEvent란?

`ServletContextListener`의 두 메서드 모두 `ServletContextEvent` 객체를 파라미터로 받습니다.

```java
public class ServletContextEvent extends EventObject {
    public ServletContext getServletContext()
}
```

* 이 이벤트 객체를 통해 **ServletContext**에 접근할 수 있음
* 전역 속성 설정, 초기화 파라미터 접근 등에 사용

---

## 🛠️ 7. 실무 활용 팁

| 용도                 | 설명                        |
| ------------------ | ------------------------- |
| 로그 시스템 초기화         | Log4j, SLF4J 설정 파일 로딩     |
| DB 연결 풀 초기화        | HikariCP, DBCP 등의 리소스 준비  |
| 외부 API 키 로딩        | AWS, Google API 등의 설정값 로딩 |
| 웹소켓, 스케줄러, 쓰레드풀 준비 | 컨텍스트 시작 시 자동 구동           |
| 종료 정리 작업           | 파일 닫기, 커넥션 반환, 캐시 정리 등    |

---

## ✅ 마무리

`ServletContextListener`는 **웹 애플리케이션의 생명주기 이벤트를 제어할 수 있는 가장 기본적이고 강력한 리스너**입니다. 특히 Spring 기반 프로젝트 외에도 순수 서블릿 기반 또는 하이브리드 구조의 애플리케이션에서 많이 사용됩니다.

Servlet 3.0 이후부터는 `@WebListener`를 통한 편리한 선언이 가능하며, Spring WebApplicationContext와 연동해서 사용할 수도 있습니다.

