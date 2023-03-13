package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.po.XcUser;

/**
 * @Description 微信扫码接入
 * @Date 2023/3/13,13:08
 * @Author Metty
 * @Version 1.0
 */

public interface WxAuthService {

    /**
     * @MethodName wxAuth
     * @Description 微信扫码认证 申请令牌,携带令牌查询用户信息,保存用户信息到数据库
     * @param code 授权码
     * @return com.xuecheng.ucenter.model.po.XcUser
     * @Author Metty
     * @Date 2023/3/13 13:09
    */
    public XcUser wxAuth(String code);
}
