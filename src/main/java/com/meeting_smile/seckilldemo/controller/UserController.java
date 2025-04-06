package com.meeting_smile.seckilldemo.controller;


import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.rabbitmq.MQSender;
import com.meeting_smile.seckilldemo.valueobject.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 2025-03-11
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    MQSender mqSender;

    /**
     * 功能描述：用户信息（专门用于测试）
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }

//    /**
//     *功能描述：测试发送RabbitMQ消息
//     */
//    @RequestMapping("mq")
//    @ResponseBody
//    public void mq(){
//        mqSender.send("Hello");
//    }
//
//    /**
//     * 功能描述：测试fanout模式
//     */
//    @RequestMapping("mq/fanout")
//    @ResponseBody
//    public void mq01(){
//        mqSender.send("Hello");
//    }
//
//    /**
//     * 功能描述：测试direct模式
//     */
//    @RequestMapping("mq/direct01")
//    @ResponseBody
//    public void mq02(){
//        mqSender.send01("Hello,red!");//注意别用成测试fanout的send()方法
//    }
//
//    @RequestMapping("mq/direct02")
//    @ResponseBody
//    public void mq03(){
//        mqSender.send02("Hello,green!");
//    }
//
//    /**
//     * 功能描述：测试topic模式
//     */
//    @RequestMapping("mq/topic01")
//    @ResponseBody
//    public void mq04(){
//        mqSender.send03("Hello,Red!");
//    }
//
//    @RequestMapping("mq/topic02")
//    @ResponseBody
//    public void mq05(){
//        mqSender.send04("Hello,Green!");
//    }
//
//    /**
//     * 功能描述：测试headers模式
//     */
//    @RequestMapping("mq/headers01")
//    @ResponseBody
//    public void mq06(){
//        mqSender.send05("Hello,Header01");
//    }
//
//    @RequestMapping("mq/headers02")
//    @ResponseBody
//    public void mq07(){
//        mqSender.send06("Hello,Header02");
//    }
}
