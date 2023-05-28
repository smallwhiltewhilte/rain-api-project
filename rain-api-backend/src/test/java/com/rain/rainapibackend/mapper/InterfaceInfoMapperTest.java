package com.rain.rainapibackend.mapper;

import com.rain.rainapicommon.model.entity.InterfaceInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class InterfaceInfoMapperTest {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Test
    void listPostWithDelete() {
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(1);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoMapper.selectList(null);
        Assertions.assertNotNull(interfaceInfo);
    }
}