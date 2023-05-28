package com.rain.rainapibackend.service;

import com.rain.rainapicommon.model.entity.InterfaceInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 帖子服务测试
 */
@SpringBootTest
class InterfaceInfoServiceTest {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Test
    void getInterfaceInfo() {
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(1);
        Assertions.assertNotNull(interfaceInfo);
    }

}