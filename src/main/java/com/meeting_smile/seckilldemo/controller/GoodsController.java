package com.meeting_smile.seckilldemo.controller;

import com.meeting_smile.seckilldemo.pojo.Goods;
import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.service.IGoodsService;
import com.meeting_smile.seckilldemo.service.IUserService;
import com.meeting_smile.seckilldemo.valueobject.DetailVo;
import com.meeting_smile.seckilldemo.valueobject.GoodsVo;
import com.meeting_smile.seckilldemo.valueobject.RespBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.util.DateUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.awt.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述：跳转到商品列表界面
 * windows优化前：QPS 5063.974882684583
 *        缓存后：QPS 5959.475566150179
 *
 * linux优化前：QPS 1483.620826080076
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IUserService userService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;//引入redis（优化qps：可对数据库返回读取的内容做一个缓存，如将页面缓存到redis中）

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;//用于手动渲染页面

    //解决分布式session的登录问题
    //方案一：将用户信息存入session：
/*    @RequestMapping("/toList")
    public String toList(HttpSession session, Model model, @CookieValue("userTicket") String ticket){
        //session：用于获取cookie以及用户信息
        //model：跳转到商品页面时可能需要展示的信息
        //ticket:cookie的值

        //判断该用户是否存在，如果不存在则跳转至登录界面
        if(StringUtils.isEmpty(ticket)){
            //如果cookie为空，跳转至登录页面
            return "login";
        }
        User user = (User)session.getAttribute(ticket);//从session中获取用户
        if(user == null){//如果用户不存在
            return  "login";
        }
        //用户存在：
        model.addAttribute("user",user);//model可将用户对象传到前端中(前端可通过键值"user"拿到user对象)
        return "goodsList";
    }*/

    //方案二：直接将用户信息存入redis：

    //使用redis解决的问题：
    //原本是把用户信息存在服务器中，但是如果用到多个服务器（Nginx使用默认负载均衡策略，请求将按照时间顺序逐一分发到后端上）
    //则用户可能一开始在服务器Tomcat1登录之后，用户信息会存在Tomcat里；过了一会再登录时请求又被Nginx分发到Tomcat2上，而此时Tomcat2上Session里还没有用户信息，于是又需要重新登陆
    //因此将用户信息统一存在redis便可解决这一问题

    //手动判断user是否合法
/*    @RequestMapping("/toList")
    public String toList(HttpServletRequest request, HttpServletResponse response,Model model, @CookieValue("userTicket") String ticket){
        //session：用于获取cookie以及用户信息
        //model：跳转到商品页面时可能需要展示的信息
        //ticket:cookie的值

*//*        //判断该用户是否存在，如果不存在则跳转至登录界面
        if(StringUtils.isEmpty(ticket)){
            //如果cookie为空，跳转至登录页面
            return "login";
        }
        User user = userService.getUserByCookie(ticket,request,response);
        if(user == null){//如果用户不存在
            return  "login";
        }*//*
        //用户存在：
        model.addAttribute("user",user);//model可将用户对象传到前端中(前端可通过键值"user"拿到user对象)
        return "goodsList";
    }*/

    //利用WebConfig和UserArgumentResolve来判断user是否合法
    @RequestMapping(value = "/toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model,User user,
                         HttpServletRequest request,HttpServletResponse response){
        if(user == null){//如果用户不存在
            return  "login";//返回到登录页面
        }
        //用户存在：

        //html缓存：
        //从Redis中获取页面，若不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        //若页面不为空，则直接返回一个html页面
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("user",user);//model可将用户对象传到前端中(前端可通过键值"user"拿到user对象)
        model.addAttribute("goodsList",goodsService.findGoodsVo());
        //return "goodsList";
        //若页面为空，手动渲染页面，存入Redis中并返回

        //新版WebContext创建对象的方法：
        // 获取 JakartaServletWebApplication 实例（通常通过 Spring 注入）
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(request.getServletContext());
        // 创建 IWebExchange
        IWebExchange webExchange = application.buildExchange(request, response);
        WebContext context = new WebContext(
                webExchange,          // IWebExchange 对象
                request.getLocale(),  // Locale
                model.asMap()         // 模板变量 Map
        );

        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",context);
        if(!StringUtils.isEmpty(html)){//如果html不为空，说明渲染成功
            valueOperations.set("goodsList",html,60, TimeUnit.SECONDS);//设置页面的失效时间为一分钟
            //即一分钟之后将会把"goodsList"从Redis中删除
        }
        return html;
    }

    /**
     * 功能描述：跳转商品详情页（弃用）
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/toDetail2/{goodsId}",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(Model model,User user, @PathVariable Long goodsId,HttpServletRequest request,HttpServletResponse response){
        //url缓存
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String)valueOperations.get("goodsDetail"+goodsId);
        if(!StringUtils.isEmpty(html)){//如果页面不为空，直接返回页面
            return html;
        }
        model.addAttribute("user",user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus = 0;//秒杀状态
        int remainSeconds = 0;//秒杀倒计时
        //秒杀还未开始
        if(nowDate.before(startDate)){//当前时间在开始时间之前:秒杀未开始
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime())/1000);
        }else if(nowDate.after(endDate)){//当前时间在结束时间之后:秒杀已结束
        //秒杀已结束
            secKillStatus = 2;
            remainSeconds = -1;//表示秒杀已结束
        }else{
            //秒杀中
            secKillStatus = 1;//当前时间在startDate之后，在endDate之前
            remainSeconds = 0;//表示秒杀进行时
        }
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("goods",goodsVo);

        //如果html为空，手动渲染页面

        //新版WebContext创建对象的方法：
        // 获取 JakartaServletWebApplication 实例（通常通过 Spring 注入）
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(request.getServletContext());
        // 创建 IWebExchange
        IWebExchange webExchange = application.buildExchange(request, response);
        WebContext context = new WebContext(
                webExchange,          // IWebExchange 对象
                request.getLocale(),  // Locale
                model.asMap()         // 模板变量 Map
        );

        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail",context);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail:"+goodsId,html,60,TimeUnit.SECONDS);
        }
        return html;
        //return "goodsDetail";
    }

    /**
     * 功能描述：跳转商品详情页
     * @param goodsId
     * @return
     */
    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(Model model, User user, @PathVariable Long goodsId){
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus = 0;//秒杀状态
        int remainSeconds = 0;//秒杀倒计时
        //秒杀还未开始
        if(nowDate.before(startDate)){//当前时间在开始时间之前:秒杀未开始
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime())/1000);
        }else if(nowDate.after(endDate)){//当前时间在结束时间之后:秒杀已结束
            //秒杀已结束
            secKillStatus = 2;
            remainSeconds = -1;//表示秒杀已结束
        }else{
            //秒杀中
            secKillStatus = 1;//当前时间在startDate之后，在endDate之前
            remainSeconds = 0;//表示秒杀进行时
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);
        return  RespBean.success(detailVo);
    }
}
