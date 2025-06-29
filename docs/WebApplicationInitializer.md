
# 🌐 Spring WebApplicationInitializer 완벽 가이드: web.xml을 대체하는 코드 기반 구성 방식

## 1. 🧩 WebApplicationInitializer란 무엇인가?

`WebApplicationInitializer`는 **Servlet 3.0 이상 환경에서 `web.xml` 없이** 서블릿 컨텍스트를 **코드 기반으로 초기화**할 수 있도록 하는 **Spring의 SPI 인터페이스**입니다.

```java
public interface WebApplicationInitializer {
    void onStartup(ServletContext servletContext) throws ServletException;
}
```

* Servlet 컨테이너가 기동될 때 `ServletContainerInitializer`가 자동 실행되며,
* 그중 Spring은 `SpringServletContainerInitializer`가 `WebApplicationInitializer` 구현체를 자동 탐지하여 호출합니다.

---

## 2. 🧾 기존 `web.xml` 방식 예제

대부분의 Spring 웹 애플리케이션은 DispatcherServlet을 등록하기 위해 아래처럼 `web.xml`을 사용해 왔습니다.

```xml
<servlet>
  <servlet-name>dispatcher</servlet-name>
  <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  <init-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/spring/dispatcher-config.xml</param-value>
  </init-param>
  <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
  <servlet-name>dispatcher</servlet-name>
  <url-pattern>/</url-pattern>
</servlet-mapping>
```

> 💡 XML 설정은 명시적이지만, 자바 코드와 분리되어 가독성과 유지보수성이 떨어집니다.

---

## 3. ✅ WebApplicationInitializer 방식으로 DispatcherServlet 등록하기

`web.xml`을 Java 코드로 대체하면 다음과 같습니다.

```java
public class MyWebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) {
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");

        ServletRegistration.Dynamic dispatcher =
            container.addServlet("dispatcher", new DispatcherServlet(appContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
```

* `onStartup()` 메서드는 컨테이너가 초기화되면 자동 호출됩니다.
* DispatcherServlet을 직접 생성하여 Spring 애플리케이션 컨텍스트를 주입할 수 있습니다.
* XML 설정(`dispatcher-config.xml`)을 그대로 사용할 수 있습니다.

---

## 4. ☕ 완전한 자바 기반 구성으로 리팩토링

Spring 3.1 이후에는 XML 없이 완전히 자바 클래스로만 설정하는 것이 일반화되었습니다.

```java
public class MyWebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) {
        // 루트 컨텍스트 등록 (서비스/리포지토리 등 글로벌 빈)
        AnnotationConfigWebApplicationContext rootContext =
            new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);

        container.addListener(new ContextLoaderListener(rootContext));

        // DispatcherServlet용 컨텍스트 등록 (웹 관련 빈만)
        AnnotationConfigWebApplicationContext dispatcherContext =
            new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(DispatcherConfig.class);

        ServletRegistration.Dynamic dispatcher =
            container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
```

### ✅ 구성 포인트:

| 구성 요소                    | 설명                                           |
| ------------------------ | -------------------------------------------- |
| `AppConfig.class`        | 루트 애플리케이션 컨텍스트 설정 (DB, 트랜잭션, Service 등)      |
| `DispatcherConfig.class` | DispatcherServlet 전용 컨텍스트 설정 (컨트롤러, 뷰 리졸버 등) |
| `ContextLoaderListener`  | 루트 컨텍스트의 생명주기 관리 담당                          |

---

## 5. 📚 더 간단한 방식: AbstractAnnotationConfigDispatcherServletInitializer 사용

Spring은 더 나은 추상화를 위해 `AbstractAnnotationConfigDispatcherServletInitializer`를 제공합니다.

```java
public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { AppConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { DispatcherConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}
```

이 방식은 내부에서 `onStartup()`을 자동으로 구현해주므로 코드가 훨씬 간결해집니다.

---

## 6. 🔢 WebApplicationInitializer 실행 순서 지정하기

여러 개의 `WebApplicationInitializer`가 존재할 경우, 다음 중 하나를 통해 실행 순서를 지정할 수 있습니다:

* `@Order(1)` 애노테이션 사용
* `org.springframework.core.Ordered` 인터페이스 구현

> ⚠️ 일반적으로 하나만 작성하는 것이 좋으며, 순서를 지정할 필요는 거의 없습니다.

---

## 7. 🚀 장점 요약

| 기존 XML 방식                    | 코드 기반 WebApplicationInitializer |
| ---------------------------- | ------------------------------- |
| 별도 XML 작성 필요                 | 자바 코드만으로 설정 가능                  |
| Servlet 초기화 하드코딩             | 컨텍스트 및 DispatcherServlet 직접 관리  |
| 유지보수 어려움                     | IDE 자동완성 + 리팩토링 용이              |
| context-param, init-param 필요 | 생성자 주입 또는 Setter로 간편 처리 가능      |

---

## 🧠 마무리

`WebApplicationInitializer`는 Spring 기반 웹 애플리케이션에서 **Servlet 3.0 이상 환경에서 완전한 Java Config 기반의 웹 설정을 가능하게 해주는 핵심 인터페이스**입니다.
더 이상 `web.xml`에 의존하지 않고, 더 유연하고 타입 안전한 방식으로 웹 환경을 구성할 수 있도록 해줍니다.

> ☑ Spring Boot에서는 이 방식이 내부적으로 이미 적용되어 있어, 일반 사용자는 이 클래스를 직접 구현할 일이 거의 없습니다.
> 그러나 Spring Legacy 프로젝트나 Spring MVC Standalone 프로젝트에서는 매우 유용하게 사용됩니다.


