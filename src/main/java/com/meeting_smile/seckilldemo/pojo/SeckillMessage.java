package com.meeting_smile.seckilldemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.ref.PhantomReference;

/**
 * 秒杀信息
 */
@Data //提供类的get、set、equals、hashCode、canEqual、toString方法
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {
    private User user;
    private Long goodsId;
}
