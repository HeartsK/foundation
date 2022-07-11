package com.foundation.common.core.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关白名单
 * @author
 * @date 2022-07-07 16:18
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "security.ignore")
@AutoConfiguration
public class IgnoreProperties {

    private List<String> whiteUrls = new ArrayList<>();
}
