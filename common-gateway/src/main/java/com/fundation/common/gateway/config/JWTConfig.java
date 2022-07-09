package com.fundation.common.gateway.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author
 * @date 2022-07-05 10:41
 */
@AutoConfiguration
@ConfigurationProperties(prefix = "jwt")
@Data
@RefreshScope
public class JWTConfig {

    /**
     * 密钥
     */
    private String secret = "salt";
    /**
     * HeaderKEY
     */
    private String tokenHeader = "Authorization";

    /**
     * Token前缀字符
     */
    private String tokenPrefix = "Bearer ";

    /**
     * 过期时间 单位秒 1天后过期=86400 7天后过期=604800
     */
    private Long expiration = 86400L;

}
