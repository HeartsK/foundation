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
//                // ?????????????????????????????????
//                .accessDeniedHandler(new CustomServerAccessDeniedHandler())
//                // ?????????????????????????????????????????????token?????????token?????????
//                .authenticationEntryPoint(new CustomServerAuthenticationEntryPoint())
//                // ??????????????????token???????????????????????????
////                .bearerTokenConverter(new CustomServerBearerTokenAuthenticationConverter())
//                .and()
//                .authorizeExchange()
//                // ???????????????????????????
//                .pathMatchers(Convert.toStrArray(ignoreProperties.getWhiteUrls())).permitAll()
//                // ??????????????????????????????????????????????????????
//                .anyExchange()
//                .access(customReactiveAuthorizationManager)
//                .and()
//                .exceptionHandling()
//                .accessDeniedHandler(new CustomServerAccessDeniedHandler())//???????????????
//                .authenticationEntryPoint(new CustomServerAuthenticationEntryPoint())//???????????????
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
                .jwtDecoder(jwtDecoder())   // ??????????????????
        //.jwkSetUri()  // ????????????????????????????????????key???spring.security.oauth2.resourceserver.jwt.jwk-set-uri
        ;
        http.oauth2ResourceServer().authenticationEntryPoint(new CustomServerAuthenticationEntryPoint());
        http.authorizeExchange()
                .pathMatchers(Convert.toStrArray(ignoreProperties.getWhiteUrls())).permitAll()
                .anyExchange().access(customReactiveAuthorizationManager)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new CustomServerAccessDeniedHandler()) // ???????????????
                .authenticationEntryPoint(new CustomServerAuthenticationEntryPoint()) //???????????????
                .and().csrf().disable();

        return http.build();
    }

    /**
     * ???jwt???????????????????????????
     */
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {

        // ???jwt ???????????????????????????????????????
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // ???????????????????????????????????????SCOPE_
        authoritiesConverter.setAuthorityPrefix(AuthConstant.AUTHORITY_PREFIX);
        // ??????????????????????????????
        authoritiesConverter.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // ?????? principal name
        jwtAuthenticationConverter.setPrincipalClaimName("sub");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    /**
     * ??????jwt
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
