package com.foundation.common.auth.config;

import com.foundation.common.auth.componet.JwtTokenEnhancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * 描述：TokenStore配置类。
 * TokenStore的实现类，有InMemoryTokenStore、JdbcTokenStore、JwtTokenStore、RedisTokenStore。
 * JwtAccessTokenConverter JWT访问令牌转换器和 JwtTokenStore JWT令牌存储组件
 */
@Configuration
public class JwtTokenStoreConfig {

    /**
     * 生成TokenStore来保存token  此处为JwtTokenStore实现
     *
     * @return TokenStore
     */
    @Bean
    public TokenStore jwtTokenStore() {
        //需要传入JwtAccessTokenConverter
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 生成JwtAccessTokenConverter转换器，并设置密钥
     *
     * @return JwtAccessTokenConverter
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //设置jwt密钥
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    /**
     * 密钥库中获取密钥对(公钥+私钥)
     */
    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return factory.getKeyPair("jwt", "123456".toCharArray());
    }

    /**
     * JwtTokenEnhancer的注入
     *
     * @return
     */
    @Bean
    public JwtTokenEnhancer jwtTokenEnhancer() {
        return new JwtTokenEnhancer();
    }

}
