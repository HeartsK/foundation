package com.foundation.common.core.utils.api;

import org.springframework.http.HttpStatus;

/**
 * @author
 * @date 2022-07-05 11:44
 */
public enum ResultCode{

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    TOKEN_INVALID_OR_EXPIRED(230, "token无效或已过期"),
    TOKEN_ACCESS_FORBIDDEN(231, "token已被禁止访问"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS.value(), "请求过于频繁，请稍后访问");

    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
