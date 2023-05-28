package com.rain.rainapiinterface.controller;

import com.rain.rainapiclientsdk.model.User;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 名称API
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping("/get")
    public String getNameByGet(String name, HttpServletRequest request) {
        System.out.println(request.getHeader("wz"));
        return "GET 你的名字是" + name;
    }
    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) {
        /*String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");
        // todo 去数据库查询是否分配给用户
        if (!accessKey.equals("wxxb")) {
            throw new RuntimeException("无权限");
        }
        if (Long.parseLong(nonce) > 100000) {
            throw new RuntimeException("无权限");
        }
        // todo 时间和当前时间不超过五分钟
//        if (timestamp){
//
//        }
        // todo 实际情况中是从数据库中查出 secretKey
        String serverSign = SignUtils.getSign(body, "abcdefg");
        if (!sign.equals(serverSign)) {
            throw new RuntimeException("无权限");
        }*/

        return "POST 用户名字是" + user.getUsername();
    }
}
