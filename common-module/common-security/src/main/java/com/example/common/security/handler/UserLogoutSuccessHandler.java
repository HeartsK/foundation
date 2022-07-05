package com.example.common.security.handler;

import com.example.common.core.utils.api.R;
import com.example.common.core.utils.api.ResultUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author
 * @date 2022-07-05 16:27
 */
@Component
public class UserLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication e) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        ResultUtils.responseJson(response, R.success("", "登出成功"));
    }
}
