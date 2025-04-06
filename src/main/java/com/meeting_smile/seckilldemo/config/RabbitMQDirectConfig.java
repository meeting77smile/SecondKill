//package com.meeting_smile.seckilldemo.config;
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.DirectExchange;
//import org.springframework.amqp.core.Queue;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * RabbitMQ配置类（direct模式）
// */
//@Configuration
//public class RabbitMQDirectConfig {
//    private static final String QUEUE01 = "queue_direct01";
//    private static final String QUEUE02 = "queue_direct02";
//    private static final String EXCHANGE = "directExchange";
//    private static final String ROUNTINGKEY01 = "queue.red";
//    private static final String ROUNTINGKEY02 = "queue.green";
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
//    public DirectExchange directExchange(){
//        return new DirectExchange(EXCHANGE);
//    }
//
//    @Bean
//    public Binding binding01(){
//        //交换机directExchange可根据路由键ROUNTINGKEY01连接至队列queue01
//        return BindingBuilder.bind(queue01()).to(directExchange()).with(ROUNTINGKEY01);
//    }
//
//    @Bean
//    public Binding binding02(){
//        //交换机directExchange可根据路由键ROUNTINGKEY02连接至队列queue02
//        return BindingBuilder.bind(queue02()).to(directExchange()).with(ROUNTINGKEY02);
//    }
//}
