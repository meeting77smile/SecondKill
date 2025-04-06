package com.meeting_smile.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting_smile.seckilldemo.pojo.SeckillOrder;
import com.meeting_smile.seckilldemo.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2025-03-26
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 功能秒杀：获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId存在表示秒杀成功 ,-1：表示秒杀失败 ,0:表示排队中
     */
    Long getResult(User user, Long goodsId);
}
