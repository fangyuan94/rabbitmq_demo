package com.fc.rabbitmq_demo.listener;

import com.fc.rabbitmq_demo.config.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.BatchMessageListener;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareBatchMessageListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.UUID;

/**
 * Simple
 * @author fangyuan
 */
@Slf4j
@ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = "type", havingValue = "direct")
@Configuration
public class DirectRabbitmqListenerConfiguration {

    @Autowired
    private DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory;

    /**
     * 这里有以下不同类型：
     * MessageListener 包含message 自动ack或不开启ack
     * ChannelAwareMessageListener 包含channel 用于主动ack确认
     * BatchMessageListener 批量接受数据消费 不包含
     * ChannelAwareBatchMessageListener
     */

    //单条消费
//    @Bean
    public MessageListenerContainer directMessageListenerContainer(){

        DirectMessageListenerContainer listenerContainer = directRabbitListenerContainerFactory.createListenerContainer();

        //设置被监控的queue
        listenerContainer.setQueueNames(CommonConstant.queue_direct1,CommonConstant.queue_direct2);

        //为每个队列添加多个消费者 增加并行度
        listenerContainer.setConsumersPerQueue(3);
        //不发出ack通知
        listenerContainer.setAcknowledgeMode(AcknowledgeMode.NONE);
        //设置消费者唯一标记 基于queue设置
        listenerContainer.setConsumerTagStrategy(queue -> {
            return queue+"_"+ UUID.randomUUID().toString();
        });

        listenerContainer.setMessageListener(message->{
            //获取数据
            byte[] body = message.getBody();
            String str = new String(body);

            Thread thread=Thread.currentThread();
            log.info("message:{}  ThreadId is:{}  ConsumerTag:{}  Queue:{}"
                    ,str,thread.getId(),message.getMessageProperties().getConsumerTag(),message.getMessageProperties().getConsumerQueue());

        });

        return listenerContainer;

    }


    @Bean
    public MessageListenerContainer directChannelMessageListenerContainer(){

        DirectMessageListenerContainer listenerContainer = directRabbitListenerContainerFactory.createListenerContainer();

        //设置被监控的queue
        listenerContainer.setQueueNames(CommonConstant.queue_direct1,CommonConstant.queue_direct2);

        //手动确认
        listenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        //为每个队列添加多个消费者 增加并行度
        listenerContainer.setConsumersPerQueue(2);

//        listenerContainer.setTaskExecutor();

        //设置消费者唯一标记 基于queue设置
        listenerContainer.setConsumerTagStrategy(queue -> {
            return queue+"_"+ UUID.randomUUID().toString();
        });

        //包含channel
        listenerContainer.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {

            Thread thread=Thread.currentThread();

            //获取数据
            long maxDeliveryTag = message.getMessageProperties().getDeliveryTag();
            //获取数据
            byte[] body = message.getBody();
            String str = new String(body);

            log.info("deliveryTag:{} message:{}  ThreadId :{} channel:{} ConsumerTag:{}  Queue:{} "
                    ,maxDeliveryTag,str,channel.getChannelNumber(),thread.getId(),message.getMessageProperties().getConsumerTag()
                    ,message.getMessageProperties().getConsumerQueue());

            //批量确认
            try {
                channel.basicAck(maxDeliveryTag,false);
            } catch (IOException e) {
                //确认失败 处理message数据需要回滚
                e.printStackTrace();
            }
        });

        return listenerContainer;

    }



}
