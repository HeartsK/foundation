package com.foundation.common.auth.componet;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author
 * @date 2022-07-11 15:29
 */
public class JwtTokenEnhancer implements TokenEnhancer {

    /**
     * JWT内容增强器
     *
     * @param accessToken    oAuth2AccessToken
     * @param authentication oAuth2Authentication
     * @return
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        Map<String, Object> additionalInformation = new HashMap<>();
        //给的参数是oAuth2的AccessToken，实现类是DefaultOAuth2AccessToken，
        //里面有个setAdditionalInformation方法添加自定义信息（Map类型）
        additionalInformation.put("username", user.getUsername());
        token.setAdditionalInformation(additionalInformation);
        return accessToken;
    }
}
