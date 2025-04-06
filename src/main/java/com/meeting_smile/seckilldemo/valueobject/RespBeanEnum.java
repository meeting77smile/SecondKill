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
    MOBILE_NOT_EXIT(500213,"手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500214,"密码更新失败"),
    SESSION_ERROR(500215,"用户不存在" ),
    //秒杀模块5005xx
    EMPTY_STOCK(500500,"库存不足"),
    REPEATE_ERROR(500501,"该商品每日限购一件"),
    REQUEST_ILLEGAL(500502,"请求非法，请重新尝试"),
    ERROR_CHAPTCHA(500503,"验证码错误，请重新输入"),
    ACCESS_LIMIT_REACHED(500504,"访问过于频繁，请稍后再试"),
    //订单模块5003xx
    ORDER_NOT_EXIT(500300, "订单信息不存在");
    private final Integer code;
    private final String message;

//    RespBeanEnum(Integer code, String message) {
//        this.code = code;
//        this.message = message;
//    }
//
//    public Integer getCode() {
//        return code;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//    @Override
//    public String toString() {
//        return "RespBeanEnum{" +
//                "code=" + code +
//                ", message='" + message + '\'' +
//                '}';
//    }
}
