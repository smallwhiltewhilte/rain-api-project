package com.rain.rainapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rain.rainapicommon.model.entity.UserInterfaceInfo;

public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean deductInvokeCount(long interfaceInfoId, long userId);

    boolean haveInvokeCount(long interfaceInfoId, long userId);
}
