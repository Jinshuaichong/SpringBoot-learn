package com.xuecheng.ucenter.service.impl;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.stereotype.Service;

/**
 * @Description 账号密码模式登录
 * @Date 2023/3/11,10:18
 * @Author Metty
 * @Version 1.0
 */
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {


    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        return null;
    }
}
