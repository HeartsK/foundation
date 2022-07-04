package com.example.common.security.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author
 * @date 2022-07-04 16:57
 */
public class SecurityConfig implements WebMvcConfigurer {

    /** 不需要拦截地址 */
    public static final String[] excludeUrls = { "/login", "/logout", "/refresh" };

    public void addInterceptors(InterceptorRegistry registry){
        /*registry.addInterceptor(new )*/

    }
}
