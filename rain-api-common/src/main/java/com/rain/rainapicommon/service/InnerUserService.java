package com.rain.rainapicommon.service;

import com.rain.rainapicommon.model.entity.User;

/**
 * 用户服务
 */
public interface InnerUserService {
    /**
     * 数据库中查是否已分配给用户密钥（accessKey）
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);


}
