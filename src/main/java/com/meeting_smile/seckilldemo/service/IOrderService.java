package com.meeting_smile.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting_smile.seckilldemo.pojo.Order;
import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.valueobject.GoodsVo;
import com.meeting_smile.seckilldemo.valueobject.OrderDetailVo;

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
     * 功能描述:秒杀
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVo goods);

    /**
     * 功能描述：获取订单详情
     * @param orderId
     * @return
     */
    OrderDetailVo getDetail(Long orderId);

    /**
     * 功能描述：获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    String createPath(User user, Long goodsId);

    /**
     * 功能描述：校验秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    Boolean checkPath(User user, Long goodsId,String path);

    /**
     * 功能描述：验证码校验
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    Boolean checkCaptcha(User user, Long goodsId, String captcha);
}
