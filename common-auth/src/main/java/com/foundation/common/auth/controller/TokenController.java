package com.foundation.common.auth.controller;

import com.foundation.common.core.utils.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * token 控制
 * 
 */
@RestController
@RequestMapping("/token")
@Api(value = "token处理", tags = "token处理")
public class TokenController
{
    @Resource
    private TokenStore tokenStore;

    @DeleteMapping("/logout")
    @ApiOperation(value = "等处token清楚")
    public R<?> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader)
    {
        if (StringUtils.isEmpty(authHeader))
        {
            return R.success(null);
        }

        String tokenValue = authHeader.replace(OAuth2AccessToken.BEARER_TYPE, StringUtils.EMPTY).trim();
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
        if (accessToken == null || StringUtils.isEmpty(accessToken.getValue()))
        {
            return R.success(null);
        }

        // 清空 access token
        tokenStore.removeAccessToken(accessToken);

        // 清空 refresh token
        OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
        tokenStore.removeRefreshToken(refreshToken);
        return R.success(null);
    }
}
