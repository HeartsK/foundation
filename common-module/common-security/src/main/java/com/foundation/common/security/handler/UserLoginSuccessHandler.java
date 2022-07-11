package com.foundation.common.security.handler;

import com.foundation.common.core.utils.api.R;
import com.foundation.common.core.utils.api.ResultUtils;
import com.foundation.common.security.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author
 * @date 2022-07-05 14:31
 */
@Slf4j
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler{

    @Resource
    private JWTConfig jwtConfig;

    /**
     * 登录成功返回结果
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 组装JWT
        UserDetails details = (UserDetails) authentication.getPrincipal();
        String token = JWTUtils.generateToken(details, jwtConfig.getSecret(), jwtConfig.getExpiration());
        token = jwtConfig.getTokenPrefix() + token;
        // 封装返回参数
        ResultUtils.responseJson(response, R.success(token,"登录成功"));
    }

}
