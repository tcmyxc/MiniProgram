package com.tcmyxc.redis;

/**
 * @author 徐文祥
 * @date 2021/1/28 17:13
 */
public class MiaoshaKey extends BasePrefix{

    public MiaoshaKey(String prefix) {
        super(prefix);
    }

    public MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey("goodsOver");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "miaoshaPath");
    public static MiaoshaKey getMiaoshaVerifyCode  = new MiaoshaKey(120, "miaoshaVerifyCode");
    public static MiaoshaKey access =new MiaoshaKey(5, "access");// 过期时间 5 秒


    public static MiaoshaKey withExpire(int expireSeconds){
        return new MiaoshaKey(expireSeconds, "access");
    }
}
