package com.meeting_smile.seckilldemo.controller;


import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.valueobject.RespBean;
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
}
