package com.meeting_smile.seckilldemo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ配置类（headers模式）
 */
@Configuration
public class RabbitMQHeadersConfig {
    private static final String QUEUE01 = "queue_headers01";
    private static final String QUEUE02 = "queue_headers02";
    private static final String EXCHANGE = "headersExchange";

    @Bean
    public Queue queue01(){
        return new Queue(QUEUE01);
    }

    @Bean
    public Queue queue02(){
        return new Queue(QUEUE02);
    }

    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(EXCHANGE);
    }

    @Bean
    public Binding binding01(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("color","red");
        map.put("speed","low");
        //Any：只要匹配其中任意一个键值对即可满足
        return BindingBuilder.bind(queue01()).to(headersExchange()).whereAny(map).match();
    }

    @Bean
    public Binding binding02(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("color","red");
        map.put("speed","fast");
        //All：需要匹配所有键值对才可满足
        return BindingBuilder.bind(queue02()).to(headersExchange()).whereAll(map).match();
    }

}
