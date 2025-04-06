package com.meeting_smile.seckilldemo.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.meeting_smile.seckilldemo.config.AccessLimit;
import com.meeting_smile.seckilldemo.exception.GlobalException;
import com.meeting_smile.seckilldemo.pojo.*;
import com.meeting_smile.seckilldemo.rabbitmq.MQSender;
import com.meeting_smile.seckilldemo.service.IGoodsService;
import com.meeting_smile.seckilldemo.service.IOrderService;
import com.meeting_smile.seckilldemo.service.ISeckillOrderService;
import com.meeting_smile.seckilldemo.valueobject.GoodsVo;
import com.meeting_smile.seckilldemo.valueobject.RespBean;
import com.meeting_smile.seckilldemo.valueobject.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀
 */
@Slf4j
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    RedisScript<Long> script;

    //key为商品id，value为对应商品库存是否为空（true为空）
    //用于判断对应商品库存是否为空，从而减少通过对redis进行访问来判断是否为空
    private Map<Long,Boolean> EmptyStockMap = new HashMap<Long,Boolean>();
    /**
     * 功能描述：秒杀
     * windows优化前：QPS 1379.1835233541742
     *        缓存后：QPS 1404.0342584359057
     *        优化后：QPS 1756.440281030445
     *        使用Redis分布式锁（java本地写的lua脚本）会是得QPS减小，因为需要先将脚本发给redis服务端，需要耗费一定的时间
     * Linux优化前：QPS 336.50394831299354
     * @param
     * @param user
     * @param goodsId
     * @return
     */
//    @RequestMapping("/doSeckill")
//    public String dpSeckill(Model model, User user,Long goodsId){
//        if(user == null){//当用户不存在
//            return "login";
//        }
//        model.addAttribute("user",user);
//        //根据商品id拿到库存
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        if(goods.getStockCount() < 1){//如果没有库存了
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return "secKillFail";
//        }
//        //判断是否重复抢购
//        //若在秒杀商品表中根据当前用户id能查到商品id，说明用户已经抢购过商品
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",
//                user.getId()).eq("goods_id",goodsId));
//        if(seckillOrder != null){//若用户已抢购过该商品
//            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
//            return "secKillFail";
//        }
//        //用户能够抢购该商品
//        Order order = orderService.seckill(user,goods);
//        model.addAttribute("order",order);
//        model.addAttribute("goods",goods);
//        return  "orderDetail";
//    }

/*    @PostMapping("/doSeckill")
    @ResponseBody
    public RespBean dpSeckill(Model model, User user, Long goodsId){
        if(user == null){//当用户不存在
            return RespBean.error((RespBeanEnum.SESSION_ERROR));
        }
        //model.addAttribute("user",user);
        //根据商品id拿到库存
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goods.getStockCount() < 1){//如果没有库存了
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购
        //若在秒杀商品表中根据当前用户id能查到商品id，说明用户已经抢购过商品
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goods.getId());
        if(seckillOrder != null){//若用户已抢购过该商品
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //用户能够抢购该商品
        Order order = orderService.seckill(user,goods);
//        model.addAttribute("order",order);
//        model.addAttribute("goods",goods);
        return  RespBean.success(order);
    }*/

    @PostMapping("{path}/doSeckill")
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, User user, Long goodsId){
        if(user == null){//当用户不存在
            return RespBean.error((RespBeanEnum.SESSION_ERROR));
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //验证路径path是否为该用户+该商品id的秒杀路径
        Boolean check = orderService.checkPath(user,goodsId,path);
        if(!check){//秒杀路径错误
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder != null){//若用户已抢购过该商品
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //通过内存标记减少对redis的访问
        if(EmptyStockMap.get(goodsId)){//如果goodsId的商品库存为空
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //从redis中预减库存
        //decrement方法：若调用的对象为数字类型，则每调用一次就会减1，且该方法具有原子性
        //Long stock = valueOperations.decrement("seckillGoods:"+goodsId);
        //利用Redis的分布式锁与lua脚本完成库存的递减与查询操作
        Long stock = (Long) redisTemplate.execute(script,Collections.singletonList("seckillGoods:"+goodsId),Collections.EMPTY_LIST);
        if(stock < 0){//库存不足
            EmptyStockMap.put(goodsId,true);//标记id为goodsId的商品库存为空
            //小于0说明递减前值为0，因此需要再+1
            valueOperations.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user,goodsId);
        mqSender.sendSeckillMessage(JSON.toJSONString(seckillMessage));//toJSONString：将对象转化为JSON字符串
        return RespBean.success(0);//前端接收到0后会显示“正在排队中”
    }

    /**
     * 系统初始化时执行的方法，可将商品库存数量加载到Redis中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)) {//如果商品列表为空
            return;
        }
        //遍历商品列表（goodsVo表示单个商品对象）
        list.forEach(goodsVo ->{
                redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
                EmptyStockMap.put(goodsVo.getId(),false);//最开始时商品的有库存的，故此时设置为false
        }
        );
    }

    /**
     * 功能描述：获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId存在表示秒杀成功 ,-1：表示秒杀失败 ,0:表示排队中
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if(user == null){//用户为空
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 功能描述：获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    //利用注解实现个性化修改接口限流的操作
    //即在second秒内最多只能操作maxCount次，且是否需要登录才能操作
    @AccessLimit(second=5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request){
        if(user == null){//用户为空
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //接口限流：利用计数器算法
        /*ValueOperations valueOperations = redisTemplate.opsForValue();
        // 限制访问次数5:秒内只能访问五次
        String uri = request.getRequestURI();//获取请求的地址
        captcha = "0";//方便测试将验证码答案设置为0
        Integer count = (Integer) valueOperations.get(uri+":"+user.getId());
        if(count == null){//如果是第一次访问
            //将其初值设置为1（已经访问了一次），并设置失效时间为5秒（失效5秒内只能访问5次）
            valueOperations.set(uri+":"+user.getId(),1,5,TimeUnit.SECONDS);
        }else if(count < 5){
            //如果未到达访问上限，则递增
            valueOperations.increment(uri+":"+user.getId());
        }else{//超过访问上限
            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
        }*/
        //校验验证码
        Boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if(!check){
            return RespBean.error(RespBeanEnum.ERROR_CHAPTCHA);
        }
        //根据用户与商品id生成唯一的秒杀地址
        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }

    /**
     * 功能描述：生成验证码
     * @param user
     * @param goodsId
     * @param response
     */
    @RequestMapping(value = "/captcha",method = RequestMethod.GET)
    public void verifycode(User user, Long goodsId, HttpServletResponse response){
        if(user == null || goodsId < 0){//健壮性判断
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求头输出类型为输出图片的类型
        response.setContentType("image/png");
        //设置为不需要缓存，避免刷新之后获取到的是旧的验证码，确保每次获取的都是新的验证码
        response.setHeader("Pragma","No-cache");
        response.setHeader("Cache-Control","no-cache");
        //设置失效时间为永不失效
        response.setDateHeader("Expires",0);
        //生成验证码，并将其放在redis中
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130,32,3);//设置验证码的长宽高
        //将验证码存在Redis中，并设置失效时间为300s，captcha.text()为验证码的答案
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败,",e.getMessage());
        }
    }
}
