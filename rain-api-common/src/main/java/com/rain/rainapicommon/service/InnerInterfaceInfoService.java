package com.rain.rainapicommon.service;

import com.rain.rainapicommon.model.entity.InterfaceInfo;

public interface InnerInterfaceInfoService  {

    /**
     * 数据库中查询模拟接口是否存在
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path,String method);
}
