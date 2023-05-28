package com.rain.rainapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rain.rainapibackend.common.ErrorCode;
import com.rain.rainapibackend.exception.BusinessException;
import com.rain.rainapibackend.mapper.UserInterfaceInfoMapper;
import com.rain.rainapibackend.service.UserInterfaceInfoService;
import com.rain.rainapicommon.model.entity.UserInterfaceInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userInterfaceInfo.getUserId();
        Long interfaceInfoId = userInterfaceInfo.getInterfaceInfoId();
        Integer totalNum = userInterfaceInfo.getTotalNum();
        Integer leftNum = userInterfaceInfo.getLeftNum();
        // 创建时参数不能为空
        if (add) {

            if (userId == null || userId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (interfaceInfoId == null || interfaceInfoId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (totalNum == null || totalNum < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }

        }
        if (leftNum == null || leftNum < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于 0");
        }
    }

    @Override
    public boolean deductInvokeCount(long interfaceInfoId, long userId) {
        // 校验
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaUpdateWrapper<UserInterfaceInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId);
        lambdaUpdateWrapper.eq(UserInterfaceInfo::getUserId, userId);
        lambdaUpdateWrapper.gt(UserInterfaceInfo::getLeftNum, 0);
        lambdaUpdateWrapper.setSql("left_num = left_num - 1 ,total_num = total_num + 1");
        return this.update(lambdaUpdateWrapper);
    }

    @Override
    public boolean haveInvokeCount(long interfaceInfoId, long userId) {
        // 校验
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userInterfaceInfoMapper.getUserInvokeCount(userId, interfaceInfoId) > 0;
    }
}




