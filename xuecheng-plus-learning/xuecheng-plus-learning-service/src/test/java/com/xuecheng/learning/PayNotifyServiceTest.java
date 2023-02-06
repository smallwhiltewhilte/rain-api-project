package com.xuecheng.learning;

import com.xuecheng.learning.service.impl.ReceivePayNotifyService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description 支付结果通知测试类
 * @author wangzan
 * @date 2023/1/31
 * @version 1.0
 */
 @SpringBootTest
public class PayNotifyServiceTest {

  @Autowired
    ReceivePayNotifyService receivePayNotifyService;

  @Test
 public void testSend(){
   MqMessage mqMessage = new MqMessage();
   mqMessage.setId(100L);
      receivePayNotifyService.send(mqMessage);
  }

}
