package com.intheeast.service;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

import com.opensymphony.module.sitemesh.html.TextFilter;

/*
 텍스트 필터링 로직은 사용자의 입력을 정리하거나 보안적으로 취약한 입력(예: XSS 공격, SQL 인젝션)을 방지하기 위해 사용하는 것입니다. 
 여기서는 HTML 태그 제거, 스크립트 방지와 같은 보안과 데이터 정리 목적의 필터링 로직을 구현할 수 있습니다.
 */
@Component
public class DefaultTextFilter implements TextFilter {
    @Override
    public String filter(String input) {
        if (input == null) {
            return null;
        }
        
        return input;

        /*
        // 1. Trim whitespace (앞뒤 공백 제거)
        String filtered = input.trim();

        // 2. Escape HTML characters (HTML 인젝션 방지)
        filtered = StringEscapeUtils.escapeHtml4(filtered);

        // 3. Remove script tags (XSS 방지)
        filtered = filtered.replaceAll("(?i)<script.*?>.*?</script>", "");  // 대소문자 무시하고 script 태그 제거

        // 4. Remove other potentially dangerous tags (iframe, object 등)
        filtered = filtered.replaceAll("(?i)<iframe.*?>.*?</iframe>", "");
        filtered = filtered.replaceAll("(?i)<object.*?>.*?</object>", "");
        filtered = filtered.replaceAll("(?i)<embed.*?>.*?</embed>", "");
        filtered = filtered.replaceAll("(?i)<applet.*?>.*?</applet>", "");
        
        // 5. Normalize extra spaces to a single space
        filtered = filtered.replaceAll("\\s+", " ");

        return filtered;
        */
    }
}