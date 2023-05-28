package com.rain.rainapibackend.service.impl.inner;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rain.rainapibackend.common.ErrorCode;
import com.rain.rainapibackend.exception.BusinessException;
import com.rain.rainapibackend.mapper.UserMapper;
import com.rain.rainapicommon.model.entity.User;
import com.rain.rainapicommon.service.InnerUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
@DubboService
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    private UserMapper userMapper;

    @Override
    @SentinelResource(value = "getInvokeUser", blockHandler = "handleBlock")
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getAccessKey,accessKey);
        return userMapper.selectOne(lambdaQueryWrapper);
    }
    public String handleBlock(BlockException ex) {
        ex.printStackTrace();
        return null;
    }
}




