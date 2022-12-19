package com.tricon.rcm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tricon.rcm.interceptor.InterceptLog;

@Component
public class CustomWebConfigurer implements WebMvcConfigurer {

	//https://medium.com/@ankithahjpgowda/log-request-and-responses-of-rest-apis-in-springboot-c13f9bc7903f
    @Autowired
    private InterceptLog logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);
    }
}