package com.xuecheng.messagesdk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * @author wangzan
 * @version 1.0
 * @description 消息处理测试类
 * @date 2023/1/29
 */
@SpringBootTest
public class MessageProcessClassTest {

    @Autowired
    MessageProcessClass messageProcessClass;

    @Test
    public void test() throws InterruptedException {

        System.out.println("开始执行-----》" + LocalDateTime.now());
        messageProcessClass.process(0, 1, "test", 2, 10);

        System.out.println("结束执行-----》" + LocalDateTime.now());
        Thread.sleep(999999);
    }
}
