package com.rain.rainapibackend.mapper;

import com.rain.rainapicommon.model.entity.InterfaceInfo;
import com.rain.rainapicommon.model.entity.UserInterfaceInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class UserInterfaceInfoMapperTest {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Test
    void testHaveInvokeCount() {
        System.out.println(userInterfaceInfoMapper.getUserInvokeCount(1653075282643070978L,2L));
    }
}