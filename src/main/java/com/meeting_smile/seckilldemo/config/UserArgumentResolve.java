package com.meeting_smile.seckilldemo.config;

import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.service.IUserService;
import com.meeting_smile.seckilldemo.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

/**
 * 自定义用户参数
 * 功能描述：在每一层Controller之前都进行参数校验。避免每一个接口都需要校验用户参数，简化代码。
 */
@Component //因为要将该类的对象添加（注入）到WebConfig中
public class UserArgumentResolve implements HandlerMethodArgumentResolver {
    @Autowired
    private IUserService userService;

    //做一层条件的判断，返回true才会执行resolveArgument方法
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz== User.class;//判断参数是否为user类型
    }

    //代替了GoodsController中判断user是否合法的功能
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //通过webRequest拿到request和response
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        String ticket = CookieUtil.getCookieValue(request,"userTicket");
        if(StringUtils.isEmpty(ticket)){//如果ticket为空
            return null;
        }
        return userService.getUserByCookie(ticket,request,response);
    }
}
