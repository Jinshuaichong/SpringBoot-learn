package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.po.XcUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description 实现UserDetailsService的方法
 * @Date 2023/3/10,10:59
 * @Author Metty
 * @Version 1.0
 */
@Slf4j
@Component
public class UserServiceImpl implements UserDetailsService {
	
	@Resource
	XcUserMapper xcUserMapper;
	
	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		String username=s;
		//根据username查询数据库
		XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
		//查询到用户不存在 返回null即可,spring security框架会抛出异常
		if(xcUser==null){
			return null;
		}
		//查到了用户 拿到正确的密码封装成UserDetails对象给框架,由框架进行密码对比
		String password=xcUser.getPassword();
		String [] authorities={"test"};
		xcUser.setPassword(null);
		//将用户信息转为json
		String userJson = JSON.toJSONString(xcUser);
		return User.withUsername(userJson).password(password).authorities(authorities).build();
	}
}
