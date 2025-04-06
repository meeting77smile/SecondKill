package com.meeting_smile.seckilldemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting_smile.seckilldemo.exception.GlobalException;
import com.meeting_smile.seckilldemo.mapper.OrderMapper;
import com.meeting_smile.seckilldemo.pojo.Order;
import com.meeting_smile.seckilldemo.pojo.SeckillGoods;
import com.meeting_smile.seckilldemo.pojo.SeckillOrder;
import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.service.IGoodsService;
import com.meeting_smile.seckilldemo.service.IOrderService;
import com.meeting_smile.seckilldemo.service.ISeckillGoodsService;
import com.meeting_smile.seckilldemo.service.ISeckillOrderService;
import com.meeting_smile.seckilldemo.utils.MD5Util;
import com.meeting_smile.seckilldemo.utils.UUIDUtil;
import com.meeting_smile.seckilldemo.valueobject.GoodsVo;
import com.meeting_smile.seckilldemo.valueobject.OrderDetailVo;
import com.meeting_smile.seckilldemo.valueobject.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2025-03-26
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 功能描述：秒杀
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().
                eq("goods_id",goods.getId()));//通过商品id获取当前秒杀的商品
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);//商品库存-1
        //使用 MyBatis-Plus 框架执行的一个数据库更新操作
        //核心作用：安全地更新秒杀商品的库存数量，同时满足以下条件：
        //只更新 id = 指定ID 的商品
        //仅当当前 stock_count > 0（库存大于0）时才执行扣库存数的操作
        //将库存设置为 seckillGoods.getStockCount() 的新值
        //boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().set("stock_count", seckillGoods.getStockCount()).eq("id",seckillGoods.getId()).gt("stock_count",0));//更新商品信息
        boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count-1")
                .eq("goods_id",goods.getId()).gt("stock_count",0));
        if(seckillGoods.getStockCount() < 1) {//库存为空
            //标记id为goods.getId()的商品的库存为空
            valueOperations.set("isStockEmpty:"+goods.getId(),"0");//供SeckillOrderServiceImpl的getResult方法判断
            return null;
        }
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);//插入订单
        //生成秒杀订单（其中的goodsId属性值与订单表相关联）
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        valueOperations.set("order:"+user.getId()+":"+goods.getId(),seckillOrder);
        return order;
    }

    /**
     * 功能描述：获取订单详情
     * @param orderId
     * @return
     */

    @Override
    public OrderDetailVo getDetail(Long orderId) {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIT);
        }
        Order order = baseMapper.selectById(orderId);
        Long goodsId = order.getGoodsId();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        OrderDetailVo detail = new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        return detail;
    }

    /**
     * 功能描述：获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(User user, Long goodsId) {
        //利用UUID生成随机的秒杀地址，并利用MD5进行加密
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        //将生成的秒杀接口地址放在redis中便于校验，设置60秒之后失效
        //秒杀地址不放在数据库中：地址是临时的，一段时间后会失效，并不长久
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,str,60, TimeUnit.SECONDS);
        return str;
    }

    /**
     * 功能描述：校验秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public Boolean checkPath(User user, Long goodsId,String path) {
        if(user == null || goodsId<0 || StringUtils.isEmpty(path)){//健壮性判断
            return false;

        }
        //从redis中获取先前存入的路径
        String  redisPath = (String) redisTemplate.opsForValue().get("seckillPath:"+user.getId()+":"+goodsId);

        return path.equals(redisPath);//判断路径path是否属于该user与该goodsId的路径
    }

    /**
     * 功能描述：验证码校验
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public Boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(StringUtils.isEmpty(captcha) || user == null || goodsId < 0) {//健壮性判断
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:"+user.getId()+":"+goodsId);
        return captcha.equals(redisCaptcha);
    }
}
