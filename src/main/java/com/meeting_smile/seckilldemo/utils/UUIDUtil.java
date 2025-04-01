package com.meeting_smile.seckilldemo.utils;

import java.util.UUID;

/**
 * UUID工具类：用于生成UUID
 *
 * @author zhoubin
 * @since 1.0.0
 */
public class UUIDUtil {

   public static String uuid() {

      //去掉uuid生成时的横杠
      return UUID.randomUUID().toString().replace("-", "");
   }

}