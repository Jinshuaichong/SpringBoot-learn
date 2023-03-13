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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
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

    @Resource
    XcUserRoleMapper xcUserRoleMapper;

    @Resource
    WxAuthServiceImpl currentProxy;

    @Value("${weixin.appid}")
    String appid;

    @Value("${weixin.secret}")
    String secret;

    //微信认证方法
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //获取账号
        String username = authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, authParamsDto.getUsername()));
        if(xcUser==null){
            throw new RuntimeException("用户不存在");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);

        return xcUserExt;
    }

    @Override
    public XcUser wxAuth(String code) {
        //申请令牌
        Map<String, String> accessToken_map = getAccess_token(code);
        String accessToken = accessToken_map.get("access_token");
        String openid = accessToken_map.get("openid");

        //获取用户信息
        Map<String, String> userinfo = getUserinfo(accessToken, openid);

        //保存用户信息到数据库
        XcUser xcUser = currentProxy.addWxUser(userinfo);
        return null;
    }

    /**
     * @param code 授权码
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @MethodName getAccess_token
     * @Description 携带授权码申请令牌
     * @Author Metty
     * @Date 2023/3/13 13:11
     */
    private Map<String, String> getAccess_token(String code) {
        String url_template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        String url = String.format(url_template, appid, secret, code);

        //远程调用
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        //获取响应结果  并防止乱码
        String result = new String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        //转为map
        Map<String, String> map = JSON.parseObject(result, Map.class);
        return map;
    }

    /**
     * @param access_token 令牌
     * @param openid       openid
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @MethodName getUserinfo
     * @Description 携带令牌查询用户信息
     * @Author Metty
     * @Date 2023/3/13 14:56
     */
    private Map<String, String> getUserinfo(String access_token, String openid) {
        String url_template = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String url = String.format(url_template, access_token, openid);

        //远程调用
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        //获取响应结果
        String result = new String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        //转为map
        Map<String, String> map = JSON.parseObject(result, Map.class);
        return map;
    }

    /**
     * @param userInfo_map 用户信息map
     * @return com.xuecheng.ucenter.model.po.XcUser
     * @MethodName addWxUser
     * @Description 保存微信扫码用户的信息到数据库
     * @Author Metty
     * @Date 2023/3/13 15:05
     */
    @Transactional
    public XcUser addWxUser(Map<String, String> userInfo_map) {
        //根据unionid查询是否存在该用户
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, userInfo_map.get("unionid")));
        if (xcUser != null) {
            return xcUser;
        }
        //没有该用户便向数据库插入
        xcUser = new XcUser();
        String unionid = userInfo_map.get("unionid");
        String nickname = userInfo_map.get("nickname");
        String userId = UUID.randomUUID().toString();
        xcUser.setId(userId);
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        xcUser.setWxUnionid(unionid);
        xcUser.setNickname(nickname);
        xcUser.setUtype("101001");
        xcUser.setStatus("1");
        xcUser.setCreateTime(LocalDateTime.now());
        int insert = xcUserMapper.insert(xcUser);

        //向用户角色关系表新增记录
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        //17为学生角色
        xcUserRole.setRoleId("17");
        xcUserRole.setCreateTime(LocalDateTime.now());
        int insert1 = xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }
}
