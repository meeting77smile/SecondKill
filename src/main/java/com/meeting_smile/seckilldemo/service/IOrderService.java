package com.meeting_smile.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting_smile.seckilldemo.pojo.Order;
import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.valueobject.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2025-03-26
 */
public interface IOrderService extends IService<Order> {

    /**
     * 功能描述”秒杀
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVo goods);
}
