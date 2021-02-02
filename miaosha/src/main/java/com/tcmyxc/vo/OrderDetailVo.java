package com.tcmyxc.vo;

import com.tcmyxc.domain.MiaoshaUser;
import com.tcmyxc.domain.OrderInfo;

/**
 * @author 徐文祥
 * @date 2021/1/26 23:01
 */
public class OrderDetailVo {

    private GoodsVo goods;
    private OrderInfo order;
    public GoodsVo getGoods() {
        return goods;
    }
    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }
    public OrderInfo getOrder() {
        return order;
    }
    public void setOrder(OrderInfo order) {
        this.order = order;
    }
}
