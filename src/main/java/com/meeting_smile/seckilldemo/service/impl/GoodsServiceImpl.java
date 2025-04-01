package com.meeting_smile.seckilldemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting_smile.seckilldemo.mapper.GoodsMapper;
import com.meeting_smile.seckilldemo.pojo.Goods;
import com.meeting_smile.seckilldemo.service.IGoodsService;
import com.meeting_smile.seckilldemo.valueobject.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2025-03-26
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    /**
     * 功能描述：获取商品列表
     * @return
     */
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    /**
     * 功能描述：获取商品详情
     * @param goodsId
     * @return
     */
    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}
