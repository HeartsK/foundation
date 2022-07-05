package com.example.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author
 * @date 2022-07-05 10:41
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
@RefreshScope
public class JWTConfig {

    /**
     * 密钥
     */
    private String secret;
    /**
     * HeaderKEY
     */
    private String tokenHeader;

    /**
     * Token前缀字符
     */
    private String tokenPrefix;

    /**
     * 过期时间 单位秒 1天后过期=86400 7天后过期=604800
     */
    private Long expiration;

    /**
     * 配置不需要认证的接口例：/index/**,/login/**,/favicon.ico
     */
    private String antMatchers;

}
