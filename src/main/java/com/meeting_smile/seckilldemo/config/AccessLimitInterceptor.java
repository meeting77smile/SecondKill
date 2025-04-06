package com.meeting_smile.seckilldemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.service.IUserService;
import com.meeting_smile.seckilldemo.utils.CookieUtil;
import com.meeting_smile.seckilldemo.valueobject.RespBean;
import com.meeting_smile.seckilldemo.valueobject.RespBeanEnum;
import io.lettuce.core.dynamic.output.OutputRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

/**
 * 用于实现 自定义接口限流/通用接口限流 的拦截器
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor{

    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){//如果handler是需要处理的方法
            User user = getUser(request,response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            //获取AccessLimit注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){//如果没有该注解
                return true;//跳过该拦截
            }
            //如果存在该注解，就获取其含有的信息
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin){//如果需要登录
                if(user == null){//如果用户不存在
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                //用户不为空
                key+=":"+user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count =(Integer) valueOperations.get(key);//用户规定时间内的访问次数
            if(count == null){//用户第一次访问
                //设置有效时间为second秒
                valueOperations.set(key,1,second, TimeUnit.SECONDS);
            }else if(count < maxCount){//未达到上限次数，则自增
                valueOperations.increment(key);
            }else{//在规定时间（second秒）超过上限次数
                render(response,RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        //没发生问题：
        return true;
    }

    /**
     * 功能描述：构建返回对象
     * @param response
     * @param respBeanEnum
     */
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");//返回json格式的返回数据
        response.setCharacterEncoding("UTF-8");//设置返回数据的编码格式
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        //JSON处理:
        //ObjectMapper：Jackson库的核心类，用于对象与JSON互相转换
        //writeValueAsString()：将Java对象序列化为JSON字符串
        out.write(new ObjectMapper().writeValueAsString(respBean));
        //IO流操作:
        //flush()和close()：刷新缓冲区并关闭资源
        out.flush();
        out.close();
    }

    /**
     * 功能描述：获取当前登录的用户
     * @param request
     * @param response
     * @return
     */
    private User getUser(HttpServletRequest request,HttpServletResponse response){
        String ticket = CookieUtil.getCookieValue(request,"userTicket");
        if(StringUtils.isEmpty(ticket)){//如果ticket为空
            return null;
        }
        return userService.getUserByCookie(ticket,request,response);
    }
}
