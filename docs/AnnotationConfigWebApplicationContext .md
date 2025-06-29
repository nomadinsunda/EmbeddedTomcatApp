# 🌱 AnnotationConfigWebApplicationContext 완벽 가이드: 웹 환경에서 자바 기반 구성의 핵심

Spring Framework에서는 XML 구성 대신 자바 기반 구성(`@Configuration`)을 통해 빈을 등록하고 애플리케이션을 구성할 수 있습니다. 특히 웹 환경에서 이러한 자바 기반 구성을 사용하기 위한 핵심 클래스가 바로 <Strong>`AnnotationConfigWebApplicationContext`</Strong>입니다.

이 포스트에서는 이 클래스의 역할, 사용 방법, 주의할 점, 대안 클래스까지 세세하게 살펴보겠습니다.

---

## 📌 1. AnnotationConfigWebApplicationContext란?

`AnnotationConfigWebApplicationContext`는 **`WebApplicationContext`의 구현체 중 하나**로, 자바 애노테이션 기반 구성(`@Configuration`, `@Component`, `@Inject`)을 지원합니다.

즉, Spring 웹 애플리케이션에서 **XML 대신 자바 클래스를 통해 설정을 구성**하고자 할 때 사용하는 핵심 컨텍스트입니다.

```java
AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
context.register(AppConfig.class);
```

* 일반적인 `AnnotationConfigApplicationContext`와 유사하지만 \*\*웹 환경(WebApplicationContext)\*\*을 위한 특화 버전입니다.
* Spring MVC에서 `DispatcherServlet` 또는 `ContextLoaderListener`와 함께 사용됩니다.

---

## 🧠 2. 특징 요약

| 특징             | 설명                                                                |
| -------------- | ----------------------------------------------------------------- |
| 🧩 구성 방식       | `@Configuration`, `@Component`, `@Inject` 기반 클래스 등록               |
| 🗃️ 구성 위치 설정   | `contextConfigLocation` 파라미터를 통해 클래스나 패키지를 지정                     |
| 🧼 기본 설정 없음    | `XmlWebApplicationContext`처럼 기본 XML 파일을 가정하지 않음                   |
| 🔄 여러 구성 등록 가능 | 여러 `@Configuration` 클래스 등록 시, 나중에 등록한 클래스의 빈이 우선                  |
| 🧰 유틸리티 메서드 제한 | `GenericApplicationContext`를 상속하지 않으므로 `registerBean()` 등의 메서드 없음 |

---

## 🏗️ 3. 주요 사용 방법

### ✅ 3.1 클래스 직접 등록

```java
AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
context.register(AppConfig.class);
```

* `AppConfig.class`는 `@Configuration` 또는 `@Component`로 선언된 클래스여야 합니다.

### ✅ 3.2 클래스패스 스캔 기반 등록

```java
AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
context.scan("com.example.config");
```

* 해당 패키지 내 `@Component`, `@Configuration` 등이 붙은 클래스를 자동으로 탐지합니다.

---

## 🌐 4. 웹.xml 기반 등록

기존 웹.xml을 사용하는 경우 다음과 같이 설정합니다.

```xml
<context-param>
    <param-name>contextClass</param-name>
    <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
</context-param>

<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>com.example.config.AppConfig</param-value>
</context-param>

<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

---

## 🚫 5. 중요한 주의 사항

### ❗ 5.1 `registerBean()` 메서드 없음

* 이 클래스는 `GenericApplicationContext`를 상속하지 않으므로, `registerBean()` 같은 편의 메서드를 직접 사용할 수 없습니다.
* `register()` 또는 `scan()`을 사용해야 합니다.

### ❗ 5.2 기본 configLocation 없음

* `XmlWebApplicationContext`는 기본적으로 `WEB-INF/applicationContext.xml`을 로드하려고 시도하지만,
* `AnnotationConfigWebApplicationContext`는 **반드시** `contextConfigLocation` 또는 `scan()` 등을 명시해야만 합니다.

### ❗ 5.3 분할 설정시 오버라이딩 주의

* 여러 개의 `@Configuration` 클래스를 등록할 경우, **나중에 등록된 클래스가 먼저 등록된 클래스의 빈을 오버라이드할 수 있습니다.**

---

## 🧩 6. 대안: GenericWebApplicationContext + AnnotatedBeanDefinitionReader

더 유연하게 빈을 등록하고 싶다면 `GenericWebApplicationContext`와 `AnnotatedBeanDefinitionReader` 조합을 사용할 수 있습니다.

```java
GenericWebApplicationContext context = new GenericWebApplicationContext();
AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(context);
reader.register(AppConfig.class);
```

이 방식은 **보다 정교하게 빈 등록을 제어**하고 싶을 때 적합합니다.

---

## 🧪 7. WebApplicationInitializer와 함께 사용

`web.xml` 없이 코드 기반 구성만으로 애플리케이션을 초기화하려면 `WebApplicationInitializer` 인터페이스를 사용합니다.

```java
public class MyWebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);

        servletContext.addListener(new ContextLoaderListener(context));

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher",
            new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
```

이 방식은 Spring Boot 이전의 **Java 기반 설정의 핵심 초기화 전략**이었습니다.

---

## 📚 8. 요약

| 항목    | 설명                                                                                   |
| ----- | ------------------------------------------------------------------------------------ |
| 클래스명  | `AnnotationConfigWebApplicationContext`                                              |
| 소속    | `org.springframework.web.context.support`                                            |
| 상속 계층 | `AbstractRefreshableWebApplicationContext` → `AbstractRefreshableApplicationContext` |
| 주요 기능 | Java 기반 설정 클래스 등록, 클래스 경로 스캔, DispatcherServlet 연동                                   |
| 주의사항  | 기본 위치 없음, `GenericApplicationContext` 미상속, 오버라이딩 규칙 있음                               |

---

## ✅ 마무리

`AnnotationConfigWebApplicationContext`는 XML 구성을 벗어나 **자바 기반 설정을 통해 웹 애플리케이션을 구성**할 수 있도록 해주는 매우 유용한 도구입니다. 특히 Spring MVC + Java Config 구조를 채택한 프로젝트에서는 이 클래스가 핵심적인 역할을 수행합니다.

다만, 단순히 클래스를 등록한다고 끝나는 것이 아니라, **contextConfigLocation, contextInitializerClasses, WebApplicationInitializer** 등 다양한 초기화 방식과 옵션들을 함께 이해해야 **진정한 유연성**을 누릴 수 있습니다.

