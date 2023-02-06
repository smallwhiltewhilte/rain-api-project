package com.xuecheng.auth.controller;

import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.impl.WeChatAuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * @author wangzan
 * @version 1.0
 * @description 微信登录接口
 * @date 2023/1/30
 */
@Slf4j
@Controller
public class WeChatLoginController {
    @Autowired
    WeChatAuthServiceImpl weChatAuthService;

    @RequestMapping("/wxLogin")
    public String wxLogin(String code, String state) throws IOException {
        // 申请令牌，查询用户
        XcUser xcUser = weChatAuthService.weChatAuth(code);
        if (xcUser == null) {
            return "redirect:http://www.xuecheng-plus.com/error.html";
        } else {
            String username = xcUser.getUsername();
            //重定向登录页面，自动登录
            return "redirect:http://www.xuecheng-plus.com/sign.html?username=" + username + "&authType=wx";
        }
    }
}