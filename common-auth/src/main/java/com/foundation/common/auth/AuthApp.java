package com.foundation.common.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author
 * @date 2022-07-07 10:21
 */
@SpringBootApplication
public class AuthApp {

    public static void main(String[] args) {
        SpringApplication.run(AuthApp.class, args);
        System.out.println("outh2 认证中心启动成功");
    }
}
