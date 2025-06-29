# 🌐 DispatcherServlet 완벽 가이드

### Spring MVC 요청 흐름의 심장부, 중앙 디스패처 서블릿

---

## 📌 DispatcherServlet이란?

`DispatcherServlet`은 Spring MVC 프레임워크에서 **모든 HTTP 요청을 중앙에서 처리하는 프론트 컨트롤러(Front Controller)** 역할을 합니다.

즉, 클라이언트 요청이 들어오면 가장 먼저 이 서블릿이 응답하며, **핸들러 매핑 → 핸들러 실행 → 결과 렌더링**까지의 전 과정을 조율합니다.

> 🧭 핵심 키워드: *중앙 집중식 요청 처리, 확장 가능한 전략 기반 아키텍처, 유연한 구성*

---

## 🧠 DispatcherServlet의 역할 요약

| 처리 단계       | 설명                                    |
| ----------- | ------------------------------------- |
| 1️⃣ 요청 수신   | 모든 요청을 먼저 받음 (보통 `/` 또는 `*.do` 등에 매핑) |
| 2️⃣ 핸들러 매핑  | 어떤 컨트롤러가 이 요청을 처리할지 결정                |
| 3️⃣ 핸들러 어댑터 | 컨트롤러를 실행할 수 있는 어댑터 선택 및 실행            |
| 4️⃣ 뷰 리졸버   | 논리 뷰 이름을 실제 View 객체로 변환               |
| 5️⃣ 뷰 렌더링   | 최종 HTML 또는 JSON 등 응답 생성               |
| 6️⃣ 예외 처리   | 발생한 예외를 등록된 Resolver가 처리              |

---

## ⚙️ DispatcherServlet 구성 요소 (전략 객체)

DispatcherServlet은 내부적으로 다양한 컴포넌트들을 사용해 요청을 처리합니다. 이들은 모두 인터페이스 기반으로 설계되어 있어 쉽게 **교체하거나 확장**할 수 있습니다.

### ✅ 1. HandlerMapping (요청 → 핸들러 결정)

* 기본 구현체:

  * `RequestMappingHandlerMapping`
  * `BeanNameUrlHandlerMapping`

### ✅ 2. HandlerAdapter (핸들러 실행 방식 결정)

* 기본 구현체:

  * `RequestMappingHandlerAdapter`
  * `HttpRequestHandlerAdapter`
  * `SimpleControllerHandlerAdapter`

### ✅ 3. HandlerExceptionResolver (예외 처리 전략)

* 기본 구현체:

  * `ExceptionHandlerExceptionResolver`
  * `ResponseStatusExceptionResolver`
  * `DefaultHandlerExceptionResolver`

### ✅ 4. ViewResolver (뷰 이름 → View 객체 변환)

* 기본 구현체:

  * `InternalResourceViewResolver` (JSP 사용 시 기본)

### ✅ 5. MultipartResolver (파일 업로드 처리)

* 파일 업로드 요청을 처리하는 전략
* 기본값: 없음 (명시적으로 등록해야 활성화됨)

### ✅ 6. LocaleResolver (다국어 처리용 로케일 결정)

* 기본 구현체: `AcceptHeaderLocaleResolver`

### ✅ 7. ThemeResolver (테마 처리, 6.0부터 deprecated)

* 기본 구현체: `FixedThemeResolver`

### ✅ 8. ViewNameTranslator (컨트롤러에서 명시적 뷰 이름이 없을 경우 기본 뷰 이름 결정)

* 기본 구현체: `DefaultRequestToViewNameTranslator`

---

## 🏗️ DispatcherServlet 생성 및 초기화

### 전통적인 `web.xml` 방식

