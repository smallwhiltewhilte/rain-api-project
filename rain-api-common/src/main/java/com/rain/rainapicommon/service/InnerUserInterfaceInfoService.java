package com.rain.rainapicommon.service;


public interface InnerUserInterfaceInfoService  {

    /**
     * 扣除调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean deductInvokeCount(long interfaceInfoId, long userId);

    /**
     * 判断是否还有调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean haveInvokeCount(long interfaceInfoId, long userId);
}
