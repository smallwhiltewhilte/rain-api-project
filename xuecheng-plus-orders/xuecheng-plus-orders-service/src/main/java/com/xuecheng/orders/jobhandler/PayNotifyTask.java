package com.xuecheng.orders.jobhandler;

import com.alibaba.fastjson.JSON;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.orders.config.PayNotifyConfig;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wangzan
 * @version 1.0
 * @description 接收支付结果类
 * @date 2023/2/2
 */
@Component
@Slf4j
public class PayNotifyTask extends MessageProcessAbstract {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    MqMessageService mqMessageService;

    @XxlJob("NotifyPayResultJobHandler")
    public void notifyPayResultJobHandler() {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex: " + shardIndex + ",shardTotal: " + shardTotal);
        process(shardIndex, shardTotal, PayNotifyConfig.MESSAGE_TYPE, 10, 60);
    }

    @Override
    public boolean execute(MqMessage mqMessage) {
        log.debug("向消息队列发送支付结果通知消息:{}", mqMessage);
        send(mqMessage);

        return false;
    }

    // 监听支付结果通过回复队列
    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_REPLY_QUEUE)
    public void receive(String message) {
        log.debug("收到支付结果通知回复:{}", message);
        MqMessage mqMessage = JSON.parseObject(message, MqMessage.class);
        // 删除消息
        mqMessageService.completed(mqMessage.getId());
    }

    /**
     * @param mqMessage 消息内容
     * @return void
     * @description 发送支付结果通知
     * @date 2023/2/2
     **/
    private void send(MqMessage mqMessage) {
        // 发布消息
        String MessageJson = JSON.toJSONString(mqMessage);
        rabbitTemplate.convertAndSend(PayNotifyConfig.PAYNOTIFY_EXCHANGE_FANOUT, "", MessageJson);
        log.debug("向消息队列发送支付结果通知消息完成:{}", mqMessage);

    }
}
