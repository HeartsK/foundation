package com.foundation.common.core.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.foundation.common.core.constant.AuthConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author
 * @date 2022-07-04 16:42
 */
@Slf4j
public class JWTUtils {

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";

    /**
     * 从token中获取登录用户名
     */
    public static String getUserNameFromToken(String token, String secret) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token, secret);
            username =  claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
    /**
     * 校验token
     */
    public static boolean validateToken(String token, String username, String secret) {
        String usernameFromToken = getUserNameFromToken(token, secret);
        return usernameFromToken.equals(username) && !isTokenExpired(token, secret);
    }

    /**
     * 根据用户信息生成token
     */
    public static String generateToken(String username, String secret, Long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims, secret, expiration);
    }

    /**
     * 判断token是否已经失效
     */
    private static boolean isTokenExpired(String token, String secret) {
        Date expiredDate = getClaimsFromToken(token, secret).getExpiration();
        return expiredDate.before(new Date());
    }

    private static String generateToken(Map<String, Object> claims, String secret, Long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate(expiration))
                //签名算法
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 生成token的过期时间
     */
    private static Date generateExpirationDate(Long expiration) {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    private static Claims getClaimsFromToken(String token, String secret) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.info("JWT格式验证失败:{}",token);
        }
        return claims;
    }

    public static JSONObject getJwtPayload() {
        JSONObject jsonObject = null;
        String payload = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getHeader(AuthConstant.JWT_PAYLOAD_KEY);
        if (StrUtil.isNotBlank(payload)) {
            try {
                jsonObject = JSONUtil.parseObj(URLDecoder.decode(payload, StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
        }
        return jsonObject;
    }

}
