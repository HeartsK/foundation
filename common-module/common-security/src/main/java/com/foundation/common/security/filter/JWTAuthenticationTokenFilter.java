package com.foundation.common.security.filter;

import com.foundation.common.core.config.JWTConfig;
import com.foundation.common.security.service.UserService;
import com.foundation.common.security.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author
 * @date 2022-07-05 11:34
 */
@Slf4j
public class JWTAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private JWTConfig jwtConfig;

    @Resource
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader(jwtConfig.getTokenHeader());
        if (authHeader != null && authHeader.startsWith(jwtConfig.getTokenPrefix())){
            String authToken = authHeader.substring(jwtConfig.getTokenPrefix().length());// The part after "Bearer "
            String username = JWTUtils.getUserNameFromToken(authToken, jwtConfig.getSecret());
            log.info("checking username:{}", username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userService.loadUserByUsername(username);
                if (JWTUtils.validateToken(authToken, userDetails, jwtConfig.getSecret())) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    log.info("authenticated user:{}", username);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
