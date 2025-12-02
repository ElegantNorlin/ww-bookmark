package com.wanwan.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域请求配置类
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有跨域请求
        registry.addMapping("/**")
                // 允许的源（前端地址）
                .allowedOrigins("http://127.0.0.1:5173", "http://127.0.0.1:5174")
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头
                .allowedHeaders("*").allowCredentials(true)
                // 预检请求的有效期，单位为秒
                .maxAge(3600);
    }
}