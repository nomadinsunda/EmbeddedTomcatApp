package com.intheeast;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class EmbeddedTomcatApp {
	
	public static void main(String[] args) throws Exception {
        // 임베디드 톰캣 생성 및 설정
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector().setURIEncoding("UTF-8");

        // 웹 애플리케이션 컨텍스트 경로 설정
        String contextPath = ""; 
        String docBase = new File("src/main/webapp").getAbsolutePath();
        Context ctx = tomcat.addWebapp(contextPath, docBase);

        if (ctx == null) {
            System.out.println("Context creation failed.");
            return;
        }

        // 톰캣 서버 시작
//        tomcat.getConnector();  // 커넥터 초기화
        tomcat.start();
        tomcat.getServer().await();  // 서버 대기
    }

}
