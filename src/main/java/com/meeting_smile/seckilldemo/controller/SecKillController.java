package com.meeting_smile.seckilldemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.meeting_smile.seckilldemo.pojo.Goods;
import com.meeting_smile.seckilldemo.pojo.Order;
import com.meeting_smile.seckilldemo.pojo.SeckillOrder;
import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.service.IGoodsService;
import com.meeting_smile.seckilldemo.service.IOrderService;
import com.meeting_smile.seckilldemo.service.ISeckillOrderService;
import com.meeting_smile.seckilldemo.valueobject.GoodsVo;
import com.meeting_smile.seckilldemo.valueobject.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 秒杀
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;

    /**
     * 功能描述：秒杀
     * windows优化前：QPS 1379.1835233541742
     * Linux优化前：QPS
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill")
    public String dpSeckill(Model model, User user,Long goodsId){
        if(user == null){//当用户不存在
            return "login";
        }
        model.addAttribute("user",user);
        //根据商品id拿到库存
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goods.getStockCount() < 1){//如果没有库存了
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        //判断是否重复抢购
        //若在秒杀商品表中根据当前用户id能查到商品id，说明用户已经抢购过商品
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",
                user.getId()).eq("goods_id",goodsId));
        if(seckillOrder != null){//若用户已抢购过该商品
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        //用户能够抢购该商品
        Order order = orderService.seckill(user,goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);
        return  "orderDetail";
    }
}
