# 🌐 ContextLoaderListener(WebApplicationContext) 생성자 완벽 가이드

Spring 웹 애플리케이션에서 `ContextLoaderListener`는 서블릿 컨테이너의 시작/종료 생명주기를 감지하여 Spring의 루트 ApplicationContext를 관리하는 핵심 리스너입니다. 
특히 <Strong>Java 기반 초기화 방식(WebApplicationInitializer 등)</Strong>을 사용할 때는 `ContextLoaderListener`를 직접 인스턴스화하여 등록하게 됩니다.

이 포스트에서는 `ContextLoaderListener(WebApplicationContext context)` 생성자의 동작 원리와 생명주기, 주의사항까지 깊이 있게 다뤄보겠습니다.

---

## 📌 1. 생성자 시그니처

```java
public ContextLoaderListener(WebApplicationContext context)
```

이 생성자는 **직접 주어진 `WebApplicationContext` 인스턴스를 관리**하는 `ContextLoaderListener`를 생성합니다.

> ✅ 주로 `ServletContext.addListener()` API를 사용하여 **코드 기반(Servlet 3.0 이상)** 초기화 시 사용됩니다.

---

## 🧩 2. 주요 사용 시나리오

`WebApplicationInitializer` 또는 `ServletContainerInitializer` 기반 초기화 코드에서 다음과 같이 사용됩니다:

```java
public class MyWebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);

        // 핵심! 직접 생성한 context를 ContextLoaderListener에 주입
        servletContext.addListener(new ContextLoaderListener(rootContext));
    }
}
```

---

## 🧠 3. 이 생성자의 내부 동작

### 조건 1: 전달받은 `context`가 `ConfigurableWebApplicationContext`의 구현체일 경우

### 조건 2: 해당 context가 아직 `refresh()`되지 않았을 경우

다음 작업이 자동으로 수행됩니다:

1. **컨텍스트 ID 자동 할당**

   * context에 ID가 지정되지 않았다면 내부적으로 기본 ID가 할당됩니다.

     ```java
     if (context.getId() == null) {
         context.setId(generateId());
     }
     ```

2. **`ServletContext`와 `ServletConfig` 객체를 context에 연결**

   * Spring 컨텍스트가 서블릿 환경 정보를 사용할 수 있도록 설정됩니다.

3. **`customizeContext()` 호출**

   * 이 메서드는 `contextInitializerClasses` 파라미터에 등록된 초기화 로직(`ApplicationContextInitializer`)을 실행하는 메서드입니다.
   * 보통 사용자 정의 초기화자 또는 Spring Security 초기화 등이 이 과정을 통해 수행됩니다.

4. **`ApplicationContextInitializer` 자동 적용**

   * `contextInitializerClasses`라는 `init-param`을 통해 등록된 클래스들을 자동으로 실행합니다.
   * 예: `MySecurityInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>`

5. **컨텍스트 `refresh()` 수행**

   * 모든 Bean 정의를 로딩하고 초기화합니다.

---

## ⚠️ 4. 이미 refresh된 context라면?

만약 전달된 `context`가 이미 `refresh()`된 상태이거나 `ConfigurableWebApplicationContext`를 구현하지 않는다면 위 작업들은 **실행되지 않습니다.**

이는 다음과 같은 의미를 가집니다:

* 개발자가 **컨텍스트 초기화 과정을 직접 제어한 경우**, Spring은 그 상태를 **존중**합니다.
* 단, 이런 경우에는 `ApplicationContextInitializer`나 `ServletContext` 바인딩 등이 **자동으로 수행되지 않기 때문에** 모든 것을 수동으로 설정해야 합니다.

---

## 💾 5. ServletContext에 등록되는 속성

```java
servletContext.setAttribute(
  WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
  context
);
```

* 이는 이후 다른 서블릿(`DispatcherServlet` 등)에서 `getServletContext().getAttribute(...)`로 루트 컨텍스트에 접근할 수 있도록 해줍니다.
* 속성명: `WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE` (`"org.springframework.web.context.WebApplicationContext.ROOT"`)

---

## 🔄 6. 애플리케이션 종료 시 자동 close()

서블릿 컨테이너가 종료되면 다음 메서드가 호출됩니다:

```java
@Override
public void contextDestroyed(ServletContextEvent event) {
    this.context.close(); // Spring ApplicationContext 종료
}
```

* 등록된 모든 `@PreDestroy`, `DisposableBean`, `destroyMethod` 등의 정리 작업이 수행됩니다.

---

## 📚 7. 참고 사항 요약

| 항목            | 설명                                                           |
| ------------- | ------------------------------------------------------------ |
| 어떤 경우 사용?     | 코드 기반 초기화(WebApplicationInitializer)에서 Spring 컨텍스트 직접 주입     |
| 자동 refresh 조건 | ConfigurableWebApplicationContext 구현체 & 아직 refresh되지 않은 경우   |
| 자동 수행 작업      | ID 설정, ServletContext 연결, 초기화자 적용, refresh()                 |
| 수동 처리 필요 조건   | 이미 refresh된 context or Configurable 구현체가 아닌 경우               |
| 컨텍스트 등록 위치    | ServletContext에 `ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE`로 저장 |
| 종료 시 처리       | contextDestroyed() → context.close() 자동 호출                   |

---

## ✅ 마무리

`ContextLoaderListener(WebApplicationContext)` 생성자는 Spring 웹 애플리케이션을 **Java 기반 구성 방식으로 유연하게 초기화할 수 있도록** 해주는 핵심 메커니즘입니다. 특히 `WebApplicationInitializer` 스타일로 애플리케이션을 설정하는 현대적 방식에서는 **컨텍스트 객체를 직접 생성하고, 이 생성자를 통해 리스너에 주입**하는 것이 일반적입니다.

이 방식을 제대로 이해하면, Spring Web MVC의 전체 초기화 과정을 세밀하게 제어할 수 있으며, 특히 **테스트용 컨텍스트 설정**, **다중 ApplicationContext 구성**, **Spring Security 커스터마이징** 등 고급 설정에서도 막힘없이 활용할 수 있습니다.

