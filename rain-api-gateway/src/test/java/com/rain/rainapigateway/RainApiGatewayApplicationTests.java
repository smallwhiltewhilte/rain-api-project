package com.rain.rainapigateway;

import com.rain.rainapicommon.service.InnerUserInterfaceInfoService;
//import com.rain.rainapigateway.feignclient.UserInterfaceInfoClient;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class RainApiGatewayApplicationTests {
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;
//    @Resource
//    private UserInterfaceInfoClient userInterfaceInfoClient;

    @Test
    void testDubbo() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            innerUserInterfaceInfoService.haveInvokeCount(1L, 1653076738506915841L);
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

    }

/*    @Test
    void testFeign() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            userInterfaceInfoClient.haveInvokeCount(1L,1653076738506915841L);
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }*/
}
