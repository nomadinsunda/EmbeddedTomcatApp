package com.intheeast.init;

import com.intheeast.filter.LoggingFilter;
import com.intheeast.filter.SimpleCORSFilter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import com.intheeast.config.AppConfig;
import com.intheeast.config.WebConfig;

public class MyWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // 1. Root 애플리케이션 컨텍스트 설정 (AppConfig)
        AnnotationConfigWebApplicationContext rootContext = 
        		new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);  // 전역 빈 설정

        // 2. ContextLoaderListener 추가 (Root Context 관리)
        servletContext.addListener(new ContextLoaderListener(rootContext));

        // 3. 전역 파라미터 및 필터 설정
        addGlobalParameters(servletContext);
        addFilters(servletContext);  // 필터 설정

        // 4. DispatcherServlet 서블릿 컨텍스트 설정 (WebConfig) 및 parent-child 설정
        addDispatcherServlet(servletContext, rootContext);
    }

    // 전역 파라미터 설정 메서드
    private void addGlobalParameters(ServletContext servletContext) {
        servletContext.setInitParameter("appName", "SiP University");
        servletContext.setInitParameter("recipe", "8.1");
        servletContext.setInitParameter("aboutThisRecipe",
                "<p>Recipe 8.1 shows how to create a simple user feedback form.</p>");
    }

    // 필터 추가 메서드 (필터 추가)
    private void addFilters(ServletContext servletContext) {
        // SiteMesh 필터 설정
        FilterRegistration.Dynamic siteMeshFilter = servletContext.addFilter("sitemesh", new SiteMeshFilter());
        siteMeshFilter.addMappingForUrlPatterns(null, false, "/*");

        // CharacterEncodingFilter 설정
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encodingFilter", characterEncodingFilter);
        encodingFilter.addMappingForUrlPatterns(null, false, "/*");

        // LoggingFilter 등록
        FilterRegistration.Dynamic loggingFilter = servletContext.addFilter("loggingFilter", new LoggingFilter());
        loggingFilter.addMappingForUrlPatterns(null, false, "/*");

        // SimpleCORSFilter 등록
        FilterRegistration.Dynamic corsFilter = servletContext.addFilter("corsFilter", new SimpleCORSFilter());
        corsFilter.addMappingForUrlPatterns(null, false, "/*");
        
    }

    // DispatcherServlet 설정 (Web 전용 컨텍스트) + parent-child 구조 설정
    private void addDispatcherServlet(ServletContext servletContext, AnnotationConfigWebApplicationContext rootContext) {
        // Web 전용 애플리케이션 컨텍스트 (컨트롤러, 뷰 리졸버 등)
        AnnotationConfigWebApplicationContext servletContextConfig = 
        		new AnnotationConfigWebApplicationContext();
        servletContextConfig.register(WebConfig.class);  // Web 관련 빈 설정

        // 부모 컨텍스트 설정
        servletContextConfig.setParent(rootContext);  // parent-child 계층 구조 설정

        DispatcherServlet dispatcherServlet = 
        		new DispatcherServlet(servletContextConfig);
        ServletRegistration.Dynamic registration = 
        		servletContext.addServlet("app", dispatcherServlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");

        // 파일 업로드 설정
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"), // OS에 따른 임시 디렉토리
                20971520,  // 최대 파일 크기: 20MB
                41943040,  // 전체 요청 크기: 40MB
                5242880);  // 임계값: 5MB
        registration.setMultipartConfig(multipartConfigElement);
    }
}