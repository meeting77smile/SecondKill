package com.meeting_smile.seckilldemo.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 添加接口限流的注解（利用计数器算法）
 * second秒内最多只能操作maxCount次，且是否需要登录才能操作
 */
@Retention(RetentionPolicy.RUNTIME) //设置为运行时起作用的注解
@Target(ElementType.METHOD) //设置为作用于方法上的注解
public @interface AccessLimit {

    int second();
    int maxCount();
    boolean needLogin() default true;
}

