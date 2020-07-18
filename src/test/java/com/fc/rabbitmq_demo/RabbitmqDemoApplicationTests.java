package com.fc.rabbitmq_demo;

import com.fc.rabbitmq_demo.config.CommonConstant;
import com.fc.rabbitmq_demo.sender.CorrelationData;
import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RabbitmqDemoApplication.class)
@Slf4j
public class RabbitmqDemoApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    //测试发送阻塞消息
    @Test
    public void testSendMessageSync(){

        Boolean flag = rabbitTemplate.invoke(operations -> {
            rabbitTemplate.convertAndSend(CommonConstant.exchange_direct, CommonConstant.routingKey_direct2, "测试数据direct2");
            log.info("发送message==============》");
            //同步阻塞
            return rabbitTemplate.waitForConfirms(3 * 1000);
        });
        //同步ack确认
        if(flag){
            log.info("消息发送成功");
        }else {
            //消息消费失败 后续处理 重发(消费者需要进行幂等控制)或其它
            log.error("消息发送失败");
        }
    }

    //异步ack
    @Test
    public void testSendMessageASyncCommon(){
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId("1");
        correlationData.setMessage("测试数据direct1");
        correlationData.setExchange(CommonConstant.exchange_direct);
        correlationData.setRoutingKey(CommonConstant.routingKey_direct1);
        correlationData.setRetryCount(3);
        //异步发送
        rabbitTemplate.convertAndSend(CommonConstant.exchange_direct,CommonConstant.routingKey_direct1,"这是一条测试数据",correlationData);


    }


    @Test
    public void testSendMessagePriority(){

        for (int i=1;i<200;i++){
            final  int j = i;
            rabbitTemplate.convertAndSend(CommonConstant.exchange_priority,CommonConstant.routingKey_priority,"这是一条测试数据"+i,message -> {
                //设置
                message.getMessageProperties().setPriority(j);
                return message;
            });
        }

        try {
            Thread.sleep(600*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testSendMessageTTL(){


        for (int i=0;i<20;i++){
            rabbitTemplate.convertAndSend(CommonConstant.exchange_direct,CommonConstant.routingKey_ttl,"这是一条测试数据"+i,message->{
                //设置过期时间时间
                message.getMessageProperties().setExpiration(String.valueOf(10000));
                return message;
            });
        }

        for (int i=0;i<20;i++){
            rabbitTemplate.convertAndSend(CommonConstant.exchange_direct,CommonConstant.routingKey_ttl,"这是一条测试数据"+i);
        }

        try {
            Thread.sleep(60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testSendMessageLazy(){

        for (int i=0;i<20;i++){
            rabbitTemplate.convertAndSend(CommonConstant.exchange_direct,CommonConstant.routingKey_direct1,"这是一条测试数据"+i);
        }

        for (int i=0;i<20;i++){
            rabbitTemplate.convertAndSend(CommonConstant.exchange_direct,CommonConstant.routingKey_direct2,"这是一条测试数据"+i);

        }

        try {
            Thread.sleep(60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }




    @Test
    public void testSendMessageASyncList(){

        for (int i=0;i<1000;i++){
            rabbitTemplate.convertAndSend(CommonConstant.exchange_direct,CommonConstant.routingKey_direct1,"这是一条测试数据"+i);
        }

        for (int i=0;i<1000;i++){
            rabbitTemplate.convertAndSend(CommonConstant.exchange_direct,CommonConstant.routingKey_direct2,"这是一条测试数据"+i);

        }

        try {
            Thread.sleep(60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Autowired
    private BatchingRabbitTemplate batchingRabbitTemplate;

    @Test
    public void testBatchSendMessage(){
        // 除了send(String exchange, String routingKey, Message message, CorrelationData correlationData)方法是发送单条数据
        // 其他send都是批量
        String msg;
        Message message;
        MessageProperties messageProperties=new MessageProperties();
        for(int i=0;i<1000;i++){
            msg="测试批量数据"+i;
            message=new Message(msg.getBytes(), messageProperties);
            batchingRabbitTemplate.send(CommonConstant.exchange_direct,CommonConstant.routingKey_direct2,message);
        }

        try {
            Thread.sleep(100*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发送数据完毕");
    }


    @Test
    public void testBatchSendMessageAck(){
        // 除了send(String exchange, String routingKey, Message message, CorrelationData correlationData)方法是发送单条数据
        // 其他send都是批量
        String msg;
        Message message;
        MessageProperties messageProperties=new MessageProperties();
        for(int i=0;i<1000;i++){
            msg="测试批量数据"+i;
            message=new Message(msg.getBytes(), messageProperties);
//            batchingRabbitTemplate.send(CommonConstant.exchange_direct,CommonConstant.routingKey_direct2,message);
            Message finalMessage = message;
            Boolean flag = batchingRabbitTemplate.invoke(operations -> {
                batchingRabbitTemplate.convertAndSend(CommonConstant.exchange_direct, CommonConstant.routingKey_direct2, finalMessage);
                log.info("发送message==============》");
                //同步阻塞
                return batchingRabbitTemplate.waitForConfirms(3 * 1000);
            });
            //同步ack确认
            if(flag){
                log.info("消息发送成功");
            }else {
                //消息消费失败 后续处理 重发(消费者需要进行幂等控制)或其它
                log.error("消息发送失败");
            }

        }

        System.out.println("发送数据完毕");
    }


    //定制
    @Test
    public void testSendMessageASyncCustomized(){

//
//        org.springframework.amqp.rabbit.connection.CorrelationData correlationData = new org.springframework.amqp.rabbit.connection.CorrelationData();
//        correlationData.setId("1");
//
//        rabbitTemplate.convertAndSend(CommonConstant.exchange_direct, CommonConstant.routingKey_direct2, "测试数据direct2",correlationData);



//
        //该invoke 有3种参数 第一个代表具体操作
        rabbitTemplate.invoke((operations -> {
            rabbitTemplate.convertAndSend(CommonConstant.exchange_direct, CommonConstant.routingKey_direct2, "测试数据direct2");
            return true;
        }),((long deliveryTag, boolean multiple)->{
            //处理 ack消息


        }),((long deliveryTag, boolean multiple)->{
            //处理非ack消息
        }));

    }

    @Autowired
    private RabbitAdmin rabbitAdmin;

    //删除queue
    @Test
    public void deleteQueue(){

        rabbitAdmin.deleteQueue(CommonConstant.queue_direct1);
        rabbitAdmin.deleteQueue(CommonConstant.queue_direct2);
        rabbitAdmin.deleteQueue(CommonConstant.queue_fanoutQueue1);
        rabbitAdmin.deleteQueue(CommonConstant.queue_fanoutQueue2);
        rabbitAdmin.deleteQueue(CommonConstant.queue_topic1);
        rabbitAdmin.deleteQueue(CommonConstant.queue_topic2);

    }


    //测试发送消息
    @Test
    public void testSendMessageBatchASync(){

        //convertAndSend 使用此方法，交换机会马上把所有的信息都交给所有的消费者，消费者再自行处理，不会因为消费者处理慢而阻塞线程。
        //convertSendAndReceive 使用此方法，当确认了所有的消费者都接收成功之后，才触发另一个convertSendAndReceive
        //发送direct类型数据
        log.info("开始发送！！！！");

        //发送direct
        rabbitTemplate.convertAndSend(CommonConstant.exchange_direct, CommonConstant.routingKey_direct1,"测试数据direct1");
        rabbitTemplate.convertAndSend(CommonConstant.exchange_direct, CommonConstant.routingKey_direct2, "测试数据direct2");
        //发送fanout类型数据
        rabbitTemplate.convertAndSend(CommonConstant.exchange_fanout, "", "测试数据fanout1");
        rabbitTemplate.convertAndSend(CommonConstant.exchange_fanout, "", "测试数据fanout2");
        //发送topic类型数据
        rabbitTemplate.convertAndSend(CommonConstant.exchange_topic, CommonConstant.routingKey_c_topic1, "测试数据topic1");
        rabbitTemplate.convertAndSend(CommonConstant.exchange_topic, CommonConstant.routingKey_c_topic2, "测试数据topic2");


    }

    //测试发送消息
    @Test
    public void testSendMessageBatchSync(){

        //convertAndSend 使用此方法，交换机会马上把所有的信息都交给所有的消费者，消费者再自行处理，不会因为消费者处理慢而阻塞线程。
        //convertSendAndReceive 使用此方法，当确认了所有的消费者都接收成功之后，才触发另一个convertSendAndReceive
        //发送direct类型数据
        log.info("开始发送！！！！");

        //批量确认
        Boolean result = rabbitTemplate.invoke(operations -> {

            //发送direct
            rabbitTemplate.convertAndSend(CommonConstant.exchange_direct, CommonConstant.routingKey_direct1,"测试数据direct1");
            rabbitTemplate.convertAndSend(CommonConstant.exchange_direct, CommonConstant.routingKey_direct2, "测试数据direct2");
            //发送fanout类型数据
            rabbitTemplate.convertAndSend(CommonConstant.exchange_fanout, "", "测试数据fanout1");
            rabbitTemplate.convertAndSend(CommonConstant.exchange_fanout, "", "测试数据fanout2");
            //发送topic类型数据
            rabbitTemplate.convertAndSend(CommonConstant.exchange_topic, CommonConstant.routingKey_c_topic1, "测试数据topic1");
            rabbitTemplate.convertAndSend(CommonConstant.exchange_topic, CommonConstant.routingKey_c_topic2, "测试数据topic2");

            return rabbitTemplate.waitForConfirms(3*1000);
        });

        //这个批次是否发送成功
        if(result){
            log.info("所有消息发送成功");
        }else {
            log.error("消息发送失败");
        }

    }

}
