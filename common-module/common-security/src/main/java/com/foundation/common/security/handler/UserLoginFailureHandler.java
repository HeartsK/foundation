package com.foundation.common.security.handler;

import com.foundation.common.core.utils.api.R;
import com.foundation.common.core.utils.api.ResponseUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author
 * @date 2022-07-05 14:55
 */
public class UserLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        if (e instanceof BadCredentialsException){
            ResponseUtils.responseJson(response, R.success(null, "用户名密码不正确！"));
        }
        ResponseUtils.responseJson(response, R.success(null,"登录失败，错误信息:"+e.getMessage()));
    }
}
