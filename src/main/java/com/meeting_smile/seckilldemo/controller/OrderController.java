package com.meeting_smile.seckilldemo.controller;


import com.meeting_smile.seckilldemo.pojo.User;
import com.meeting_smile.seckilldemo.service.IOrderService;
import com.meeting_smile.seckilldemo.valueobject.OrderDetailVo;
import com.meeting_smile.seckilldemo.valueobject.RespBean;
import com.meeting_smile.seckilldemo.valueobject.RespBeanEnum;
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
 * @since 2025-03-26
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    /**
     * 功能描述：订单详情
     */

    @Autowired
    private IOrderService orderService;

    //订单信息
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId) {
        if(user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        OrderDetailVo detail = orderService.getDetail(orderId);

        return RespBean.success(detail);

    }

}
