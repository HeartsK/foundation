package com.foundation.common.auth.config;

import com.foundation.common.auth.componet.JwtTokenEnhancer;
import com.foundation.common.auth.exception.CustomWebResponseExceptionTranslator;
import com.foundation.common.auth.extension.refresh.PreAuthenticatedUserDetailsService;
import com.foundation.common.auth.service.impl.ClientDetailsServiceImpl;
import com.foundation.common.core.constant.AuthConstant;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author
 * @date 2022-07-11 14:44
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private UserDetailsService userDetailsService;
    @Resource(name = "jwtTokenStore")
    private TokenStore tokenStore;
    @Resource(name = "jwtAccessTokenConverter")
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Resource
    private JwtTokenEnhancer jwtTokenEnhancer;
    @Resource
    private ClientDetailsServiceImpl clientDetailsService;

    /**
     * 定义授权和令牌端点以及令牌服务
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        //TokenEnhancerChain是TokenEnhance的一个实现类
        TokenEnhancerChain chain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(jwtTokenEnhancer);
        delegates.add(jwtAccessTokenConverter);//还要把转换器放进去用来实现jwtTokenEnhancer的互相转换
        chain.setTokenEnhancers(delegates);
        // 获取原有默认授权模式(授权码模式、密码模式、客户端模式、简化模式)的授权者
        List<TokenGranter> granterList = new ArrayList<>(Collections.singletonList(endpoints.getTokenGranter()));
        CompositeTokenGranter compositeTokenGranter = new CompositeTokenGranter(granterList);
        endpoints.authenticationManager(authenticationManager)
                //可以看到主要是增加了 JwtAccessTokenConverter JWT访问令牌转换器和JwtTokenStore JWT令牌存储组件，
                //通过AuthorizationServerEndpointsConfigurer 授权服务器端点配置加入两个实例
                .tokenStore(tokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)
                .tokenGranter(compositeTokenGranter)
                .exceptionTranslator(new CustomWebResponseExceptionTranslator())
                .tokenServices(tokenServices(endpoints))
                .tokenEnhancer(chain); //设置JWT增强内容
    }

    /**
     * 授权配置
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /*传来的参数clients是我们的应用，要去找授权服务器授权，授权完了之后会给我们授权码，我们
         * （client）拿着授权码再到授权服务器去获取令牌，获取到令牌之后拿着令牌去资源服务器获取资源
         * */
        clients.withClientDetails(clientDetailsService);
//        clients.inMemory() //.inMemory()放入内存。我们为了方便，直接放在内存中生成client，正常情况下是我们主动找授权服务器注册的时候才会有处理。
//                .withClient(AuthConstant.TEST_CLIENT_ID) //指定client。参数为唯一client的id
//                .secret(passwordEncoder.encode("123456")) //指定密钥
//                .redirectUris("http://www.baidu.com") //指定重定向的地址,通过重定向地址拿到授权码。
//                .redirectUris("http://localhost:8081/login") //单点登录到另一服务器
//                .accessTokenValiditySeconds(60 * 10) //设置Access Token失效时间
//                .refreshTokenValiditySeconds(60 * 60 * 24) //设置refresh token失效时间
//                .scopes("all") //指定授权范围
//                .autoApprove(true) //自动授权，不需要手动允许了
        /**
         * 授权类型：
         * "authorization_code" 授权码模式
         * "password"密码模式
         * "refresh_token" 刷新令牌
         */
//                .authorizedGrantTypes("authorization_code", "password", "refresh_token"); //指定授权类型 可以多种授权类型并存。

    }

    public DefaultTokenServices tokenServices(AuthorizationServerEndpointsConfigurer endpoints) {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setSupportRefreshToken(true);
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(jwtTokenEnhancer);
        tokenEnhancers.add(jwtAccessTokenConverter);
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);
        // 多用户体系下，刷新token再次认证客户端ID和 UserDetailService 的映射Map
        Map<String, UserDetailsService> clientUserDetailsServiceMap = new HashMap<>();
        clientUserDetailsServiceMap.put(AuthConstant.ADMIN_CLIENT_ID, userDetailsService);
//        clientUserDetailsServiceMap.put(SecurityConstants.ADMIN_CLIENT_ID, sysUserDetailsService); // 系统管理客户端
//        clientUserDetailsServiceMap.put(SecurityConstants.APP_CLIENT_ID, memberUserDetailsService); // Android、IOS、H5 移动客户端
//        clientUserDetailsServiceMap.put(SecurityConstants.WEAPP_CLIENT_ID, memberUserDetailsService); // 微信小程序客户端
        // 刷新token模式下，重写预认证提供者替换其AuthenticationManager，可自定义根据客户端ID和认证方式区分用户体系获取认证用户信息
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new PreAuthenticatedUserDetailsService<>(clientUserDetailsServiceMap));
        tokenServices.setAuthenticationManager(new ProviderManager(Collections.singletonList(provider)));
        /** refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
         *  1 重复使用：access_token过期刷新时， refresh_token过期时间未改变，仍以初次生成的时间为准
         *  2 非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新便永不失效达到无需再次登录的目的
         */
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setReuseRefreshToken(true);
        return tokenServices;
    }

    /**
     * 单点登录配置
     *
     * //@param security
     * //@throws Exception
     */
    /*@Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //必须要身份认证，单点登录必须要配置
        security.tokenKeyAccess("isAuthenticated()");
    } */
}
