package com.project.mySite.conf;

import com.project.mySite.interceptor.ContextPathInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ContextPathInterceptor contextPathInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(contextPathInterceptor)
                .addPathPatterns("/**"); // 모든 경로에 인터셉터 적용
    }
}