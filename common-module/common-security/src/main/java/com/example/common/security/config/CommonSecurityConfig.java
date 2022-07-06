package com.example.common.security.config;

import com.example.common.security.filter.JWTAuthenticationTokenFilter;
import com.example.common.security.handler.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author
 * @date 2022-07-06 16:56
 */
@Configuration
public class CommonSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JWTAuthenticationTokenFilter();
    }

    @Bean
    public UserAuthAccessDeniedHandler userAuthAccessDeniedHandler(){
        return new UserAuthAccessDeniedHandler();
    }

    @Bean
    public UserAuthenticationEntryPointHandler userAuthenticationEntryPointHandler(){
        return new UserAuthenticationEntryPointHandler();
    }

    @Bean
    public UserLoginFailureHandler userLoginFailureHandler(){
        return new UserLoginFailureHandler();
    }

    @Bean
    public UserLoginSuccessHandler userLoginSuccessHandler(){
        return new UserLoginSuccessHandler();
    }

    @Bean
    public UserLogoutSuccessHandler userLogoutSuccessHandler(){
        return new UserLogoutSuccessHandler();
    }

}
