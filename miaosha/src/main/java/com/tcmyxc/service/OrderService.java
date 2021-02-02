package com.tcmyxc.service;

import com.tcmyxc.dao.GoodsDao;
import com.tcmyxc.dao.OrderDao;
import com.tcmyxc.domain.MiaoshaOrder;
import com.tcmyxc.domain.MiaoshaUser;
import com.tcmyxc.domain.OrderInfo;
import com.tcmyxc.redis.OrderKey;
import com.tcmyxc.redis.RedisService;
import com.tcmyxc.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author 徐文祥
 * @date 2021/1/15 21:58
 */

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    public MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(Long userId, long goodsId) {
        // 查缓存
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, "" + userId + "_" + goodsId, MiaoshaOrder.class);
        //return orderDao.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        // 创建订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0l);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setUserId(user.getId());
        orderInfo.setStatus(0);
        orderDao.insertOrder(orderInfo);

        // 创建秒杀订单
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getId() + "_" + goods.getId(), miaoshaOrder);

        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
