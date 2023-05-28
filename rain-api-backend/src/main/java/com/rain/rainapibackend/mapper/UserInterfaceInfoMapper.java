package com.rain.rainapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rain.rainapicommon.model.entity.UserInterfaceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户接口数据库操作
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(Integer limit);

    Integer getUserInvokeCount(@Param("userId") Long userId,@Param("interfaceInfoId") Long interfaceInfoId);
}




