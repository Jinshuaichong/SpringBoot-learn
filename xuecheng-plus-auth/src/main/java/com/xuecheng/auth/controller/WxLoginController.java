package com.xuecheng.auth.controller;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.WxAuthService;
import com.xuecheng.ucenter.service.impl.WxAuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @description TODO
 * @author Mr.M
 * @date 2022/10/20 16:47
 * @version 1.0
 */
@Slf4j
@Controller
public class WxLoginController {
     @Resource
     WxAuthService wxAuthService;
     @RequestMapping("/wxLogin")
     public String wxLogin(String code,String state) throws IOException{
         log.debug("微信扫码回调,code:{},state:{}",code,state);

         //远程调用微信申请令牌,拿到令牌查询用户信息,将用户信息写入本项目数据库
         XcUser xcUser1 = wxAuthService.wxAuth(code);
         XcUser xcUser=new XcUser();
         //暂时指定账号,测试一下
         xcUser.setUsername("t1");
         if(xcUser==null){
             return "rediect:http://www.xucheng-plus.com/error.html";
         }
         String username=xcUser.getUsername();
         return "rediect:http://www.xuecheng-plus.com/sign.html?username="+username+"&authType=wx";
     }



}
