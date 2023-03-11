package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/**
 * @Description 统一的认证接口
 * @Date 2023/3/11,10:16
 * @Author Metty
 * @Version 1.0
 */

public interface AuthService {


    /**
     * @MethodName execute
     * @Description 认证方法
     * @param authParamsDto 认证参数
     * @return com.xuecheng.ucenter.model.dto.XcUserExt
     * @Author Metty
     * @Date 2023/3/11 10:17
    */
    XcUserExt execute(AuthParamsDto authParamsDto);
}
