package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/**
 * @author wangzan
 * @version 1.0
 * @description 认证接口
 * @date 2023/1/30
 */
public interface AuthService {
    /**
     * @param authParamsDto 认证参数
     * @return com.xuecheng.ucenter.model.dto.XcUserExt
     * @description 认证方法
     * @date 2023/1/30
     **/

    XcUserExt execute(AuthParamsDto authParamsDto);
}
