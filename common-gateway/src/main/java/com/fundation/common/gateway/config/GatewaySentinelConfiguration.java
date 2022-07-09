package com.fundation.common.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 * @date 2022-07-09 11:04
 */
@Configuration
public class GatewaySentinelConfiguration {


    // 自定义限流异常页面
    @PostConstruct
    public void initBlockHandlers() {

        BlockRequestHandler blockRequestHandler = (serverWebExchange, throwable) -> {
            Map<String, String> result = new HashMap<>();
            result.put("code",HttpStatus.TOO_MANY_REQUESTS.toString());
            result.put("message","接口被限流了");
            return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(result));
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
}
