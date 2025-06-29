# 🌟 SPI (Service Provider Interface)란?

**SPI**는 Java 플랫폼이 제공하는 **확장 메커니즘**입니다.

> 즉, **라이브러리나 프레임워크 개발자가 정의한 인터페이스의 구현체를**
> **애플리케이션 개발자가 "서비스 제공자(Service Provider)"로서 등록하면**,
> 런타임에 JVM이 이를 자동으로 찾아 실행할 수 있게 하는 **플러그인 구조**입니다.

---

## ✅ 핵심 목적

* 유연한 **확장성(Extensibility)** 제공
* **프레임워크 → 사용자 코드**로의 제어 흐름을 위임 (제어의 역전, Inversion of Control)
* **소스 코드 수정 없이도** 기능을 교체하거나 확장 가능

---

## 📦 Java SPI 구조 요약

| 구성 요소                        | 설명                                             |
| ---------------------------- | ---------------------------------------------- |
| **인터페이스 (API)**              | 확장 지점을 정의하는 인터페이스                              |
| **서비스 제공자 (Implementation)** | 이 인터페이스를 구현한 클래스                               |
| **등록 파일 (Descriptor)**       | `META-INF/services/` 디렉터리의 텍스트 파일              |
| **서비스 로더**                   | `java.util.ServiceLoader` 클래스를 통해 구현체를 런타임에 로딩 |

---

## 🔧 예제: 간단한 SPI 구현

### 1. 서비스 인터페이스 정의

```java
public interface GreetingService {
    String greet(String name);
}
```

### 2. 구현체 제공자 작성

```java
public class EnglishGreetingService implements GreetingService {
    public String greet(String name) {
        return "Hello, " + name;
    }
}
```

### 3. SPI 등록 파일 작성

* 경로: `META-INF/services/com.example.GreetingService`
* 파일 내용:

```
com.example.impl.EnglishGreetingService
```

> 이 파일은 텍스트 파일이며, 구현체의 <strong>정확한 FQCN(fully-qualified class name)</strong>을 명시해야 합니다.

### 4. 서비스 로딩

```java
ServiceLoader<GreetingService> loader = ServiceLoader.load(GreetingService.class);
for (GreetingService service : loader) {
    System.out.println(service.greet("John"));
}
```

이 코드는 런타임에 `META-INF/services/com.example.GreetingService` 파일을 읽고
거기에 명시된 구현체를 **자동으로 로딩**하여 실행합니다.

---

## 🔍 Spring 또는 Servlet에서의 SPI 사용 예

### 예: SpringServletContainerInitializer

* 인터페이스: `ServletContainerInitializer`
* 구현체: `SpringServletContainerInitializer`
* 등록 경로: `META-INF/services/jakarta.servlet.ServletContainerInitializer`
* 역할: Spring의 `WebApplicationInitializer` 구현체들을 찾아 `onStartup()` 호출

이 구조는 **JAR 파일 내부의 SPI 메커니즘**을 통해 톰캣에서 자동 실행됩니다.

---

## 🔒 SPI vs API vs SPI 사용자

| 역할      | 주체         | 설명                              |
| ------- | ---------- | ------------------------------- |
| API 제공자 | 프레임워크 개발자  | 인터페이스 또는 추상 클래스 제공              |
| SPI 구현자 | 애플리케이션 개발자 | SPI 인터페이스를 구현하고 JAR에 등록         |
| SPI 사용자 | 프레임워크 코드   | `ServiceLoader`로 SPI 구현체를 자동 로딩 |

---

## 🧠 SPI와 OOP 디자인 패턴의 연계

SPI는 다음 패턴들과 개념적으로 맞닿아 있습니다:

* **전략 패턴**: 구현체 교체를 유연하게 허용
* **팩토리 패턴**: 구현체 생성을 추상화
* **의존성 역전 원칙(DIP)**: 고수준 모듈이 저수준 구현에 의존하지 않도록

---

## 🚨 주의사항

| 항목        | 주의점                                               |
| --------- | ------------------------------------------------- |
| JAR 내부 위치 | 반드시 `META-INF/services/` 하위에 있어야 함                |
| 파일명       | 인터페이스의 FQCN과 동일해야 함                               |
| 내용        | **구현체의 FQCN만** 줄바꿈으로 나열 (주석 없음)                   |
| 로딩 시점     | 명시적 로딩 (`ServiceLoader.load(...)`) 또는 프레임워크 자동 로딩 |

---

## 📌 요약 정리

| 항목     | 설명                                                           |
| ------ | ------------------------------------------------------------ |
| 개념     | 서비스 구현체를 자동으로 로딩하기 위한 Java 플랫폼의 표준 확장 메커니즘                   |
| 위치     | `META-INF/services/인터페이스명`                                   |
| 주요 클래스 | `java.util.ServiceLoader`                                    |
| 대표 사례  | JDBC 드라이버, JAX-RS, ServletContainerInitializer, Java Logging |

---

## 📚 참고 자료

* [Java ServiceLoader 공식 문서](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html)
* [SPI 개념 소개 - Baeldung](https://www.baeldung.com/java-spi)

