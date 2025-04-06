package com.meeting_smile.seckilldemo.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息发送者
 */
@Service
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;//发消息用的模板
//
//    //测试fanout模式
//    public void send(Object msg){
//        log.info("发送消息："+msg);
//        rabbitTemplate.convertAndSend("fanoutExchange","",msg);//发送消息
//    }
//
//    //测试direct模式
//    public void send01(Object msg){
//        log.info("发送red消息："+msg);
//        rabbitTemplate.convertAndSend("directExchange","queue.red",msg);
//    }
//
//    public void send02(Object msg){
//        log.info("发送green消息："+msg);
//        rabbitTemplate.convertAndSend("directExchange","queue.green",msg);
//    }
//
//    //测试topic模式：
//    public void send03(Object msg){
//        log.info("发送消息(QUEUE01接收）："+msg);
//        rabbitTemplate.convertAndSend("topicExchange","a.b.queue.red",msg);
//    }
//    public void send04(Object msg){
//        log.info("发送消息(QUEUE01与QUEUE02共同接收）："+msg);
//        rabbitTemplate.convertAndSend("topicExchange","a.queue.red",msg);
//    }
//
//    //测试headers模式：
//    public void send05(String msg){
//        log.info("发送消息(QUEUE01和QUEUE02同时接收）："+msg);
//        MessageProperties Properties = new MessageProperties();
//        Properties.setHeader("color","red");
//        Properties.setHeader("speed","fast");
//        Message message = new Message(msg.getBytes(),Properties);
//        rabbitTemplate.convertAndSend("headersExchange","",message);
//    }
//
//    public void send06(String msg){
//        log.info("发送消息(QUEUE01接收）："+msg);
//        MessageProperties Properties = new MessageProperties();
//        Properties.setHeader("color","red");
//        Properties.setHeader("speed","normal");//防止QUEUE02匹配
//        Message message = new Message(msg.getBytes(),Properties);
//        rabbitTemplate.convertAndSend("headersExchange","",message);
//    }
    /**
     * 功能描述：发送秒杀信息
     */
    public void sendSeckillMessage(String message){
        log.info("发送消息："+message);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message",message);
    }
}
