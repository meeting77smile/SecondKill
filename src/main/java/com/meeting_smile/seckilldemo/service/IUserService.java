package com.meeting_smile.seckilldemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.valueobject.LoginVo;
import com.meeting_smile.seckilldemo.valueobject.RespBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2025-03-11
 */
public interface IUserService extends IService<User> {

    /**
     * 功能描述：登录
     *
     * @param loginVo
     * @param request
     * @param response
     * @return
     */

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户
     * @param userTicket
     * @return
     */
    User getUserByCookie(String userTicket,HttpServletRequest request,HttpServletResponse response);

    /**
     * 功能描述：更新密码
     * @param userTicket
     * @param password
     * @param request
     * @param response
     * @return
     */
    RespBean updatePassword(String userTicket,String password,HttpServletRequest request,HttpServletResponse response);
}
