package com.xuecheng.checkcode.service.impl;

import com.xuecheng.checkcode.service.CheckCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author wangzan
 * @version 1.0
 * @description 使用本地内存存储验证码
 * @date 2023/1/30
 */
@Component("RedisCheckCodeStore")
public class RedisCheckCodeStore implements CheckCodeService.CheckCodeStore {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value, Integer expire) {
        stringRedisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
}
