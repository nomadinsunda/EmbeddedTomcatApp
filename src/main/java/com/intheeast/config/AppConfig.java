package com.intheeast.config;


import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.intheeast.entity.UserMessage;

import jakarta.servlet.MultipartConfigElement;


import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.intheeast.service", 
		"com.intheeast.dao",
		"com.intheeast.init"})
@PropertySource("classpath:application.properties")
public class AppConfig{
	
	@Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/sbdt_db?characterEncoding=UTF-8");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");

        return dataSource;
    }

    @Bean
    // Hibernate Session Factory
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());  // 데이터소스 설정
        sessionFactory.setPackagesToScan("com.intheeast.entity");  // 엔티티 패키지 설정
        sessionFactory.setHibernateProperties(hibernateProperties());  // Hibernate 설정
        return sessionFactory;
    }
    
    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager
          = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.use_sql_comments", "true");
        properties.setProperty("hibernate.id.new_generator_mappings", "true");
        
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        return properties;
    }    

    // 메시지 소스 설정
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/spring/messages");
        return messageSource;
    }

    // View Controller 설정 (예: URL 경로와 뷰 매핑)
    
    
    @Bean
    public Class<UserMessage> userMessageClass() {
        return UserMessage.class;
    }
    
    @Bean
    public JavaMailSenderImpl javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // 구글 SMTP 서버 설정
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);  // TLS 포트 (또는 465는 SSL)

        // 구글 이메일 계정 정보 입력
        mailSender.setUsername("swseokitec@gmail.com");  // 구글 이메일 계정
        mailSender.setPassword("kmwmvsbajccozsxc");    // 앱 비밀번호 (구글 계정에서 생성)

        // 추가적인 속성 설정
        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");  // TLS 사용
//        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");  // SSL 신뢰 설정 (TLS 사용하는 경우 불필요)
        props.put("mail.debug", "true");  // 디버그 활성화
        
        mailSender.setJavaMailProperties(props);

        return mailSender;
    }
    
    @Bean
    public SimpleMailMessage simpleMailMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("swseokitec@gmail.com");  // 기본 발신자 설정
        message.setSubject("안녕하세요");    // 기본 제목 설정
        message.setText("냉무");       // 기본 내용 설정
        return message;
    }
    
}
