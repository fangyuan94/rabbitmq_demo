package com.fc.rabbitmq_demo.config;

import com.fc.rabbitmq_demo.sender.CorrelationData;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

public class MessageConverterTest {

    public static void main(String[] args) {
//        jackson2JsonMessageConverterTest();

//        simpleMessageConverterTest();

        serializerMessageConverterTest();

    }

    private static void serializerMessageConverterTest() {
        long startTime = System.currentTimeMillis();

        for (int i=0;i<1000;i++){

            CorrelationData correlationData = new CorrelationData();
            correlationData.setId("1"+i);
            correlationData.setMessage("测试数据direct1"+i);
            correlationData.setExchange(CommonConstant.exchange_direct);
            correlationData.setRoutingKey(CommonConstant.routingKey_direct1);

            correlationData.setRetryCount(i);

            SerializerMessageConverter serializerMessageConverter = new SerializerMessageConverter();

            Message message = serializerMessageConverter.toMessage(correlationData,null );

            Object o = serializerMessageConverter.fromMessage(message);
//            System.out.println(o);
        }

        long endTime = System.currentTimeMillis();

        System.out.println(endTime-startTime);

    }

    private static void simpleMessageConverterTest() {
        long startTime = System.currentTimeMillis();

        for (int i=0;i<1000;i++){

            CorrelationData correlationData = new CorrelationData();
            correlationData.setId("1"+i);
            correlationData.setMessage("测试数据direct1"+i);
            correlationData.setExchange(CommonConstant.exchange_direct);
            correlationData.setRoutingKey(CommonConstant.routingKey_direct1);

            correlationData.setRetryCount(i);

            SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();

            Message message = simpleMessageConverter.toMessage(correlationData,null );

            Object o = simpleMessageConverter.fromMessage(message);
//            System.out.println(o);
        }

        long endTime = System.currentTimeMillis();

        System.out.println(endTime-startTime);

    }

    public static void jackson2JsonMessageConverterTest(){

        long startTime = System.currentTimeMillis();


        for (int i=0;i<1000;i++){

            CorrelationData correlationData = new CorrelationData();
            correlationData.setId("1"+i);
            correlationData.setMessage("测试数据direct1"+i);
            correlationData.setExchange(CommonConstant.exchange_direct);
            correlationData.setRoutingKey(CommonConstant.routingKey_direct1);

            correlationData.setRetryCount(i);

            Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();

            Message message = jackson2JsonMessageConverter.toMessage(correlationData,null );

            Object o = jackson2JsonMessageConverter.fromMessage(message);
//            System.out.println(o);
        }

        long endTime = System.currentTimeMillis();

        System.out.println(endTime-startTime);

    }
}
