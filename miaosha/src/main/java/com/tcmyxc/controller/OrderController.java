package com.tcmyxc.controller;

import com.tcmyxc.domain.MiaoshaUser;
import com.tcmyxc.domain.OrderInfo;
import com.tcmyxc.redis.RedisService;
import com.tcmyxc.result.CodeMsg;
import com.tcmyxc.result.Result;
import com.tcmyxc.service.GoodsService;
import com.tcmyxc.service.MiaoshaUserService;
import com.tcmyxc.service.OrderService;
import com.tcmyxc.vo.GoodsVo;
import com.tcmyxc.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 徐文祥
 * @date 2021/1/26 23:00
 */

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    // 可以加一个拦截器，判断 session 是否过期，避免重复劳动
    public Result<OrderDetailVo> info(Model model, MiaoshaUser user,
                                      @RequestParam("orderId") long orderId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }
}
