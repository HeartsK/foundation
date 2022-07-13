package com.fundation.common.gateway.filter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.foundation.common.core.config.IgnoreProperties;
import com.foundation.common.core.config.JWTConfig;
import com.foundation.common.core.utils.api.ResponseUtils;
import com.foundation.common.core.utils.api.ResultCode;
import com.nimbusds.jose.JWSObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.text.ParseException;

/**
 * @author
 * @date 2022-07-08 10:58
 */
@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    private JWTConfig jwtConfig;

    @Resource
    private IgnoreProperties ignoreProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();
        //跳过白名单校验
        if (StringUtils.isNotEmpty(url) && !CollectionUtil.isEmpty(ignoreProperties.getWhiteUrls())){
            AntPathMatcher matcher = new AntPathMatcher();
            for (String whiteUrl : ignoreProperties.getWhiteUrls()) {
                if (matcher.match(whiteUrl, url)){
                    return chain.filter(exchange);
                }
            }
        }
        String token = exchange.getRequest().getHeaders().getFirst(jwtConfig.getTokenHeader());
        if (StrUtil.isEmpty(token)) {
            ServerHttpResponse response = exchange.getResponse();
            return ResponseUtils.writeErrorInfo(response, ResultCode.UNAUTHORIZED);
        }
        try {
            //todo: 进行token有效性校验后，从token中解析用户信息并设置到Header中去
            String realToken = token.replace(jwtConfig.getTokenPrefix(), "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String userStr = jwsObject.getPayload().toString();
            log.info("AuthGlobalFilter.filter() user:{}",userStr);
            ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().header(jwtConfig.getTokenHeader(), userStr).build();
            exchange = exchange.mutate().request(serverHttpRequest).build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
