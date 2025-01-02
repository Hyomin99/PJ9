package com.hm.pj9;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/ws/**") // WebSocket 엔드포인트
                .allowedOrigins("pj9.store") // 클라이언트 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
                .allowCredentials(true); // 자격 증명 허용
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 외부 이미지 디렉토리를 웹 경로와 연결
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/home/ubuntu/images/");  // 실제 이미지가 저장된 외부 디렉토리
    }
}
