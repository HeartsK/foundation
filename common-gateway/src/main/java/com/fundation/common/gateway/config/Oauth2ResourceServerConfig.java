package com.fundation.common.gateway.config;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import com.foundation.common.core.config.IgnoreProperties;
import com.foundation.common.core.constant.AuthConstant;
import com.fundation.common.gateway.componet.CustomReactiveAuthorizationManager;
import com.fundation.common.gateway.componet.CustomServerAccessDeniedHandler;
import com.fundation.common.gateway.componet.CustomServerAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author
 * @date 2022-07-12 14:18
 */
@Configuration
@EnableWebFluxSecurity
public class Oauth2ResourceServerConfig {

    @javax.annotation.Resource
    private IgnoreProperties ignoreProperties;

    @javax.annotation.Resource
    private CustomReactiveAuthorizationManager customReactiveAuthorizationManager;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
//        http.oauth2ResourceServer()
//                .jwt()
//                .jwtAuthenticationConverter(jwtAuthenticationConverter())
//                .jwtDecoder(jwtDecoder())
//                .and()
//                // 认证成功后没有权限操作
//                .accessDeniedHandler(new CustomServerAccessDeniedHandler())
//                // 还没有认证时发生认证异常，比如token过期，token不合法
//                .authenticationEntryPoint(new CustomServerAuthenticationEntryPoint())
//                // 将一个字符串token转换成一个认证对象
////                .bearerTokenConverter(new CustomServerBearerTokenAuthenticationConverter())
//                .and()
//                .authorizeExchange()
//                // 白名单请求全部放行
//                .pathMatchers(Convert.toStrArray(ignoreProperties.getWhiteUrls())).permitAll()
//                // 所有的请求都交由此处进行权限判断处理
//                .anyExchange()
//                .access(customReactiveAuthorizationManager)
//                .and()
//                .exceptionHandling()
//                .accessDeniedHandler(new CustomServerAccessDeniedHandler())//处理未授权
//                .authenticationEntryPoint(new CustomServerAuthenticationEntryPoint())//处理未认证
//                .and()
//                .csrf()
//                .disable();
////                .addFilterAfter(new TokenTransferFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
//
//        return http.build();
        http
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                .jwtDecoder(jwtDecoder())   // 本地加载公钥
        //.jwkSetUri()  // 远程获取公钥，默认读取的key是spring.security.oauth2.resourceserver.jwt.jwk-set-uri
        ;
        http.oauth2ResourceServer().authenticationEntryPoint(new CustomServerAuthenticationEntryPoint());
        http.authorizeExchange()
                .pathMatchers(Convert.toStrArray(ignoreProperties.getWhiteUrls())).permitAll()
                .anyExchange().access(customReactiveAuthorizationManager)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new CustomServerAccessDeniedHandler()) // 处理未授权
                .authenticationEntryPoint(new CustomServerAuthenticationEntryPoint()) //处理未认证
                .and().csrf().disable();

        return http.build();
    }

    /**
     * 从jwt令牌中获取认证对象
     */
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {

        // 从jwt 中获取该令牌可以访问的权限
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 取消权限的前缀，默认会加上SCOPE_
        authoritiesConverter.setAuthorityPrefix(AuthConstant.AUTHORITY_PREFIX);
        // 从那个字段中获取权限
        authoritiesConverter.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // 获取 principal name
        jwtAuthenticationConverter.setPrincipalClaimName("sub");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    /**
     * 解码jwt
     */
    public ReactiveJwtDecoder jwtDecoder() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Resource resource = new ClassPathResource("public.key");
        InputStream is = resource.getInputStream();
        byte[] publicKeyBytes = Base64.decode(IoUtil.read(is).toString());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

        return NimbusReactiveJwtDecoder.withPublicKey(rsaPublicKey)
                .signatureAlgorithm(SignatureAlgorithm.RS256)
                .build();
    }
}
