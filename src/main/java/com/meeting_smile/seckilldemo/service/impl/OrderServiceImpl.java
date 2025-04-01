package com.meeting_smile.seckilldemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting_smile.seckilldemo.mapper.OrderMapper;
import com.meeting_smile.seckilldemo.pojo.Order;
import com.meeting_smile.seckilldemo.pojo.SeckillGoods;
import com.meeting_smile.seckilldemo.pojo.SeckillOrder;
import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.service.IOrderService;
import com.meeting_smile.seckilldemo.service.ISeckillGoodsService;
import com.meeting_smile.seckilldemo.service.ISeckillOrderService;
import com.meeting_smile.seckilldemo.valueobject.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    @Autowired ISeckillOrderService seckillOrderService;
    /**
     * 功能描述：秒杀
     * @param user
     * @param goods
     * @return
     */
    @Override
    public Order seckill(User user, GoodsVo goods) {
        //秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().
                eq("goods_id",goods.getId()));//通过商品id获取当前秒杀的商品
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);//商品库存-1
        seckillGoodsService.updateById(seckillGoods);//更新商品信息
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
        return order;
    }
}
