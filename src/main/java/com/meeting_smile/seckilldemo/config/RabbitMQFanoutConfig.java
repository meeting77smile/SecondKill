//package com.meeting_smile.seckilldemo.config;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.FanoutExchange;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.amqp.core.Queue;
//
///**
// * RabbitMQ配置类（fanout模式）
// * 广播模式：即发送一条消息能够被指定的多个队列受到
// */
//@Configuration
//public class RabbitMQFanoutConfig {
//
//    private static final String QUEUE01 = "queue_fanout01";
//    private static final String QUEUE02 = "queue_fanout02";
//    private static final String EXCHANGE = "fanoutExchange";
//
//
//    //准备一个队列：所有消息最终都会经过队列
//    @Bean
//    public Queue queue(){
//        return new Queue("queue",true);//将队列配置为持久化（队列与消息均设置为持久化，就可以实现持久化）
//    }
//
//    @Bean
//    public Queue queue01(){
//        return new Queue(QUEUE01);
//    }
//
//    @Bean
//    public Queue queue02(){
//        return new Queue(QUEUE02);
//    }
//
//    @Bean
//    public FanoutExchange fanoutExchange(){
//        return new FanoutExchange(EXCHANGE);
//    }
//
//    //将队列绑定到交换机
//    @Bean
//    public Binding bind01(){
//        return BindingBuilder.bind(queue01()).to(fanoutExchange());
//    }
//
//    @Bean
//    public Binding bind02(){
//        return BindingBuilder.bind(queue02()).to(fanoutExchange());
//    }
//}
