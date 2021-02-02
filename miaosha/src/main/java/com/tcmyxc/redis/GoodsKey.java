package com.tcmyxc.redis;

/**
 * @author 徐文祥
 * @date 2021/1/24 21:31
 */
public class GoodsKey extends BasePrefix{

    // 页面缓存一般有效期比较短
    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public GoodsKey(String prefix) {
        super(prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60,"goodsList");
    public static GoodsKey getGoodsDetail = new GoodsKey(60,"goodsDetail");
    public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0,"goodsStock");
}
