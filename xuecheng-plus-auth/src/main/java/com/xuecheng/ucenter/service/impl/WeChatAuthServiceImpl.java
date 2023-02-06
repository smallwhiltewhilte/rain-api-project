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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * @author wangzan
 * @version 1.0
 * @description 微信认证service实现类
 * @date 2023/1/30
 */
@Service
@Slf4j
public class WeChatAuthServiceImpl implements AuthService {
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    XcUserRoleMapper xcUserRoleMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    WeChatAuthServiceImpl currentProxy;
    @Value("${wechat.appid}")
    String appid;
    @Value("${wechat.secret}")
    String secret;
    @Value("${wechat.url.getAccessToken}")
    String urlAccessToken;

    @Value("${wechat.url.getUserinfo}")
    String urlUserInfo;

    // 申请令牌，查询用户
    public XcUser weChatAuth(String code) {
        Map<String, String> accessTokenMap = getAccessToken(code);

        String openid = accessTokenMap.get("openid");
        String accessToken = accessTokenMap.get("access_token");
        //拿access_token查询用户信息
        Map<String, String> userinfo = getUserinfo(accessToken, openid);
        // 添加用户到数据库
        return currentProxy.addWxUser(userinfo);
    }

    /**
     * 获取用户信息，示例如下：
     * {
     * "openid":"OPENID",
     * "nickname":"NICKNAME",
     * "sex":1,
     * "province":"PROVINCE",
     * "city":"CITY",
     * "country":"COUNTRY",
     * "headimgurl": "https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
     * "privilege":[
     * "PRIVILEGE1",
     * "PRIVILEGE2"
     * ],
     * "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     */
    @Transactional
    public XcUser addWxUser(Map<String, String> userInfoMap) {
        String unionid = userInfoMap.get("unionid").toString();
        //根据unionid查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid));
        if (xcUser != null) {
            // 该用户已存在
            return xcUser;
        }
        xcUser = new XcUser();
        String userId = UUID.randomUUID().toString();
        xcUser.setId(userId);
        xcUser.setWxUnionid(unionid);
        //记录从微信得到的昵称
        xcUser.setNickname(userInfoMap.get("nickname").toString());
        xcUser.setUserpic(userInfoMap.get("headimgurl").toString());
        xcUser.setName(userInfoMap.get("nickname").toString());
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        //学生类型
        xcUser.setUtype("101001");
        //用户状态
        xcUser.setStatus("1");
        xcUser.setCreateTime(LocalDateTime.now());
        xcUserMapper.insert(xcUser);
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        //学生角色
        xcUserRole.setRoleId("17");
        xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }

    // 请求微信获取接口

    /**
     * 微信接口响应结果
     * {
     * "access_token":"ACCESS_TOKEN",
     * "expires_in":7200,
     * "refresh_token":"REFRESH_TOKEN",
     * "openid":"OPENID",
     * "scope":"SCOPE",
     * "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     */
    private Map<String, String> getAccessToken(String code) {
        //请求微信地址
        String url = String.format(urlAccessToken, appid, secret, code);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        String responseString = response.getBody();
        return JSON.parseObject(responseString, Map.class);

    }

    private Map<String, String> getUserinfo(String accessToken, String openid) {
        //请求微信地址
        String url = String.format(urlUserInfo, accessToken, openid);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        String responseString = response.getBody();
        return JSON.parseObject(responseString, Map.class);
    }

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        String username = authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (xcUser == null) {
            throw new RuntimeException("用户不存在");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        return xcUserExt;
    }
}
