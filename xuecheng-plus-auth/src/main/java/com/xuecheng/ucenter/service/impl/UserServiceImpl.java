package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 验证用户实现类
 * @date 2023/1/30
 */
@Slf4j
@Service("wx_authservice")
public class UserServiceImpl implements UserDetailsService {
    private static final String SUFFIX = "_authservice";
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    XcMenuMapper xcMenuMapper;
    @Autowired
    ApplicationContext applicationContext;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto = null;
        try {
            //将认证参数转为AuthParamsDto类型
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            log.info("认证请求不符合项目要求:{}", s);
            throw new RuntimeException("认证请求数据格式不对");
        }
        String authType = authParamsDto.getAuthType();
        AuthService authService = applicationContext.getBean(authType + SUFFIX, AuthService.class);
        //开始认证
        XcUserExt xcUserExt = authService.execute(authParamsDto);
        return getUserPrincipal(xcUserExt);
    }

    // 构造UserDetails对象
    public UserDetails getUserPrincipal(XcUserExt user) {
        // 调用mapper查询权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(user.getId());

        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] permissions = {"p1"};
        List<String> permissionList  = new ArrayList<>();
        xcMenus.forEach(xcMenu -> {
            permissionList.add(xcMenu.getCode());
        });
        if (permissionList.size()>0){
            permissions=permissionList.toArray(new String[0]);
        }
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userJson = JSON.toJSONString(user);
        //创建UserDetails对象
        UserDetails userDetails = User.withUsername(userJson).password("").authorities(permissions).build();
        return userDetails;
    }

}
