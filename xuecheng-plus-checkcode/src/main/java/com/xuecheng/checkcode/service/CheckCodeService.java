package com.xuecheng.checkcode.service;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author wangzan
 * @version 1.0
 * @description 验证码接口
 * @date 2023/1/30
 */
public interface CheckCodeService {


    /**
     * @param checkCodeParamsDto 生成验证码参数
     * @return com.xuecheng.checkcode.model.CheckCodeResultDto 验证码结果
     * @description 生成验证码
     * @date 2023/1/30
     */
    CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto);

    /**
     * @param key
     * @param code
     * @return boolean
     * @description 校验验证码
     * @date 2023/1/30
     */
    boolean verify(String key, String code);


    /**
     * @description 验证码生成器
     * @date 2023/1/30
     */
    interface CheckCodeGenerator {
        /**
         * 验证码生成
         *
         * @return 验证码
         */
        String generate(int length);


    }

    /**
     * @description key生成器
     * @date 2023/1/30
     */
    interface KeyGenerator {

        /**
         * key生成
         *
         * @return 验证码
         */
        String generate(String prefix);
    }


    /**
     * @description 验证码存储
     * @date 2023/1/30
     */
    interface CheckCodeStore {

        /**
         * @param key    key
         * @param value  value
         * @param expire 过期时间,单位秒
         * @return void
         * @description 向缓存设置key
         * @date 2023/1/30
         */
        void set(String key, String value, Integer expire);

        String get(String key);
    }
}
