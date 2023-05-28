package com.rain.rainapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.rain.rainapibackend.common.ErrorCode;
import com.rain.rainapibackend.exception.BusinessException;
import com.rain.rainapibackend.mapper.UserInterfaceInfoMapper;
import com.rain.rainapibackend.model.vo.InterfaceInfoCountVO;
import com.rain.rainapibackend.service.AnalysisService;
import com.rain.rainapibackend.service.InterfaceInfoService;
import com.rain.rainapicommon.model.entity.InterfaceInfo;
import com.rain.rainapicommon.model.entity.UserInterfaceInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;
    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Override
    public List<InterfaceInfoCountVO> listTopInvokeInterfaceInfo() {
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        Map<Long, List<UserInterfaceInfo>> interfaceInfoObjectMap = userInterfaceInfoList.stream().collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        LambdaQueryWrapper<InterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(InterfaceInfo::getId,interfaceInfoObjectMap.keySet());
        List<InterfaceInfo> list = interfaceInfoService.list(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(list)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        List<InterfaceInfoCountVO> interfaceInfoCountVOList = list.stream().map(interfaceInfo -> {
            InterfaceInfoCountVO interfaceInfoCountVO = new InterfaceInfoCountVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoCountVO);
            Integer totalNum = interfaceInfoObjectMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceInfoCountVO.setInvokeCount(totalNum);
            return interfaceInfoCountVO;
        }).collect(Collectors.toList());
        return interfaceInfoCountVOList;
    }
}
