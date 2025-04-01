package com.meeting_smile.seckilldemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meeting_smile.seckilldemo.pojo.Goods;
import com.meeting_smile.seckilldemo.valueobject.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhoubin
 * @since 2025-03-26
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 功能描述：获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
