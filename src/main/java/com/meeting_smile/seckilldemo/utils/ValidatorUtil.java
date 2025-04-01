package com.meeting_smile.seckilldemo.utils;

import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能描述：手机号码校验
 */
public class ValidatorUtil {
    //常用的校验方式：正则表达式
    //国内的手机号码验证（简单版本）：
    //第一部分：1表示手机号码只能以1开头
    //第二部分：[3-9]表示手机号码第二位只能是3-9之间的
    //第三部分：\\d{9}表示任意数字可以出现9次，也只能出现9次
    private static final Pattern mobile_pattern = Pattern.compile("[1]([3-9])[0-9]{9}$");
    public static boolean isMobile(String mobile){
        if(StringUtils.isEmpty(mobile)){//如果手机号为空
            return false;
        }
        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();//密码匹配
    }
}
