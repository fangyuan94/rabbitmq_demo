package com.fc.rabbitmq_demo.listener;

import com.fc.rabbitmq_demo.config.CommonConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.ListenerContainerConsumerFailedEvent;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 使用注解方式进行消费
 * @author fangyaun
 */
@Component
@Slf4j
public class SimpleRabbitmqListenerAnnotation {


    /**
     *
     * @param message
     */
    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",ackMode = "AUTO",
            queues = {CommonConstant.queue_priority})
    public void handleMessage(Message message)  {

        Thread thread=Thread.currentThread();

        //消费者忙碌状态
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("==========auto-ack=======>message:{} priority:{} ThreadId is:{} "
                ,new String(message.getBody()),message.getMessageProperties().getPriority(),thread.getId());

    }


    /**
     * 手动确认
     *
     * @param message 需要处理消息
     * @param deliveryTag :使用@Header接口获取messageProperties中的DELIVERY_TAG属性。
     *
     */
//    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",ackMode = "MANUAL",
//            queues = {CommonConstant.queue_direct2,CommonConstant.queue_direct1})
    public void handleMessage2(@Payload String message, Channel channel,@Header(AmqpHeaders.CONSUMER_QUEUE) String consumer_queue, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        Thread thread=Thread.currentThread();
        log.info("==========ack=======>message:{}  ThreadId is:{}  channel:{} consumer_queue:{}"
                ,message,thread.getId(),channel.getChannelNumber(),consumer_queue);

          channel.basicReject(deliveryTag,false);

//        try {
//            int i = 1/0;
//            channel.basicAck(deliveryTag,false);
//        } catch (Exception e) {
//            e.printStackTrace();
//            //发生异常 数据推送到死信队列
//            channel.basicReject(deliveryTag,false);
//        }

    }

    /**
     *
     * @param message
     * @param channel
     * @param deliveryTag
     */
//    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",ackMode = "MANUAL",
//            queues = {CommonConstant.queue_direct2})
    public void handleMessage3(@Payload String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){

        Thread thread=Thread.currentThread();
        log.info("==========nack=======>message:{}  ThreadId is:{}  channel:{}"
                ,message,thread.getId(),channel.getChannelNumber());
        try {
            channel.basicNack(deliveryTag,false,true);
//            channel.basicAck(deliveryTag,false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
