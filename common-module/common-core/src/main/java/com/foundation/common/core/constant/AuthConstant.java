package com.foundation.common.core.constant;

/**
 * @author
 * @date 2022-07-12 11:44
 */
public class AuthConstant {

    public static final String AUTHORITY_PREFIX = "ROLE_";

    public static final String JWT_AUTHORITIES_KEY = "authorities";

    public static final String ADMIN_CLIENT_ID = "admin";

    public static final String PORTAL_CLIENT_ID = "portal";
    /**
     * 接口文档 Knife4j 测试客户端ID
     */
    public static final String TEST_CLIENT_ID = "client";

    public static final String GRANT_TYPE_KEY = "grant_type";

    public static final String CLIENT_ID_KEY = "client_id";
    /**
     * 认证请求头key
     */
    public static final String AUTHORIZATION_KEY = "Authorization";

    public static final String BASIC_PREFIX = "Basic ";

    public static final String REFRESH_TOKEN_KEY = "refresh_token";

    public static final String AUTHENTICATION_IDENTITY_KEY = "authenticationIdentity";

    public static final String JWT_PAYLOAD_KEY = "payload";
    /**
     * JWT ID 唯一标识
     */
    public static final String JWT_JTI = "jti";
    /**
     * JWT ID 唯一标识
     */
    public static final  String JWT_EXP = "exp";
    /**
     * 黑名单token前缀
     */
    public static final  String TOKEN_BLACKLIST_PREFIX = "auth:token:blacklist:";
}
