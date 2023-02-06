package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author wangzan
 * @version 1.0
 * @description 账号密码认证
 * @date 2023/1/30
 */
@Service("password_authservice")
@Slf4j
public class PasswordAuthServiceImpl implements AuthService {
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        // 得到验证码
        String checkcode = authParamsDto.getCheckcode();
        String checkcodekey = authParamsDto.getCheckcodekey();
        if (StringUtils.isBlank(checkcodekey) || StringUtils.isBlank(checkcode)) {
            throw new RuntimeException("验证码为空");
        }
        // 校验验证码，请求验证码服务器进行校验
        Boolean result = checkCodeClient.verify(checkcodekey, checkcode);
        if (result == null || !result) {
            throw new RuntimeException("验证码错误");
        }
        String username = authParamsDto.getUsername();
        // 查询数据库用户信息
        LambdaQueryWrapper<XcUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(XcUser::getUsername, username);
        XcUser user = xcUserMapper.selectOne(lambdaQueryWrapper);
        if (user == null) {
            // 账号不存在
            throw new RuntimeException("账号不存在");
        }
        // 比对密码
        // 数据库中正确的密码
        String passwordDB = user.getPassword();
        String passwordInput = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(passwordInput, passwordDB);
        if (!matches) {
            throw new RuntimeException("账号或密码错误");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user, xcUserExt);
        return xcUserExt;
    }
}
