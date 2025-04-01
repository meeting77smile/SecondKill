package com.meeting_smile.seckilldemo.valueobject;

import com.meeting_smile.seckilldemo.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品返回对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo extends Goods{//为了继承Goods的属性
    private BigDecimal seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
