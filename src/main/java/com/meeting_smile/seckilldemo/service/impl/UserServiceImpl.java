package com.meeting_smile.seckilldemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.meeting_smile.seckilldemo.exception.GlobalException;
import com.meeting_smile.seckilldemo.exception.GlobalException;
import com.meeting_smile.seckilldemo.mapper.UserMapper;
import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.service.IUserService;
import com.meeting_smile.seckilldemo.utils.CookieUtil;
import com.meeting_smile.seckilldemo.utils.MD5Util;
import com.meeting_smile.seckilldemo.utils.UUIDUtil;
import com.meeting_smile.seckilldemo.valueobject.LoginVo;
import com.meeting_smile.seckilldemo.valueobject.RespBean;
import com.meeting_smile.seckilldemo.valueobject.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.util.StringUtils;
//javax与jakarta的区别：javax是旧版本的命名空间，jakarta是新版本的

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2025-03-11
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 功能描述：登录
     *
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        //实现登录逻辑
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //参数校验
//        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){//如果电话号码或密码为空
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        if(!ValidatorUtils.isMobile(mobile)){//电话号码格式不正确
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
        //电话号码与密码均没问题,接下来进入数据库查询
        //根据手机号(id)获取用户
        User user = userMapper.selectById(mobile);
        if(user == null){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //存在该手机号的用户，接下来校验其密码正误(password是在前端加密一次之后的密码)
        //将两次加密后的密码与数据库中的密码(user.getPassword())进行比较
        //如果密码不正确
        if(!MD5Util.formPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成cookie
        String ticket = UUIDUtil.uuid();
        //将信息放在redis中
        redisTemplate.opsForValue().set("user:"+ticket,user);//opsForValue:专门操作String的数据类型
        //request.getSession().setAttribute(ticket,user);//将用户对象存到session中
        CookieUtil.setCookie(request,response,"userTicket",ticket);
        return RespBean.success(ticket);
    }

    /**
     * 根据cookie获取用户
     * @param userTicket
     * @return
     */
    @Override
    public User getUserByCookie(String userTicket,HttpServletRequest request,HttpServletResponse response) {
        if(StringUtils.isEmpty(userTicket)){//userTicket为空
            return null;
        }
        //根据key（ticket—）取获取value（user）
        User user = (User) redisTemplate.opsForValue().get("user:"+userTicket);
        if(user != null){
            //用户不用为空，重新设置一下Cookie以防万一
            CookieUtil.setCookie(request,response,"userTicket",userTicket);
        }
        return user;
    }
}
