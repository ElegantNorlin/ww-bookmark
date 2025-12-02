package com.wanwan.backend.config;

import com.wanwan.backend.interceptor.UserContextInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private UserContextInterceptor userContextInterceptor;

    /**
     * 注册拦截器，使其能够拦截所有请求
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册用户上下文拦截器，拦截所有路径，但排除登录相关接口
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/login")
                .excludePathPatterns("/api/register")
                .excludePathPatterns("/api/refresh")
                .excludePathPatterns("/error")
                .excludePathPatterns("/static/**");
    }
}
