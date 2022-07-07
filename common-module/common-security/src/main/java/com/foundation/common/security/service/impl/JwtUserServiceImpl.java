package com.foundation.common.security.service.impl;

import com.foundation.common.security.model.SecurityUser;
import com.foundation.common.security.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @date 2022-07-06 14:51
 */
@Service
public class JwtUserServiceImpl implements UserService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //定义权限列表.
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 用户可以访问的资源名称（或者说用户所拥有的权限） 注意：必须"ROLE_"开头
//        authorities.add(new SimpleGrantedAuthority("ROLE_"+ userInfo.getRole()));
        authorities.add(new SimpleGrantedAuthority("ROLE_admin"));
        return new SecurityUser("test",passwordEncoder.encode("123456"),authorities);
    }
}
