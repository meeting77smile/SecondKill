package com.meeting_smile.seckilldemo.config;

import com.meeting_smile.seckilldemo.pojo.User;

/**
 * 在整个项目中，可以把当前登录的用户存在ThreadLocal<User>中
 * 这样就不需要从UserArgumentResolve中获取了
 * 原理：将当前的用户存在自己的线程中(ThreadLocal)，而不是公共线程中，否则会产生用户信息的紊乱
 * 而秒杀是高并发多线程的，故有多个线程可以使用
 * 相当于把用户信息作为每个线程的私有数据
 */
public class UserContext {

    private static ThreadLocal<User> userHolder = new ThreadLocal<User>();
    public static void setUser(User user){
        userHolder.set(user);
    }
    public static User getUser(){
        return userHolder.get();
    }
}
