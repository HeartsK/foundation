package com.foundation.common.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 身份信息获取
 */
@RestController
@RequestMapping("/oauth")
public class UserController
{
    @RequestMapping("/user")
    public Principal user(Principal user)
    {
        return user;
    }
}
