package com.fc.rabbitmq_demo.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发送信息
 * @author fangyuan
 */
@Component
@Slf4j
public class SendMessage {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routingKey, Object payload){

        rabbitTemplate.convertAndSend(exchange,routingKey,payload);

    }

}
