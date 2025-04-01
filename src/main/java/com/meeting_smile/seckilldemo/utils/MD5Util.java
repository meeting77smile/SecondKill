package com.meeting_smile.seckilldemo.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

/**
 * MD5工具类
 */
@Component
public class MD5Util {
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt="1a2b3c4d";//盐,用于加密

    //第一次加密：将前端输入的明文密码转成后端接收的密码（防止明文密码在网络上传输）
    public static String inputPassToFormPass(String inputPass){
        //取部分的盐用来加密，便于混淆视听，提高了安全性
        String str = ""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    //第二次加密:将后端的密码再次加密后才放入数据库中（避免数据库被盗取）
    public static String formPassToDBPass(String fromPass,String salt){
        String str = salt.charAt(0)+salt.charAt(2)+fromPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    //真正会调用到的方法(将两次加密合并在一起)
    public static String inputPassToDBPass(String inputPass,String salt){
        //第一次加密
       String fromPass = inputPassToFormPass(inputPass);
        //第二次加密
       String dbPass = formPassToDBPass(fromPass,salt);
       return dbPass;
    }

    //测试
    public static void main(String[] args) {
        //结果：d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println("第一次加密结果: "+inputPassToFormPass("123456"));

        //二次加密结果：6e0a7fe692684372437c9e508508990d
        System.out.println("第二次加密结果: "+formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c4d"));

        //调用实际上会用到的方法
        System.out.println("调用实际上会用到的方法: "+inputPassToDBPass("123456","1a2b3c4d"));
    }
}
