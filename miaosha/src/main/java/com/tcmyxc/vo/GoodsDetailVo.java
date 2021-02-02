package com.tcmyxc.vo;

import com.tcmyxc.domain.MiaoshaUser;

/**
 * @author 徐文祥
 * @date 2021/1/26 21:43
 */
public class GoodsDetailVo {

    private int miaoshaStatus;// 秒杀状态
    private int remainSeconds;// 距离开始还有多少时间
    private GoodsVo goods;
    private MiaoshaUser user;

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(MiaoshaUser user) {
        this.user = user;
    }

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }
}
