package com.meeting_smile.seckilldemo.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公共返回对象
 */

@Data   //lombok方法，减少getter和setter方法
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {
    private long code;
    private String message;
    private Object obj;

    /**
     * 功能描述：成功时的返回结果
     * @return
     */
    public static RespBean success(){
        //每个RespBeanEnum对象的SUCCESS()都是相同的，不用传入新的RespBeanEnum对象
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBeanEnum.SUCCESS.getMessage(),null);
    }

    /**
     * 功能描述：成功时的返回结果
     * @return
     */
    public static RespBean success(Object obj){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBeanEnum.SUCCESS.getMessage(),obj);
    }

    /**
     * 功能描述：失败时的返回结果
     * @param respBeanEnum
     * @return
     */
    //传入RespBeanEnum对象的原因：成功时code基本上都是200，而失败的原因有很多种
    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.getCode(),respBeanEnum.getMessage(),null);
    }

    /**
     * 功能描述：失败时的返回结果
     * @param respBeanEnum
     * @return
     */
    public static RespBean error(RespBeanEnum respBeanEnum,Object obj){
        return new RespBean(respBeanEnum.getCode(),respBeanEnum.getMessage(),obj);
    }
}
