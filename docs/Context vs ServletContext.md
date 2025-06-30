# 🔍 Context vs ServletContext — 완벽한 개념 정리

| 구분           | **Tomcat의 `Context`**                             | **`jakarta.servlet.ServletContext`**          |
| ------------ | ------------------------------------------------- | --------------------------------------------- |
| **속한 영역**    | Tomcat 내부 구성 컴포넌트 (`org.apache.catalina.Context`) | 서블릿 표준 API (`jakarta.servlet.ServletContext`) |
| **의미**       | 하나의 웹 애플리케이션(컨텍스트 루트)에 대한 **Tomcat 내부 표현 객체**     | 웹 애플리케이션 전체에 대한 정보와 공유 리소스를 제공하는 **API 객체**   |
| **생성 주체**    | Tomcat이 구동되면서 내부적으로 생성 (`StandardContext`)        | Tomcat의 `Context`가 `ServletContext` 구현체를 생성   |
| **역할**       | 웹앱의 구조/생명주기/구성관리 (서블릿, 필터, 리스너, 보안 설정 등)          | 서블릿 간 공유 데이터, 초기 파라미터, 리소스 경로 제공              |
| **수명**       | Tomcat이 웹앱을 로딩할 때 생성, 언로드 시 종료                    | 해당 웹앱 로딩과 함께 생성되고 종료 시 소멸                     |
| **수동 생성 여부** | 코드에서 `Tomcat.addContext()`로 생성 가능                 | 개발자가 직접 생성할 수 없음 (컨테이너가 생성)                   |

---

## 🧠 비유로 이해하기

| 대상               | 설명                                                                              |
| ---------------- | ------------------------------------------------------------------------------- |
| `Context`        | **Tomcat 내부에서 웹 애플리케이션을 관리하기 위한 구조체** <br> → 구성 파일 파싱, 생명주기 관리, 서블릿 등록, 보안 설정 등 |
| `ServletContext` | **개발자가 서블릿 코드 안에서 접근 가능한 실행 정보 제공 객체** <br> → 공유 저장소, 경로 접근, 초기화 파라미터 등         |

---

## ✅ 실제 동작 흐름

1. Tomcat이 웹 애플리케이션을 시작하면:

   * 내부적으로 `org.apache.catalina.core.StandardContext`를 생성
   * 이 `Context`가 서블릿 초기화 수행
2. 초기화 과정에서 `ServletContext` 구현체 (`ApplicationContext`) 생성

   * 이 객체가 개발자에게 `getServletContext()` 등으로 노출됨
3. 이후 서블릿, 필터, 리스너는 모두 이 `ServletContext`를 통해:

   * 공유 속성 저장
   * 리소스 접근
   * 파라미터 읽기 등을 수행

---

## 📌 코드 관점 요약

```java
// Tomcat 내부: org.apache.catalina.Context
Tomcat tomcat = new Tomcat();
Context context = tomcat.addContext("/", new File(".").getAbsolutePath());

// 개발자 관점: jakarta.servlet.ServletContext
public class MyServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletContext sc = getServletContext(); // 내부적으로 Tomcat이 제공한 객체
        sc.setAttribute("count", 1);
    }
}
```

---

## 📜 정리 요약

* ✅ `Context`: Tomcat 내부에서 하나의 웹 애플리케이션을 나타내는 **구성 객체**
* ✅ `ServletContext`: 서블릿 스펙에서 제공하는 **개발자용 API 객체**, 애플리케이션 실행 환경에 대한 정보를 제공

> 💡 둘은 1:1로 연결되며, 실제로는 Tomcat의 `Context`가 `ServletContext` 구현체를 만들어 제공합니다.
