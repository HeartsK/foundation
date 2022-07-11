package com.foundation.common.security.config;

import com.foundation.common.core.config.IgnoreProperties;
import com.foundation.common.core.config.JWTConfig;
import com.foundation.common.security.filter.JWTAuthenticationTokenFilter;
import com.foundation.common.security.handler.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * @author
 * @date 2022-07-05 10:24
 */
@AutoConfiguration
@ConditionalOnProperty(name = "security.enabled", matchIfMissing = true)
public class SecurityConfig {

    @Resource
    private JWTConfig jwtConfig;

    @Resource
    private IgnoreProperties ignoreProperties;

    @Resource
    private UserAuthAccessDeniedHandler userAuthAccessDeniedHandler;

    @Resource
    private UserAuthenticationEntryPointHandler userAuthenticationEntryPointHandler;

    @Resource
    private UserLoginSuccessHandler userLoginSuccessHandler;

    @Resource
    private UserLogoutSuccessHandler userLogoutSuccessHandler;

    @Resource
    private UserLoginFailureHandler userLoginFailureHandler;

    @Resource
    private JWTAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry
                registry = http.authorizeRequests();
        //不进行权限验证的请求或资源(从配置文件中读取)
        ignoreProperties.getWhiteUrls().forEach(url->registry.antMatchers(url).permitAll());
        //其他的需要登陆后才能访问
        registry.anyRequest().authenticated()
        .and()
        //配置未登录自定义处理类
        .httpBasic().authenticationEntryPoint(userAuthenticationEntryPointHandler)
        .and()
        //配置登录地址
        .formLogin()
        .loginProcessingUrl("/login")
        //配置登录成功自定义处理类
        .successHandler(userLoginSuccessHandler)
        //配置登录失败自定义处理类
        .failureHandler(userLoginFailureHandler)
        .and()
        //配置登出地址
        .logout()
        .logoutUrl("/logout")
        //配置用户登出自定义处理类
        .logoutSuccessHandler(userLogoutSuccessHandler)
        .and()
        //配置没有权限自定义处理类
        .exceptionHandling().accessDeniedHandler(userAuthAccessDeniedHandler)
        .and()
        // 开启跨域
        .cors()
        .and()
        // 取消跨站请求伪造防护
        .csrf().disable();
        // 基于Token不需要session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 禁用缓存
        http.headers().cacheControl();
        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
