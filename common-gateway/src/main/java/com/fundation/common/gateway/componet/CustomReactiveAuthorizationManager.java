package com.fundation.common.gateway.componet;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.foundation.common.core.config.IgnoreProperties;
import com.foundation.common.core.config.JWTConfig;
import com.foundation.common.core.constant.AuthConstant;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定义授权管理器，判断用户是否有权限访问
 * @date 2022-07-12 14:14
 */
@Slf4j
@Component
@RefreshScope
public class CustomReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Resource
    private JWTConfig jwtConfig;

    @Resource
    private IgnoreProperties ignoreProperties;

    /**
     * 此处保存的是资源对应的权限，可以从数据库中获取
     */
    private static final Map<String, String> AUTH_MAP = Maps.newConcurrentMap();

    @PostConstruct
    public void initAuthMap() {
        AUTH_MAP.put("GET:/oauth/**", "admin");
        AUTH_MAP.put("GET:/swagger-resource/**", "admin");
        AUTH_MAP.put("GET:/login/**", "admin");
        AUTH_MAP.put("GET:/doc.html", "admin");
    }


    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        ServerWebExchange exchange = authorizationContext.getExchange();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethodValue();
        //todo 临时方案
        String authorities = AUTH_MAP.get(path);
        log.info("访问路径:[{}],所需要的权限是:[{}]", path, authorities);
        // option 请求，全部放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }
        //白名单请求全部放行
        AntPathMatcher matcher = new AntPathMatcher();
        for (String whiteUrl : ignoreProperties.getWhiteUrls()) {
            if (matcher.match(whiteUrl, path)){
                return Mono.just(new AuthorizationDecision(true));
            }
        }
        //判断是否携带token信息
        String token = request.getHeaders().getFirst(jwtConfig.getTokenHeader());
        if (StrUtil.isBlank(token) && !StrUtil.startWithIgnoreCase(token, jwtConfig.getTokenPrefix()) ) {
            return Mono.just(new AuthorizationDecision(false));
        }
        String restfulPath = method+":"+path;
        /**
         * 鉴权开始
         *
         * 缓存取 [URL权限-角色集合] 规则数据
         * urlPermRolesRules = [{'key':'GET:/api/v1/users/*','value':['ADMIN','TEST']},...]
         */
        //从redis中获取权限
//        Map<String, Object> urlPermRolesRules = redisTemplate.opsForHash().entries(GlobalConstants.URL_PERM_ROLES_KEY);
        // 带通配符的可以使用这个进行匹配
        PathMatcher pathMatcher = new AntPathMatcher();
        // 根据请求路径获取有访问权限的角色列表
        List<String> authorizedRoles = new ArrayList<>(); // 拥有访问权限的角色
        boolean requireCheck = false; // 是否需要鉴权，默认未设置拦截规则不需鉴权
        for (Map.Entry<String, String> permRoles : AUTH_MAP.entrySet()) {
            String perm = permRoles.getKey();
            if (pathMatcher.match(perm, restfulPath)) {
                List<String> roles = Convert.toList(String.class, permRoles.getValue());
                authorizedRoles.addAll(roles);
                if (!requireCheck) {
                    requireCheck = true;
                }
            }
        }
        // 没有设置拦截规则放行
        if (!requireCheck) {
            return Mono.just(new AuthorizationDecision(true));
        }
        // 判断JWT中携带的用户角色是否有权限访问
       return authentication
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authority -> {
                    String roleCode = StrUtil.removePrefix(authority, AuthConstant.AUTHORITY_PREFIX);// ROLE_ADMIN移除前缀ROLE_得到用户的角色编码ADMIN
                    /*if (GlobalConstants.ROOT_ROLE_CODE.equals(roleCode)) {
                        return true; // 如果是超级管理员则放行
                    }*/
                    return CollectionUtil.isNotEmpty(authorizedRoles) && authorizedRoles.contains(roleCode);
                })
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
