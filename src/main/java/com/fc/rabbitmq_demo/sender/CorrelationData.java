package com.fc.rabbitmq_demo.sender;

import lombok.Data;

import java.io.Serializable;

//这里需要对CorrelationData进行序列化 后续说到消息转换器说到
//默认支持 string 实现了Serializable的java类 byte[]
@Data
public class CorrelationData extends org.springframework.amqp.rabbit.connection.CorrelationData implements Serializable {

    //消息体
    private volatile Object message;
    //交换机
    private String exchange;
    //路由键
    private String routingKey;
    //重试次数
    private int retryCount = 0;

}
