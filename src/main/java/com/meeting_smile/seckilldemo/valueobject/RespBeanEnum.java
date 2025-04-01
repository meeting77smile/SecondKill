package com.meeting_smile.seckilldemo.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 枚举公共返回对象
 */
@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    //通用模块
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),
    //登录模块5002xx
    LOGIN_ERROR(500210,"用户名或密码错误"),
    MOBILE_ERROR(500211,"电话号码格式错误"),
    BIND_ERROR(500212,"参数校验异常"),
    //秒杀模块5005xx
    EMPTY_STOCK(500500,"库存不足"),
    REPEATE_ERROR(500501,"该商品每日限购一件");
    private final Integer code;
    private final String message;
}
