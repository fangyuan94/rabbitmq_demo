package com.fc.rabbitmq_demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.batch.BatchingStrategy;
import org.springframework.amqp.rabbit.batch.SimpleBatchingStrategy;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 对生产者进行设置
 * @author fangyuan
 */
@Configuration
@Slf4j
public class RabbitmqProducerAutoConfiguration {

    /**
     *
     * 若需要 使用到returnCallback 与confirmCallback 生产者确认机制
     * 其中confirmCallback：消息从生产者到达exchange时返回ack，消息未到达exchange返回nack(ack为false)；
     * returnCallBack：消息进入exchange但未进入queue时会被调用  这个时候说明路由失败了 需要进行 设置
     * 需要在配置中使用以下配置：
     * publisher-returns: true
     * publisher-confirm-type: correlated
     *
     */

    /**
     * 设置生产者的生产消息的ack信息回调(公共处理)
     * @return
     */
    @Bean
    public RabbitTemplate.ConfirmCallback confirmCallback(){

        //返回
        return (correlationData, ack, cause)->{
            //我们可以通过correlationData原始数据 来对消息进行后续处理，当时这是有个要求在于发送必须使用CorrelationData类
            //成功
            if(ack){
//                log.info("消息发送成功!!!!!,消息data:{}，时间:{}",correlationData,System.currentTimeMillis());
            }else {
                log.error("消息发送失败!!!!,原因是:{}",cause);
            }

        };
    }

    /**
     * 发送者失败通知
     */
    @Bean
    public RabbitTemplate.ReturnCallback returnCallback(){

        //构建一个
        return (Message message, int replyCode, String replyText, String exchange, String routingKey)->{
            log.error("发送者路由失败，请检查路由 Returned replyCode:{} Returned replyText:{} Returned routingKey:{} Returned message:{}"
                    ,  replyCode,replyText,routingKey,new String(message.getBody()));
        };
    }

    //设置RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        //若同步阻塞该ConfirmCallback 不用设置
        template.setConfirmCallback(confirmCallback());
        template.setReturnCallback(returnCallback());
        return template;
    }


    //批量发送message到rabbitmq
    @Bean
    public BatchingRabbitTemplate batchingRabbitTemplate(ConnectionFactory connectionFactory){

        //可自定义对该pool设置
        ThreadPoolTaskScheduler taskScheduler=new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.setDaemon(true);
        taskScheduler.setThreadNamePrefix("rabbitmq-");
        taskScheduler.setAwaitTerminationSeconds(60);
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化
        taskScheduler.initialize();
        //一次批量的数量
        int batchSize=10;
        // 缓存大小限制,单位字节，
        // simpleBatchingStrategy的策略，是判断message数量是否超过batchSize限制或者message的大小是否超过缓存限制，
        // 缓存限制，主要用于限制"组装后的一条消息的大小"
        // 如果主要通过数量来做批量("打包"成一条消息), 缓存设置大点
        // 详细逻辑请看simpleBatchingStrategy#addToBatch()
        int bufferLimit=1024; //1 K
        long timeout=10000;

        //注意，该策略只支持一个exchange/routingKey
        BatchingStrategy batchingStrategy=new SimpleBatchingStrategy(batchSize,bufferLimit,timeout);

        return  new BatchingRabbitTemplate(connectionFactory,batchingStrategy,taskScheduler);
    }

    //构建转换器
    @Bean
    public MessageConverter serializerMessageConverter(){

        return new SerializerMessageConverter();
    }
//
//
//    @Bean
//    public MessageConverter simpleMessageConverter(){
//
//        return new SimpleMessageConverter();
//    }
//
//
//    @Bean
//    public MessageConverter jackson2JsonMessageConverter(){
//
//        return new Jackson2JsonMessageConverter();
//    }

}
