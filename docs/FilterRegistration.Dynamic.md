## ✅ 요약 비교: FilterRegistration.Dynamic vs 정적 선언 (@WebFilter 또는 web.xml)

| 구분                    | `FilterRegistration.Dynamic`                            | 정적 선언 (`@WebFilter`, `web.xml`) |
| --------------------- | ------------------------------------------------------- | ------------------------------- |
| **등록 시점**             | **프로그램 실행 시 동적 등록 (코드 기반)**                             | 애플리케이션 초기 로딩 시 자동 등록            |
| **필터 객체 직접 생성**       | `new LoggingFilter()` 등 **직접 인스턴스 생성**                  | 서블릿 컨테이너가 자동 생성                 |
| **세밀한 제어**            | 필터 순서, 조건, URL 패턴 등을 코드에서 제어                            | 순서 지정은 어렵고 유연성 낮음               |
| **Servlet 3.0 이상 필수** | ✔ ServletContext 사용 가능                                  | Servlet 2.3 이상에서 가능             |
| **대표 용도**             | `WebApplicationInitializer`, `Spring Boot`, 복잡한 설정 요구 시 | 고전적인 설정, 단순 필터 등록 시             |

---

## 🔍 `FilterRegistration.Dynamic`란?

`ServletContext.addFilter(...)` 메서드는 필터를 \*\*"동적으로 등록"\*\*할 수 있게 해줍니다. 이 메서드의 리턴값이 바로 `FilterRegistration.Dynamic` 객체입니다.

즉, 동적으로 생성한 필터 인스턴스를 특정 URL 패턴에 등록하는 과정에서 **URL 매핑, 초기화 파라미터 설정, 디스패처 타입 등**을 설정할 수 있도록 도와주는 객체입니다.

### 예:

```java
FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encodingFilter", new CharacterEncodingFilter());
encodingFilter.setInitParameter("encoding", "UTF-8");
encodingFilter.addMappingForUrlPatterns(null, false, "/*");
```

* 이 방식은 Spring Boot, Spring MVC Java Config 기반 구조에서 많이 사용됩니다.

---

## 🎯 언제 사용하나?

### ✅ 동적 등록 (`FilterRegistration.Dynamic`)

* **Java Config 기반 웹 초기화 (`WebApplicationInitializer`)**
* **Spring Boot 내장 WAS** 구성 시
* 필터 간 **순서를 제어**하거나 **프로그램적 조건**에 따라 필터를 등록하고자 할 때

### ✅ 정적 등록 (`@WebFilter`, `web.xml`)

* 간단한 필터가 있고 **변동이 없는 경우**
* 구버전 WAS 또는 고전 JSP 프로젝트
* 설정을 XML에 명확히 기록하고자 할 때

---

## ⚠️ 주의점

* `addFilter(...)`는 **서블릿 컨테이너 초기화 단계**에서만 호출해야 합니다. 즉, `ServletContextListener.contextInitialized()`나 `WebApplicationInitializer.onStartup()` 내부에서만 사용 가능
* 이미 등록된 이름의 필터를 또 등록하려고 하면 **IllegalStateException** 발생

---

## 📚 결론

```java
FilterRegistration.Dynamic filter = servletContext.addFilter("myFilter", new MyFilter());
filter.addMappingForUrlPatterns(null, false, "/*");
```

이렇게 `FilterRegistration.Dynamic`을 사용하는 경우는 \*\*"코드에서 직접 필터를 등록하고 구성하는 유연한 방식"\*\*이며, `@WebFilter`나 `web.xml` 없이도 모든 설정이 가능합니다.
**Spring Boot + JavaConfig** 환경에서는 이 방식이 기본으로 사용됩니다.