```xml
<servlet>
    <servlet-name>spring</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/spring/appServlet/servlet-context.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>spring</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

### Java Config 방식 (`Servlet 3.0+`)

```java
public class WebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", dispatcherServlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }
}
```

---

## 💡 다중 DispatcherServlet 구성도 가능

* 하나의 웹 애플리케이션에 **여러 개의 `DispatcherServlet`을 등록**할 수 있습니다.
* 각각의 `DispatcherServlet`은 **독립적인 `WebApplicationContext`를 가짐**.
* 공통 빈은 루트 컨텍스트(`ContextLoaderListener`)로 분리해서 공유.

---

## 🔍 @RequestMapping이 작동하려면?

> ⚠️ 주의: `@RequestMapping`이 제대로 작동하려면 다음이 존재해야 합니다.

* `RequestMappingHandlerMapping` (HandlerMapping)
* `RequestMappingHandlerAdapter` (HandlerAdapter)

→ Spring Boot와 기본 Spring 설정에서는 자동으로 등록되므로 별도 설정이 필요 없습니다. 하지만 커스텀 설정 시 빠뜨리면 작동하지 않을 수 있습니다.

---

## 🚫 커스터마이징 주의사항

| 실수                                 | 증상                                              |
| ---------------------------------- | ----------------------------------------------- |
| HandlerMapping을 커스터마이징하면서 기본 구현 제거 | `@RequestMapping` 작동 안 함                        |
| ViewResolver를 등록하지 않음              | 뷰 이름 해석 실패 (`Could not resolve view with name`) |
| MultipartResolver 누락               | 파일 업로드 실패 (request.getFile()이 null)             |

---

## ✅ 정리

| 구성 요소                    | 기본 구현체                         | 역할              |
| ------------------------ | ------------------------------ | --------------- |
| DispatcherServlet        | Spring MVC의 중앙 컨트롤러            | 요청 라우팅 및 처리     |
| HandlerMapping           | RequestMappingHandlerMapping 등 | 요청을 핸들러로 매핑     |
| HandlerAdapter           | RequestMappingHandlerAdapter 등 | 핸들러 호출 가능하게     |
| ViewResolver             | InternalResourceViewResolver 등 | 뷰 이름 → JSP 등 변환 |
| HandlerExceptionResolver | 다양한 예외 처리 전략                   | 예외 → 에러 페이지     |

---

## ❓ 그런데 `DispatcherServlet`은 왜 ApplicationContextAware을 구현할까?

### 🔑 핵심 포인트:

> `DispatcherServlet`은 **자신의 내부에 별도의 WebApplicationContext를 생성하거나 주입받아야 하는 존재**이며, 이 컨텍스트가 필요할 때 **Spring 컨테이너에 의해 주입될 수 있는 메커니즘**을 제공하기 위해 `ApplicationContextAware`를 구현합니다.

---

## 🎯 DispatcherServlet과 ApplicationContext의 관계

Spring MVC에서 DispatcherServlet은 \*\*"자기 자신만의 자식 WebApplicationContext"\*\*를 가질 수 있습니다.
즉, 다음 두 계층이 존재합니다:

```
🟢 Root ApplicationContext (ContextLoaderListener 등)
    └── 🔵 DispatcherServlet의 WebApplicationContext
```

> `DispatcherServlet`은 기본적으로 자체적으로 ApplicationContext를 생성하지만, 필요에 따라 **외부에서 주입받을 수도 있습니다.**

이런 상황에서 Spring이 다음과 같이 처리할 수 있습니다:

```java
DispatcherServlet dispatcher = new DispatcherServlet();
dispatcher.setApplicationContext(customWebApplicationContext);
```

이때 내부적으로 호출되는 메서드가 바로 `setApplicationContext(...)`입니다.

---

## ✅ 실제 구현에서의 쓰임

DispatcherServlet 내부를 보면 다음과 같은 구조를 따릅니다:

```java
public class DispatcherServlet extends FrameworkServlet
        implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
```

그리고 나중에 `onRefresh()`나 `initWebApplicationContext()` 등에서 이 값을 사용해 컨텍스트 초기화나 부모 컨텍스트 연결을 처리합니다.

즉, **DispatcherServlet이 IoC 컨테이너의 일부가 아닐 때도 외부에서 컨텍스트를 주입받을 수 있도록 하는 확장성**을 제공하는 구조입니다.

---

## 📚 예시: Servlet 3.0 환경에서 직접 DispatcherServlet 생성 시

```java
AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
context.register(WebConfig.class);

DispatcherServlet servlet = new DispatcherServlet();
servlet.setApplicationContext(context); // 여기가 핵심
```

이런 코드가 가능한 이유가 바로 `ApplicationContextAware` 구현 덕분입니다.

---

## ❗ 일반 빈과의 차이점

| 항목                            | 일반 빈               | DispatcherServlet                       |
| ----------------------------- | ------------------ | --------------------------------------- |
| ApplicationContextAware 구현 목적 | 자신이 속한 컨텍스트를 알기 위해 | 외부에서 **주입받을 수 있도록** 열어두기 위해             |
| 빈 등록 위치                       | IoC 컨테이너 내부        | 보통 `ServletContext`에 등록                 |
| 주입 방식                         | Spring이 알아서 DI     | 사용자가 `setApplicationContext()` 직접 호출 가능 |

---

## ✅ 결론

`DispatcherServlet`이 `ApplicationContextAware`를 구현하는 이유는 다음과 같습니다:

> **DispatcherServlet이 외부에서 자신의 ApplicationContext를 주입받을 수 있도록 허용하고, 내부에서 이 컨텍스트를 저장해 사용하기 위함입니다.**

* **자체 컨텍스트 생성도 가능하지만**, **외부에서 설정한 WebApplicationContext를 사용하도록 열어둔 것**입니다.
* 이는 Spring 3.x 이후 Servlet 3.0 기반의 **프로그램적 초기화 방식**(`WebApplicationInitializer`)을 지원하기 위한 설계적 유연성입니다.



## 🧾 마무리

`DispatcherServlet`은 단순한 서블릿이 아닌, **Spring MVC 전체를 관장하는 중앙 허브**입니다. 다양한 전략 객체를 기반으로 요청을 유연하고 정형화된 방식으로 처리할 수 있도록 도와줍니다.

**전통적인 MVC 컨트롤러부터 RESTful API까지 모두 처리**할 수 있는 유연성을 갖춘 이 서블릿은, Spring 웹 애플리케이션의 구조적 일관성과 확장성을 유지하는 데 필수적입니다.

---

