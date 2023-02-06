package com.xuecheng.ucenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wangzan
 * @version 1.0
 * @description 远程调用验证码
 * @date 2023/1/30
 */
@FeignClient(value = "checkcode",fallbackFactory = CheckCodeClientFactory.class)
@RequestMapping("/checkcode")
public interface CheckCodeClient {
    @PostMapping(value = "/verify")
    Boolean verify(@RequestParam("key") String key,@RequestParam("code") String code);
}
