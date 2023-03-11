package com.xuecheng.auth.config;

import org.bouncycastle.crypto.params.DHUPublicParameters;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description 重写DaoAuthenticationProvider的additionalAuthenticationChecks校验密码方法,因为通过以了认证入口,有一些认证方式不需要认证密码
 * @Date 2023/3/11,10:06
 * @Author Metty
 * @Version 1.0
 */
@Component
public class DaoAuthenticationProviderCustom extends DaoAuthenticationProvider {
    @Resource
    public void setUserDetailsService(UserDetailsService userDetailsService){
        super.setUserDetailsService(userDetailsService);
    }


    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