---

# 🚀 Spring Boot에서 WebApplicationInitializer는 어떻게 대체되었는가?

---

## ✅ 먼저 정리: 전통적인 `WebApplicationInitializer` 방식 요약

```java
public class MyWebAppInitializer implements WebApplicationInitializer {
    public void onStartup(ServletContext container) {
        // Root Context 등록
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);
        container.addListener(new ContextLoaderListener(rootContext));

        // DispatcherServlet 등록
        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(DispatcherConfig.class);
        ServletRegistration.Dynamic dispatcher =
            container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
```

* **Servlet 3.0+ 환경**에서 동작
* `SpringServletContainerInitializer` → `WebApplicationInitializer` 호출

---

## 🔄 Spring Boot는 이 모든 초기화 과정을 어떻게 대체하는가?

> 👉 Spring Boot는 \*\*스스로 내장 서블릿 컨테이너(Tomcat/Jetty/Undertow 등)\*\*를 구동하고,
> 자동으로 DispatcherServlet을 등록하며, 사용자 설정을 Java Config로 관리합니다.

### 핵심은 이것입니다:

> **Spring Boot는 `ServletContext` 초기화를 자동으로 수행하기 위해 `ServletContextInitializer`를 사용합니다.**

---

## ✅ 핵심 클래스: `SpringApplication` → `SpringApplication.run()`

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

* 이 `run()` 호출이 내부적으로 톰캣 서버를 실행하고, 자동으로 DispatcherServlet을 등록합니다.
* `web.xml`, `WebApplicationInitializer`, `ContextLoaderListener` → 전부 필요 없음.

---

## ✅ Spring Boot가 서블릿 컨테이너를 초기화하는 흐름

### 1. `SpringApplication.run()` → `SpringApplication.createApplicationContext()`

→ `AnnotationConfigServletWebServerApplicationContext` 생성

### 2. `SpringApplication.run()` → `refresh()` → `onRefresh()`

→ 서블릿 컨테이너를 실행하기 위해 `WebServerFactoryCustomizer` 호출

### 3. `ServletWebServerFactory`에 의해 내장 Tomcat 생성 및 실행

### 4. `DispatcherServlet` 자동 등록

```java
@Bean
public DispatcherServlet dispatcherServlet() {
    return new DispatcherServlet();
}
```

* `DispatcherServletAutoConfiguration`에 의해 자동 등록됨
* `application.properties` 설정도 이 시점에 반영

### 5. DispatcherServlet을 \*\*`ServletContextInitializerBeans`\*\*가 ServletContext에 등록

```java
ServletContextInitializerBeans initializerBeans = new ServletContextInitializerBeans();
initializerBeans.onStartup(servletContext);
```

---

## ✅ 핵심 인터페이스: `ServletContextInitializer`

Spring Boot는 `WebApplicationInitializer` 대신 다음 인터페이스를 사용합니다:

```java
@FunctionalInterface
public interface ServletContextInitializer {
    void onStartup(ServletContext servletContext) throws ServletException;
}
```

* 사용자가 이 인터페이스의 구현체를 `@Bean`으로 등록하면,
* Boot는 내부적으로 이를 탐색해 `ServletContext`에 적용합니다.
* 자동 등록 대상: `DispatcherServlet`, `Filter`, `Servlet`, `Listener` 등

---

## ✅ 예시: 커스텀 서블릿을 직접 등록하고 싶을 때

```java
@Bean
public ServletRegistrationBean<MyServlet> myServlet() {
    ServletRegistrationBean<MyServlet> srb = new ServletRegistrationBean<>(new MyServlet(), "/custom");
    srb.setLoadOnStartup(1);
    return srb;
}
```

### 또는 Filter:

```java
@Bean
public FilterRegistrationBean<Filter> myFilter() {
    FilterRegistrationBean<Filter> frb = new FilterRegistrationBean<>();
    frb.setFilter(new MyFilter());
    frb.addUrlPatterns("/*");
    return frb;
}
```

→ Spring Boot가 자동으로 `ServletContext.addServlet()`, `addFilter()` 등을 호출해줍니다.

---

## ✅ 비교: Spring Legacy vs Spring Boot

| 항목                | Spring (Legacy)                      | Spring Boot                             |
| ----------------- | ------------------------------------ | --------------------------------------- |
| 서블릿 등록            | WebApplicationInitializer 또는 web.xml | DispatcherServlet 자동 등록                 |
| Context 등록        | ContextLoaderListener                | 내부적으로 자동 구성                             |
| ServletContext 접근 | 수동 onStartup() 구현                    | `ServletContextInitializer` 사용 또는 자동 처리 |
| 서버 실행             | 외부 톰캣 필요                             | 내장 톰캣 자동 구동                             |
| 설정 방식             | XML 또는 Java Config                   | Java Config + Auto Configuration        |

---

## ✅ 결론

Spring Boot는 기존의 `WebApplicationInitializer` 방식 대신 다음으로 대체합니다:

* **내장 서블릿 컨테이너 자동 구동 (Tomcat, Jetty 등)**
* **DispatcherServlet 자동 등록 (`DispatcherServletAutoConfiguration`)**
* **ServletContextInitializer를 통한 커스터마이징 지원**
* **web.xml, ContextLoaderListener, WebApplicationInitializer 전부 필요 없음**

---

## 📌 참고 클래스

* `SpringApplication`
* `DispatcherServletAutoConfiguration`
* `ServletContextInitializerBeans`
* `ServletWebServerFactory`
* `ServletRegistrationBean`, `FilterRegistrationBean`



