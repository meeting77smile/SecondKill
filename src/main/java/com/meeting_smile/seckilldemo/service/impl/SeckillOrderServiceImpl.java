package com.meeting_smile.seckilldemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting_smile.seckilldemo.mapper.SeckillOrderMapper;
import com.meeting_smile.seckilldemo.pojo.SeckillOrder;
import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2025-03-26
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 功能秒杀：获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId存在表示秒杀成功 ,-1：表示秒杀失败 ,0:表示排队中
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        //根据user_id与goods_id获取秒杀订单
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>().eq("user_id",user.getId())
                .eq("goods_id",goodsId));
        if(seckillOrder != null){//如果秒杀订单不为空，返回秒杀成功
            return seckillOrder.getOrderId();//返回秒杀订单id
        }else if (redisTemplate.hasKey("isStockEmpty:"+goodsId)){//商品库存为空，返回秒杀失败
            return -1L;
        }else{//返回正在排队中
            return 0L;
        }
    }
}
