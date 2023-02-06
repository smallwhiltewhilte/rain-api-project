package com.xuecheng.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.xuecheng.learning.config.PayNotifyConfig;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.service.MyCourseTablesService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author wangzan
 * @version 1.0
 * @description 接收支付结果通知service
 * @date 2023/1/31
 */
@Slf4j
@Service
public class ReceivePayNotifyService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    MyCourseTablesService myCourseTablesService;
    @Autowired
    XcChooseCourseMapper chooseCourseMapper;

    //接收支付结果通知
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = PayNotifyConfig.CHOOSECOURSE_PAYNOTIFY_QUEUE),
//            exchange = @Exchange(value = PayNotifyConfig.PAYNOTIFY_EXCHANGE_FANOUT, type = ExchangeTypes.FANOUT)
//
//    ))
    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_QUEUE)
    public void receive(String message) {
        //获取消息
        MqMessage mqMessage = JSON.parseObject(message, MqMessage.class);
        // 判断该消息类型
        String messageType = mqMessage.getMessageType();
        String businessKey2 = mqMessage.getBusinessKey2();
        // 只处理处理结果通知的消息,并且是学生购买课程的订单消息
        if (PayNotifyConfig.MESSAGE_TYPE.equals(messageType)&&"60201".equals(businessKey2)) {
            String businessKey1 = mqMessage.getBusinessKey1();
            // 根据选课id查询选课表的记录
            XcChooseCourse chooseCourse = chooseCourseMapper.selectById(businessKey1);
            if (chooseCourse == null) {
                log.info("收到支付结果通知，查询不到选课记录，businessKey1:{}",businessKey1);
                return;
            }
            // 选课成功
            chooseCourse.setStatus("701001");
            int update = chooseCourseMapper.updateById(chooseCourse);
            if (update>0){
                chooseCourse = chooseCourseMapper.selectById(businessKey1);
                // 向我的课程表添加记录
                myCourseTablesService.addCourseTabls(chooseCourse);
                // 发送回复
                send(mqMessage);
            }
        }

    }

    /**
     * @description 回复消息
     * @param message  回复消息
     * @return void
     * @date 2023/2/2
     */
    public void send(MqMessage message){
        //转json
        String msg = JSON.toJSONString(message);
        // 发送消息
        rabbitTemplate.convertAndSend(PayNotifyConfig.PAYNOTIFY_REPLY_QUEUE, msg);
        log.debug("学习中心服务向订单服务回复消息:{}",message);
    }



}
