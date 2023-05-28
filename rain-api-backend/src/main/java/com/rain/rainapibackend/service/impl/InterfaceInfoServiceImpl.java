package com.rain.rainapibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rain.rainapibackend.common.ErrorCode;
import com.rain.rainapibackend.exception.BusinessException;
import com.rain.rainapibackend.mapper.InterfaceInfoMapper;
import com.rain.rainapibackend.service.InterfaceInfoService;
import com.rain.rainapicommon.model.entity.InterfaceInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String url = interfaceInfo.getUrl();
        String method = interfaceInfo.getMethod();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        // 创建时参数不能为空
        if (add) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (StringUtils.isAnyBlank(url)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (StringUtils.isAnyBlank(requestHeader)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (StringUtils.isAnyBlank(responseHeader)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (StringUtils.isAnyBlank(method)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
    }
}




