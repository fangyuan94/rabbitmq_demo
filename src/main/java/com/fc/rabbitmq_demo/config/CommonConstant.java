package com.fc.rabbitmq_demo.config;

public interface CommonConstant {

    String exchange_direct = "exchange:com.fc.exchange.direct";

    String exchange_fanout = "exchange:com.fc.exchange.fanout";

    String exchange_topic = "exchange:com.fc.exchange.topic";

    String exchange_deadLetter_direct = "exchange:com.fc.exchange.deadLetter";


    String queue_direct1 = "queue:com.fc.queue.direct1";

    String queue_priority = "queue:com.fc.queue.priority";

    String exchange_priority = "exchange:com.fc.exchange.priority";


    String queue_direct2 = "queue:com.fc.queue.direct2";

    //死信队列
    String queue_deadLetter = "queue:com.fc.queue.deadLetter";
    String queue_ttl = "queue:com.fc.queue.ttl";



    String queue_fanoutQueue1 = "queue:com.fc.queue.fanoutQueue1";


    String queue_fanoutQueue2 = "queue:com.fc.queue.fanoutQueue2";


    String queue_topic1 = "queue:com.fc.queue.topic1";

    String queue_topic2 = "queue:com.fc.queue.topic2";

    String routingKey_deadLetter = "routingKey:com.fc.routingKey.deadLetter";

    String routingKey_priority = "routingKey:com.fc.routingKey.deadLetter";


    String routingKey_ttl = "routingKey:com.fc.routingKey.ttl";


    String routingKey_direct1 = "routingKey:com.fc.routingKey.direct1";

    String routingKey_direct2 = "routingKey:com.fc.routingKey.direct2";

    String routingKey_topic1 = "routingKey:com.fc.routingKey.topic1.#";

    String routingKey_topic2 = "routingKey:com.fc.routingKey.topic2.#";

    String routingKey_c_topic1 = "routingKey:com.fc.routingKey.topic1.1";


    String routingKey_c_topic2 = "routingKey:com.fc.routingKey.topic2.2";




}
