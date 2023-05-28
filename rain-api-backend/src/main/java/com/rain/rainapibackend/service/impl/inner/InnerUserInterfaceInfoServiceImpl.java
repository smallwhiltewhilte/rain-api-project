package com.rain.rainapibackend.service.impl.inner;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.rain.rainapibackend.service.UserInterfaceInfoService;
import com.rain.rainapicommon.service.InnerUserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;


    @Override
    @SentinelResource(value = "deductInvokeCount", blockHandler = "handleBlock")
    public boolean deductInvokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.deductInvokeCount(interfaceInfoId, userId);
    }

    @Override
    @SentinelResource(value = "haveInvokeCount", blockHandler = "handleBlock")
    public boolean haveInvokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.haveInvokeCount(interfaceInfoId, userId);
    }
    public boolean handleBlock(BlockException ex) {
        ex.printStackTrace();
        return false;
    }
}




