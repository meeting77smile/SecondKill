package com.meeting_smile.seckilldemo.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhoubin
 * @since 2025-03-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID,手机密码
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * MD5加密(MD5(password明文+固定salt)+salt)
     */
    private String password;

    /**
     * 盐(是指在存储用户密码的哈希值之前，向原始密码添加一个随机生成的字符串)
     */
    private String salt;

    /**
     * 头像
     */
    private String head;

    /**
     * 注册时间
     */
    private Date registerDate;

    /**
     * 最后一次登录时间
     */
    private Date lastLoginDate;

    /**
     * 登录次数
     */
    private Integer loginCount;


}
