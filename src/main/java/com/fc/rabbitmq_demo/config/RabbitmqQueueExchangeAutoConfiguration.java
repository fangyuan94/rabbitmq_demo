package com.fc.rabbitmq_demo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.fc.rabbitmq_demo.config.CommonConstant.*;

/**
 *初始化 queue exchange 并进行Binding绑定
 * @author fanyuan
 */
@Configuration
public class RabbitmqQueueExchangeAutoConfiguration {


    /**
     * 默认有四种bean 对于rabbitmq四种交换器
     * DirectExchange: 直连型 和queue一一对应
     * FanoutExchange: 广播型 广播exchange下多有queue
     * TopicExchange: 通过routeKey和 Binding Key 进行模糊匹配 进行路由发送queue
     * HeadersExchange：而根据消息中的 Headers 和创建绑定关系时指定的 Arguments 来匹配决定路由到哪些 Queue基本不用
     */

    /*
     * 设置交换器 exchange
     */
    @Bean
    public Exchange directExchange(){
        return new DirectExchange(exchange_direct);
    }

    @Bean
    public Exchange fanoutExchange(){

        return new FanoutExchange(exchange_fanout);
    }

    @Bean
    public Exchange topicExchange(){

        return new TopicExchange(exchange_topic);
    }

    /**
     * Queue创建有4个属性
     * @param name 队列名称
     * @param durable 设置是否为持久性队列 如果为持久队列，则为true（该队列将在服务器重新启动后继续有效，否则重启后失效） 默认为true
     * @param exclusive 设置是否为独占队列(该队列将仅由声明者的连接使用) 如果声明独占队列，则为true 默认为false
     * @param autoDelete 设置是否自动删除 当该队列不再使用了是否自动删除
     */

    /*创建queue*/

    @Bean
    public Queue directQueue2(){
        return  new Queue(queue_direct2,true,false,false);
    }


    /**
     * 声明创建一个queue
     * @return
     */
    @Bean
    public Queue directQueue1(){

        Map<String, Object> arguments  = new HashMap<>();

        //声明当前死信的 Exchange
        arguments.put("x-dead-letter-exchange",exchange_deadLetter_direct);

        arguments.put("x-dead-letter-routing-key",routingKey_deadLetter);

        return  new Queue(queue_direct1,true,false,false,arguments);
    }


    /**
     * 构建死信队列
     * @return
     */
    @Bean
    public Queue deadLetterQueue(){

        return  new Queue(queue_deadLetter,true,false,false);
    }


    @Bean
    public Queue ttlQueue(){

        Map<String, Object> arguments  = new HashMap<>();

        //声明当前死信的 Exchange 10s超时
//        arguments.put("x-message-ttl",10*1000);

        arguments.put("x-dead-letter-exchange",exchange_deadLetter_direct);

        arguments.put("x-dead-letter-routing-key",routingKey_deadLetter);

        return  new Queue(queue_ttl,true,false,false,arguments);
    }

    @Bean
    public Binding ttlBinding(){

        return BindingBuilder.bind(ttlQueue()).to(directExchange()).with(routingKey_ttl).noargs();
    }

    @Bean
    public Exchange priorityQExchange(){
        return new DirectExchange(exchange_priority);
    }

    /**
     *
     * @return
     */
    @Bean
    public Queue priorityQueue(){

        Map<String, Object> arguments  = new HashMap<>();

        //声明当前死信的 Exchange
        arguments.put("x-max-priority",200);

        return  new Queue(queue_priority,true,false,false,arguments);
    }


    @Bean
    public Binding priorityBinding(){
        return BindingBuilder.bind(priorityQueue()).to(priorityQExchange()).with(routingKey_priority).noargs();
    }


    //绑定死信队列
    @Bean
    public Binding deadLetterBinding(){

        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(routingKey_deadLetter).noargs();
    }

    @Bean
    public Exchange deadLetterExchange(){
        return new DirectExchange(exchange_deadLetter_direct);
    }


    @Bean
    public Queue fanoutQueue1(){
        return  new Queue(queue_fanoutQueue1,true,false,false);
    }

    @Bean
    public Queue fanoutQueue2(){
        return  new Queue(queue_fanoutQueue2,true,false,false);
    }

    @Bean
    public Queue topicQueue1(){
        return  new Queue(queue_topic1,true,false,false);
    }

    @Bean
    public Queue topicQueue2(){
        return  new Queue(queue_topic2,true,false,false);
    }




    /* 将exchange与topic进行绑定*/

    @Bean
    public Binding directBinding1(){

        return BindingBuilder.bind(directQueue1()).to(directExchange()).with(routingKey_direct1).noargs();
    }

    @Bean
    public Binding directBinding2(){

        return BindingBuilder.bind(directQueue2()).to(directExchange()).with(routingKey_direct2).noargs();
    }

    @Bean
    public Binding fanoutBinding1(){

        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange()).with("").noargs();
    }

    @Bean
    public Binding fanoutBinding2(){

        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange()).with("").noargs();
    }

    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(routingKey_topic1).noargs();
    }

    @Bean
    public Binding topicBinding2(){

        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(routingKey_topic2).noargs();
    }


}
