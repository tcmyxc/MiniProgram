package com.tcmyxc.redis;

/**
 * @author 徐文祥
 * @date 2021/1/15 1:45
 */
public class MiaoshaUserKey extends BasePrefix{

    private static final int TOKEN_EXPIRE = 3600 * 24 * 2;



    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE, "token");
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id");

    public MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
