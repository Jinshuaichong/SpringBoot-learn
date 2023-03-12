package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * @author Mr.M
 * @version 1.0
 * @Description TODO
 * @Date 2022/10/20 16:50
 */
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService {

    @Resource
    XcUserMapper xcUserMapper;
    //微信认证方法
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //获取账号
        XcUser xcUser=xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername,authParamsDto.getUsername()));
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);

        return xcUserExt;
    }
}
