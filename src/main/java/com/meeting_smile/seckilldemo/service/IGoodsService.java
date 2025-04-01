package com.meeting_smile.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting_smile.seckilldemo.pojo.Goods;
import com.meeting_smile.seckilldemo.valueobject.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2025-03-26
 */
public interface IGoodsService extends IService<Goods> {

    /**
     * 功能描述：获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 功能描述：获取商品详情
     * @param goodsId
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
