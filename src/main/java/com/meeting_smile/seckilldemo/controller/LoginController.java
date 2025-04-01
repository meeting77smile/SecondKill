package com.meeting_smile.seckilldemo.controller;
import com.meeting_smile.seckilldemo.service.IUserService;
import com.meeting_smile.seckilldemo.valueobject.LoginVo;
import com.meeting_smile.seckilldemo.valueobject.RespBean;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * 登录
 */
@Slf4j
@Controller //不能用RestController（会默认给下面所有的方法加上ResponseBody），否则打开网页时无法实现页面跳转，而是显示返回的对象（将返回值作为响应数据）
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private IUserService userService;

    /**
     * 功能描述：跳转至登录页面
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    /**
     * 功能描述：登录校验功能
     * @param loginVo
     * @return
     */
    @RequestMapping("/doLogin")
    @ResponseBody   //因为要返回RespeBean
    //要使得validation组件生效，需要在参数之前加上@Valid注解
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        log.info("{}",loginVo);
        return userService.doLogin(loginVo,request,response);
    }
}
