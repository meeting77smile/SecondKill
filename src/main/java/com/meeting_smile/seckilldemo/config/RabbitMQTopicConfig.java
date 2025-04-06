package com.meeting_smile.seckilldemo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类（Topic模式）
 * 与direct模式类似，区别就是可以使用模糊匹配
 */
@Configuration
public class RabbitMQTopicConfig {
//    private static final String QUEUE01 = "queue_topic01";
//    private static final String QUEUE02 = "queue_topic02";
//    private static final String EXCHANGE = "topicExchange";
//    private static final String ROUNTINGKEY01 = "#.queue.#";// #：能匹配0个或多个单词
//    private static final String ROUNTINGKEY02 = "*.queue.#";// *：只能匹配一个单词
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
//    public TopicExchange topicctExchange(){
//        return new TopicExchange(EXCHANGE);
//    }
//
//    @Bean
//    public Binding binding01(){
//        //交换机directExchange可根据路由键ROUNTINGKEY01连接至队列queue01
//        return BindingBuilder.bind(queue01()).to(topicctExchange()).with(ROUNTINGKEY01);
//    }
//
//    @Bean
//    public Binding binding02(){
//        //交换机directExchange可根据路由键ROUNTINGKEY02连接至队列queue02
//        return BindingBuilder.bind(queue02()).to(topicctExchange()).with(ROUNTINGKEY02);
//    }
    //选用Topic模式的原因：能实现其他模式的大部分功能
    private static final String QUEUE = "seckillQueue";
    private static final String EXCHANGE = "seckillExchange";

    @Bean
    Queue queue(){
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(topicExchange()).with("seckill.#");
    }
}
