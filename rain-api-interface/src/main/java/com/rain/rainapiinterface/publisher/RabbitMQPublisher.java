package com.rain.rainapiinterface.publisher;

import com.rain.rainapiinterface.model.dto.ArticleRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RabbitMQPublisher {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void publishTask(ArticleRequest articleRequest) {
        rabbitTemplate.convertAndSend("article_queue", articleRequest);
    }
}