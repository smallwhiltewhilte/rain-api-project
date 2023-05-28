package com.rain.rainapibackend.service.impl.inner;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rain.rainapibackend.common.ErrorCode;
import com.rain.rainapibackend.exception.BusinessException;
import com.rain.rainapibackend.mapper.InterfaceInfoMapper;
import com.rain.rainapicommon.model.entity.InterfaceInfo;
import com.rain.rainapicommon.service.InnerInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
@Resource
private InterfaceInfoMapper interfaceInfoMapper;
    @Override
    @SentinelResource(value = "getInterfaceInfo", blockHandler = "handleBlock")
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url,method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<InterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(InterfaceInfo::getUrl,url);
        lambdaQueryWrapper.eq(InterfaceInfo::getMethod,method);
        return interfaceInfoMapper.selectOne(lambdaQueryWrapper);
    }
    public String handleBlock(BlockException ex) {
        ex.printStackTrace();
        return null;
    }
}




