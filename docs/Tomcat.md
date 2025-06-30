# 🌐 Tomcat 내장 실행을 위한 최소 설정 도우미: `org.apache.catalina.startup.Tomcat` 완전 정복

---

## 🔍 들어가며

최근 몇 년간 **내장형 웹 서버(Embedded Web Server)** 방식이 널리 사용되면서, `Tomcat`을 코드로 직접 구성하고 구동하는 사례가 많아졌습니다. 특히 `Spring Boot`에서도 디폴트트 웹 서버로 채택된 Apache Tomcat은 강력하고 유연한 내장형 구동 방식을 제공합니다.

이 글에서는 **Tomcat을 자바 코드로 구성하고 구동할 수 있게 해주는 핵심 유틸리티 클래스**인 [`org.apache.catalina.startup.Tomcat`](https://tomcat.apache.org/tomcat-10.1-doc/api/org/apache/catalina/startup/Tomcat.html)에 대해 심층적으로 다루고자 합니다.

---

## 🔧 `Tomcat` 클래스란?

`org.apache.catalina.startup.Tomcat` 클래스는 Tomcat을 **코드 기반으로 임베드하여 실행**할 수 있도록 설계된 **최소한의 스타터(minimal starter)** 클래스입니다.

이 클래스는 일반적인 `server.xml` 기반 구성 방식과는 다르게, **외부 구성 파일 없이도 동작 가능한 구조**를 제공합니다.

### 📌 주요 사용 목적

* **단위 테스트용 내장 Tomcat 구동**
* **간단한 서블릿 테스트 서버**
* **Spring Boot 없이 순수 Java에서 Tomcat 실행**
* **설정 파일 없는 웹 애플리케이션 테스트**

---

## 📁 사전 요구사항

`Tomcat` 클래스를 활용하기 위해 다음과 같은 환경이 갖춰져야 합니다.

| 조건        | 설명                                                                        |
| --------- | ------------------------------------------------------------------------- |
| Classpath | 모든 Tomcat 관련 클래스 및 서블릿이 classpath 상에 존재해야 함 (예: 하나의 uber JAR 또는 IDE 설정 등) |
| 작업 디렉토리   | 임시 작업 파일을 저장할 디렉토리 필요                                                     |
| 설정 파일 불필요 | `web.xml` 등 설정 파일 없이도 실행 가능하나, 있다면 적용됨                                    |
| 자체 서블릿 가능 | 개발자가 직접 서블릿을 추가할 수 있음 (`addServlet()` 등 사용)                               |

---

## 🧰 주요 기능 요약

### ✔️ 1. 서블릿 및 웹앱 구성 메서드 제공

`Tomcat` 클래스는 다양한 `addXxx()` 메서드를 통해 웹 애플리케이션 구성을 단순화합니다.

* `addWebapp(String contextPath, String docBase)`
* `addServlet(String contextPath, String name, Servlet servlet)`
* `addContext(...)`, `addContextCustom(...)` 등

위 메서드들은 내부적으로 `Context`를 생성하고, 필요한 설정들을 자동 적용합니다. 특히 `conf/web.xml`의 디폴트 설정을 내부적으로 흉내 낸 `initWebappDefaults()`가 호출됩니다.

> ✅ 별도의 `global web.xml`은 적용되지 않으며, `LifecycleListener`를 통해 기본 구성만 주입됩니다.

---

### ✔️ 2. 보안 영역 자동 설정

기본적으로 **간단한 메모리 기반 보안 영역**(security realm)이 적용됩니다.
보다 복잡한 보안 구성이 필요한 경우에는 `Tomcat` 클래스를 상속하여 직접 설정하는 것도 가능합니다.

---

### ✔️ 3. `ServletContainerInitializer` 및 Web Fragment 지원

Tomcat이 제공하는 다음 기능들이 정상 작동합니다:

* `WEB-INF/web.xml` 및 `META-INF/context.xml` 처리
* `jakarta.servlet.ServletContainerInitializer` SPI
* `web-fragment.xml` 병합

이는 표준 웹 애플리케이션과 유사한 실행 환경을 제공함을 의미합니다.

---

## ⚙️ 고급 설정 옵션

### 🔹 `getDefaultWebXmlListener()`

Tomcat의 `addWebapp()`과 동일한 기본 동작을 수행하려면 이 리스너를 사용하세요. 이 리스너는 다음 요소를 자동으로 구성합니다:

* `DefaultServlet`
* JSP 처리기
* welcome files

```java
context.addLifecycleListener(tomcat.getDefaultWebXmlListener());
```

---

### 🔹 `noDefaultWebXmlPath()`

기본 글로벌 `web.xml`을 Tomcat이 적용하지 않도록 하기 위해 "더미 경로(dummy path)"를 반환합니다.

```java
context.setDefaultWebXml(tomcat.noDefaultWebXmlPath());
```

---

## 🧪 간단한 예시

```java
Tomcat tomcat = new Tomcat();
tomcat.setPort(8080);
tomcat.setBaseDir("temp");

Context context = tomcat.addContext("", new File(".").getAbsolutePath());

Tomcat.addServlet(context, "hello", new HttpServlet() {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("Hello Embedded Tomcat!");
    }
});
context.addServletMappingDecoded("/", "hello");

tomcat.start();
tomcat.getServer().await();
```

---

## 🛠️ 커맨드라인 지원

`Tomcat` 클래스는 자체 `main()` 메서드를 제공하며, 몇 가지 간단한 CLI 인자를 받아 기본적인 테스트 서버를 구동할 수도 있습니다. 이를 통해 데모, 교육용 서버, 기능 확인 등에 활용할 수 있습니다.

---

## 🧑‍💻 마무리

`org.apache.catalina.startup.Tomcat` 클래스는 Java 애플리케이션에서 **Tomcat을 독립 실행형으로 구동할 수 있게 하는 핵심 유틸리티**입니다. 설정 파일 없이도 기본적인 웹 환경을 빠르게 구성할 수 있으며, 단위 테스트 또는 경량 서버 구성이 필요한 프로젝트에 매우 유용합니다.

> Spring Boot를 사용하는 경우에도 내장 Tomcat은 내부적으로 이 클래스를 기반으로 구성됩니다.

---

## 📚 참고 링크

* [Tomcat 공식 문서](https://tomcat.apache.org/)
* [Tomcat 10 API Docs - `org.apache.catalina.startup.Tomcat`](https://tomcat.apache.org/tomcat-10.1-doc/api/org/apache/catalina/startup/Tomcat.html)

