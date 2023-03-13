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
import com.xuecheng.ucenter.service.WxAuthService;
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
public class WxAuthServiceImpl implements AuthService, WxAuthService {

    @Resource
    XcUserMapper xcUserMapper;

    @Resource
    RestTemplate restTemplate;

    @Value("${weixin.appid}")
    String appid;

    @Value("${weixin.secret}")
    String secret;
    //微信认证方法
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //获取账号
        XcUser xcUser=xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername,authParamsDto.getUsername()));
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);

        return xcUserExt;
    }

    @Override
    public XcUser wxAuth(String code) {
        //申请令牌
        Map<String, String> accessToken = getAccess_token(code);


        return null;
    }

    /**
     * @MethodName getAccess_token
     * @Description 携带授权码申请令牌
     * @param code 授权码
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @Author Metty
     * @Date 2023/3/13 13:11
    */
    private Map<String,String> getAccess_token(String code){
        String url_template="https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        String url = String.format(url_template, appid, secret, code);

        //远程调用
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        //获取响应结果
        String result = exchange.getBody();
        //转为map
        Map<String,String> map = JSON.parseObject(result, Map.class);
        return map;
    }
}
