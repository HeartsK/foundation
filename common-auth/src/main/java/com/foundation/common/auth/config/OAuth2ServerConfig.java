package com.foundation.common.auth.config;

import com.foundation.common.auth.componet.JwtTokenEnhancer;
import com.foundation.common.auth.constant.AuthConstant;
import com.foundation.common.auth.exception.CustomWebResponseExceptionTranslator;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @date 2022-07-11 14:44
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    @Resource
    private PasswordEncoder passwordEncoder;
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
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                //可以看到主要是增加了 JwtAccessTokenConverter JWT访问令牌转换器和JwtTokenStore JWT令牌存储组件，
                //通过AuthorizationServerEndpointsConfigurer 授权服务器端点配置加入两个实例
                .tokenStore(tokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)
                .exceptionTranslator(new CustomWebResponseExceptionTranslator())
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

        clients.inMemory() //.inMemory()放入内存。我们为了方便，直接放在内存中生成client，正常情况下是我们主动找授权服务器注册的时候才会有处理。
                .withClient(AuthConstant.ADMIN_CLIENT_ID) //指定client。参数为唯一client的id
                .secret(passwordEncoder.encode("112233")) //指定密钥
//                .redirectUris("http://www.baidu.com") //指定重定向的地址,通过重定向地址拿到授权码。
                .redirectUris("http://localhost:8081/login") //单点登录到另一服务器
                .accessTokenValiditySeconds(60 * 10) //设置Access Token失效时间
                .refreshTokenValiditySeconds(60 * 60 * 24) //设置refresh token失效时间
                .scopes("all") //指定授权范围
                .autoApprove(true) //自动授权，不需要手动允许了
                /**
                 * 授权类型：
                 * "authorization_code" 授权码模式
                 * "password"密码模式
                 * "refresh_token" 刷新令牌
                 */
                .authorizedGrantTypes("authorization_code", "password", "refresh_token"); //指定授权类型 可以多种授权类型并存。

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
