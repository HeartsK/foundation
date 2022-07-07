package com.foundation.common.security.config;

import com.foundation.common.security.filter.JWTAuthenticationTokenFilter;
import com.foundation.common.security.handler.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author
 * @date 2022-07-06 16:56
 */
@AutoConfiguration
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
