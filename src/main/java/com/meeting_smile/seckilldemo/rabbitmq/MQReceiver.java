package com.meeting_smile.seckilldemo.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.meeting_smile.seckilldemo.pojo.SeckillMessage;
import com.meeting_smile.seckilldemo.pojo.SeckillOrder;
import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.service.IGoodsService;
import com.meeting_smile.seckilldemo.service.IOrderService;
import com.meeting_smile.seckilldemo.valueobject.GoodsVo;
import com.meeting_smile.seckilldemo.valueobject.RespBean;
import com.meeting_smile.seckilldemo.valueobject.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息消费者（接收者）
 */
@Service
@Slf4j
public class MQReceiver {
//    @RabbitListener(queues = "queue")//监听名为"queue"的队列
//    public void receive(Object msg){
//        log.info("接收消息"+msg);
//    }
//
//    /**
//     * 测试：fanout模式
//     * @param msg
//     */
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg){
//        log.info("QUEUE01收到消息"+msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg){
//        log.info("QUEUE02收到消息"+msg);
//    }
//
//    /**
//     * 测试direct模式
//     * @param msg
//     */
//    @RabbitListener(queues = "queue_direct01")
//    public void receive03(Object msg){
//        log.info("QUEUE01收到消息"+msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive04(Object msg){
//        log.info("QUEUE02收到消息"+msg);
//    }
//
//    /**
//     * 测试topic模式
//     * @param msg
//     */
//    @RabbitListener(queues = "queue_topic01")
//    public void receive05(Object msg){
//        log.info("QUEUE01收到消息"+msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void receive06(Object msg){
//        log.info("QUEUE02收到消息"+msg);
//    }
//
//    /**
//     * 测试headers模式
//     * @param message
//     */
//    @RabbitListener(queues = "queue_headers01")
//    public void receive07(Message message){
//        log.info("QUEUE01收到Message对象"+message);
//        log.info("QUEUE01收到消息"+new String(message.getBody()));
//    }
//
//    @RabbitListener(queues = "queue_headers02")
//    public void receive08(Message message){
//        log.info("QUEUE02收到Message对象"+message);
//        log.info("QUEUE02收到消息"+new String(message.getBody()));
//    }

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

    /**
     * 功能描述：下单操作
     */
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收的消息："+message);

        //在监听者（消费者）的位置做了Controller层应该做的事情
        //因为利用RabbitMQ使其成为了异步操作，能快速返回请求，能进行流量区分的处理

        //将JSON对象转换为对象
        SeckillMessage seckillMessage = JSON.parseObject(message, SeckillMessage.class);
        //根据秒杀消息获得商品id与用户对象
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        //根据商品id拿到商品对象
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() <1) {//如果商品库存不足
            return;
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder != null){//若用户已抢购过该商品，即重复抢购
            return;
        }
        orderService.seckill(user,goodsVo);
    }
}
