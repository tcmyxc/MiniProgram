package com.tcmyxc.rabbitmq;

import com.tcmyxc.domain.MiaoshaUser;

/**
 * @author 徐文祥
 * @date 2021/1/28 16:32
 */
public class MiaoshaMessage {

    private MiaoshaUser user;
    private long goodsId;

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(MiaoshaUser user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goosId) {
        this.goodsId = goosId;
    }
}
