package com.example.demo.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

//    public static final String QUEUE_NAME = "javaboy_delay_queue";
//    public static final String EXCHANGE_NAME = "javaboy_delay_exchange";
//    public static final String EXCHANGE_TYPE = "x-delayed-message";

//    @Bean
//    Queue queue() {
//        return new Queue(QUEUE_NAME, true, false, false);
//    }
//
//    @Bean
//    CustomExchange customExchange() {
//        Map<String, Object> args = new HashMap<>();
//        args.put("x-delayed-type", "direct");
//        return new CustomExchange(EXCHANGE_NAME, EXCHANGE_TYPE, true, false,args);
//    }
//
//    @Bean
//    Binding binding() {
//        return BindingBuilder.bind(queue())
//                .to(customExchange()).with(QUEUE_NAME).noargs();
//    }

    @Bean
    public Exchange delayExchange() {
        Map<String, Object> args = new HashMap<>(2);
        //  x-delayed-type    声明 延迟队列Exchange的类型
        args.put("x-delayed-type", "direct");
        // 设置名字 交换机类型 持久化 不自动删除
        return new CustomExchange("emailExchange", "x-delayed-message", true, false, args);
    }

    @Bean
    public Queue delayQueue() {
        // 创建一个队列，名为test-delayed-queue
        return QueueBuilder.durable("test-delayed-queue").build();
    }

    /**
     * 将延迟队列通过routingKey绑定到延迟交换器
     *
     * @return
     */
    @Bean
    public Binding delayQueueBindExchange(Exchange delayExchange, Queue delayQueue) {
        // 这一步是将交换机与队列绑定，然后设置key
        return BindingBuilder.bind(delayQueue).to(delayExchange).with("emailKey").noargs();
    }

}
